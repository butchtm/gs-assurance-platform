import {
  Box,
  Button,
  Checkbox,
  Divider,
  FormControl,
  FormControlLabel,
  FormGroup,
  FormLabel,
  Radio,
  RadioGroup,
  Typography,
} from "@mui/material";
import React, { ChangeEvent, useCallback, useEffect, useState } from "react";

import { ReferenceType, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { isPiiAcceptedAndRelevant, isPiiNotAcceptedAndRelevant } from "../../../common/piiAnalysis/PiiAction";
import {
  CreateUpdateRequestPayload,
  GSRequest,
  isPreliminaryReview,
  RequestStatusHistory,
} from "../../../common/request/GSRequest";
import { handleApiError } from "../../APIClient/APIErrorHandler";
import LoadingSpinner from "../../Reusable/LoadingSpinner";
import { getRequestTypeStakeHolderConsultationRequired, updateRequest } from "../Requests";
import { Assignment, getRequestAssignments } from "../assignment/Assignment";

import PiiAcceptance from "./PiiAcceptance";
import RequestStatusChangeDisplay from "./RequestStatusChangeDisplay";
import { PiiNotFound } from "./PiiNotFound";

interface RequestStatusFormProps {
  request: GSRequest;
  onSubmit: () => void;
}

interface AssignmentAuditInfo {
  assignmentId: number | null;
  accountId: number;
}

const InCompletenessStatusForm: React.FC<RequestStatusFormProps> = ({ request, onSubmit }) => {
  const [chosenNextStatus, setChosenNextStatus] = useState<number>(request.requestStatus.id);
  const [assignmentAuditRequestVVB, setAssignmentAuditRequestVVB] = useState<AssignmentAuditInfo>();
  const [isRequestComplete, setIsRequestComplete] = useState<boolean>(false);
  const [isStakeholderConsultationRequired, setIsStakeholderConsultationRequired] = useState<string>();
  const [startStakeholderConsultation, setStartStakeholderConsultation] = useState<boolean>(false);
  const [canFastTrack, setCanFastTrack] = useState<boolean>(false);
  const [fastTrack, setFastTrack] = useState<boolean>(true);
  const [canSetInternalExternal, setCanSetInternalExternal] = useState<boolean>(false);
  const [internal, setInternal] = useState<boolean | undefined>(true);
  const [piiAccepted, setPiiAccepted] = useState<boolean>(false);
  const [titlePiiNotFound, setTitlePiiNotFound] = useState<boolean>(false);
  const [descriptionPiiNotFound, setDescriptionPiiNotFound] = useState<boolean>(false);
  const [notesPiiNotFound, setNotesPiiNotFound] = useState<boolean>(false);

  const { activeReferenceTypes } = useReferenceTypes();
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const allRequestTypesBackToInAudit = (inAuditStatus: ReferenceType | undefined) => {
    if (!isRequestComplete && assignmentAuditRequestVVB?.accountId && !isPreliminaryReview(request)) {
      if (inAuditStatus) setChosenNextStatus(inAuditStatus.id);
    }
  };

  const otherRequestTypesBackToInPreparation = (
    auditCompletedStatusHistory: RequestStatusHistory[],
    inPreparationStatus: ReferenceType | undefined,
  ) => {
    if (!isRequestComplete && (!auditCompletedStatusHistory.length || !assignmentAuditRequestVVB)) {
      if (inPreparationStatus) setChosenNextStatus(inPreparationStatus.id);
    }
  };

  const preliminaryReviewBackToInPreparation = (
    auditCompletedStatusHistory: RequestStatusHistory[],
    piiNotAcceptedAndRelevant: boolean,
    inPreparationStatus: ReferenceType | undefined,
  ) => {
    if (
      isRequestComplete &&
      (!auditCompletedStatusHistory.length || !assignmentAuditRequestVVB) &&
      piiNotAcceptedAndRelevant
    ) {
      if (inPreparationStatus) setChosenNextStatus(inPreparationStatus.id);
    }
  };

  const readyForInAssignment = (inAssignmentStatus: ReferenceType | undefined, piiAcceptedAndRelevant: boolean) => {
    if (isRequestComplete && piiAcceptedAndRelevant) {
      if (inAssignmentStatus) setChosenNextStatus(inAssignmentStatus.id);
    }
  };

  const checkCurrentStatusChange = useCallback(() => {
    const inPreparationStatus = activeReferenceTypes.RequestStatus.find(
      (requestStatus) => requestStatus.code === "IN_PREPARATION",
    );
    const inAuditStatus = activeReferenceTypes.RequestStatus.find((requestStatus) => requestStatus.code === "IN_AUDIT");
    const inAssignmentStatus = activeReferenceTypes.RequestStatus.find(
      (requestStatus) => requestStatus.code === "IN_ASSIGNMENT",
    );

    const piiAcceptedAndRelevant = isPiiAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance);
    const piiNotAcceptedAndRelevant = isPiiNotAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance);

    //Request Complete -> IN ASSIGNMENT
    readyForInAssignment(inAssignmentStatus, piiAcceptedAndRelevant);

    //Checking if Request has been in Audit before
    const auditCompletedStatusHistory =
      request.requestStatusHistory?.filter((status) => status.previousStatusId === inAuditStatus?.id) ?? [];

    //Request complete, pii not accepted, no audit, no VVB -> IN PREPARATION
    preliminaryReviewBackToInPreparation(auditCompletedStatusHistory, piiNotAcceptedAndRelevant, inPreparationStatus);

    //Request not complete, no audit, no VVB -> IN PREPARATION
    otherRequestTypesBackToInPreparation(auditCompletedStatusHistory, inPreparationStatus);

    //Request not complete, has VVB -> IN AUDIT
    allRequestTypesBackToInAudit(inAuditStatus);
  }, [
    activeReferenceTypes.RequestStatus,
    assignmentAuditRequestVVB?.accountId,
    isRequestComplete,
    request.requestStatusHistory,
    piiAccepted,
  ]);

  useEffect(() => {
    const fetchCurrentAssignedVVB = async () => {
      const assignments = await getRequestAssignments(request.id);
      if (assignments) {
        const auditAssignment = assignments.find(
          (assignment: Assignment) => assignment.assignmentType.code === "AUDIT",
        );

        if (auditAssignment?.id && auditAssignment.account) {
          setAssignmentAuditRequestVVB({
            assignmentId: auditAssignment.id,
            accountId: auditAssignment.account?.id,
          });
        }
      }
    };

    const fetchStakeholderRequired = async () => {
      const stakeHolderMappings = await getRequestTypeStakeHolderConsultationRequired();
      if (stakeHolderMappings) {
        const currentTypeMapping = stakeHolderMappings.find(
          (mapping) => mapping.requestTypeId == request.requestType.id,
        );
        if (currentTypeMapping?.requiresConsultation === true) {
          setIsStakeholderConsultationRequired("true");
          setStartStakeholderConsultation(true);
        }
        if (currentTypeMapping?.requiresConsultation === false) {
          setIsStakeholderConsultationRequired("false");
          setStartStakeholderConsultation(false);
        }
        if (currentTypeMapping?.requiresConsultation === null) {
          setIsStakeholderConsultationRequired("optional");
        }
      }
    };

    fetchCurrentAssignedVVB();
    fetchStakeholderRequired();

    const canFastTrackFlag = isPreliminaryReview(request) && isRequestComplete;
    setCanFastTrack(canFastTrackFlag);
    setCanSetInternalExternal(canFastTrackFlag ? !fastTrack : true);

    checkCurrentStatusChange();
  }, [checkCurrentStatusChange, request.id, fastTrack]);

  const handleClickSubmit = async () => {
    setIsSubmitting(true);
    if (chosenNextStatus) {
      const requestToSubmit: Partial<CreateUpdateRequestPayload> = {
        id: request.id,
        requestStatusId: chosenNextStatus,
        startStakeholderConsultation: isRequestComplete ? startStakeholderConsultation : false,
        fastTrack: canFastTrack ? fastTrack : undefined,
        internal: canSetInternalExternal && isRequestComplete ? internal : undefined,
      };

      if (request.project) {
        try {
          await updateRequest(requestToSubmit, request.project?.id);
        } catch (error) {
          handleApiError(error);
        } finally {
          onSubmit();
          setIsSubmitting(false);
        }
      }
    }
  };

  const handleIsCompleteRadioChange = (event: ChangeEvent<HTMLInputElement>) => {
    setIsRequestComplete(event.target.value === "true");
  };

  const handleStakeholderConsultationChange = (event: ChangeEvent<HTMLInputElement>) => {
    setStartStakeholderConsultation(event.target.checked);
  };

  const handleFastTrackChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFastTrack(event.target.checked);
  };

  const handleInternalExternalChange = (event: ChangeEvent<HTMLInputElement>) => {
    setInternal(event.target.value === "true");
  };

  const showPiiAcceptance =
    Boolean(request.project.piiMetadata?.piiPresent) && isRequestComplete && isPreliminaryReview(request);

  return (
    <Box display="flex" flexDirection="column" p={2}>
      <Box mb={2}>
        <Typography variant="body1" data-testid="has-vvb-assigned">
          <strong>Has VVB Assigned:</strong> {assignmentAuditRequestVVB?.accountId ? "Yes" : "No"}
        </Typography>
        <Typography variant="body1">
          <strong>Stakeholder Consultation Required:</strong>
          {(isStakeholderConsultationRequired == "true" && "Yes") ||
            (isStakeholderConsultationRequired == "false" && "No") ||
            (isStakeholderConsultationRequired == "optional" && "Optional")}
        </Typography>

        <Divider style={{ margin: "20px 0" }} />
      </Box>

      <Box mb={2}>
        <FormControl>
          <FormLabel>Is this Request Complete?</FormLabel>
          <RadioGroup
            name="is-complete"
            value={isRequestComplete ? "true" : "false"}
            onChange={handleIsCompleteRadioChange}
          >
            <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="request-complete-yes" />
            <FormControlLabel value="false" control={<Radio />} label="No" data-testid="review-complete-no" />
          </RadioGroup>
        </FormControl>
      </Box>

      {request.project.piiMetadata?.piiPresent && isRequestComplete && isPreliminaryReview(request) && (
        <PiiNotFound
          piiMetadata={request.project.piiMetadata}
          titlePiiNotFound={titlePiiNotFound}
          descriptionPiiNotFound={descriptionPiiNotFound}
          notesPiiNotFound={notesPiiNotFound}
          setTitlePiiNotFound={setTitlePiiNotFound}
          setDescriptionPiiNotFound={setDescriptionPiiNotFound}
          setNotesPiiNotFound={setNotesPiiNotFound}
        />
      )}

      {showPiiAcceptance && (
        <PiiAcceptance
          piiAccepted={piiAccepted}
          setPiiAccepted={setPiiAccepted}
          setFastTrack={setFastTrack}
          setInternal={setInternal}
        />
      )}

      {isStakeholderConsultationRequired === "optional" &&
        isRequestComplete &&
        isPiiAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance) && (
          <Box mb={2}>
            <FormGroup>
              <FormControlLabel
                control={
                  <Checkbox checked={startStakeholderConsultation} onChange={handleStakeholderConsultationChange} />
                }
                label="Start a Stakeholder Consultation?"
                data-testid="stakeholder-consultation-checkbox"
              />
            </FormGroup>
          </Box>
        )}

      {canFastTrack && isRequestComplete && isPiiAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance) && (
        <Box mb={2}>
          <FormGroup>
            <FormControlLabel
              control={<Checkbox checked={fastTrack} onChange={handleFastTrackChange} />}
              label="Fast track this request?"
              data-testid="fast-track-checkbox"
            />
          </FormGroup>
        </Box>
      )}

      {canSetInternalExternal &&
        isRequestComplete &&
        isPiiAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance) && (
          <Box mb={2}>
            <FormControl>
              <FormLabel>Set this request to:</FormLabel>
              <RadioGroup value={internal} onChange={handleInternalExternalChange}>
                <FormControlLabel value="true" control={<Radio />} label="Internal" data-testid="internal-radio" />
                <FormControlLabel value="false" control={<Radio />} label="External" data-testid="external-radio" />
              </RadioGroup>
            </FormControl>
          </Box>
        )}

      <RequestStatusChangeDisplay chosenNextStatus={chosenNextStatus} />

      <Box mt={2} alignItems={"center"}>
        <Button onClick={handleClickSubmit} variant="contained" color="primary">
          Submit
        </Button>
      </Box>
      {isSubmitting && <LoadingSpinner />}
    </Box>
  );
};

export default InCompletenessStatusForm;
