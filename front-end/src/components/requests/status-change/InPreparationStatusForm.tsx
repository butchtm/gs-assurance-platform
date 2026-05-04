import {
  Box,
  Button,
  FormControl,
  FormControlLabel,
  FormLabel,
  InputLabel,
  MenuItem,
  Radio,
  RadioGroup,
  Select,
  SelectChangeEvent,
  Typography,
} from "@mui/material";
import React, { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { CreateUpdateRequestPayload, GSRequest, hasNoRounds } from "../../../common/request/GSRequest";
import { handleApiError } from "../../APIClient/APIErrorHandler";
import { getAllRequestForms, RequestForm } from "../../Forms/Forms";
import { PlatformAccount } from "../../PlatformAccounts/PlatformAccountTypes";
import LoadingSpinner from "../../Reusable/LoadingSpinner";
import {
  assignChecksToVVBorARMTeam,
  AssignmentDto,
  getAllAccountsByAssignmentType,
  getRequestAssignments,
} from "../assignment/Assignment";
import { MandatoryStatus } from "../MandatoryStatus";
import { checkIfAssignmentTypeRequiredForRequestType, updateRequest } from "../Requests";

import RequestStatusChangeDisplay from "./RequestStatusChangeDisplay";

interface RequestStatusFormProps {
  request: GSRequest;
  onSubmit: () => void;
}

interface AssignmentAuditInfo {
  assignmentId: number | null;
  accountId: number | null;
}

const InPreparationStatusForm: React.FC<RequestStatusFormProps> = ({ request, onSubmit }) => {
  const [chosenNextStatus, setChosenNextStatus] = useState<number>(request.requestStatus.id);
  const [isAuditRequired, setIsAuditRequired] = useState<MandatoryStatus>(MandatoryStatus.NOT_REQUIRED);
  const [optionalAuditRequired, setOptionalAuditRequired] = useState<boolean>(false);
  const [completenessExists, setCompletenessExists] = useState<boolean>(false);
  const [hasVVBAssigned, setHasVVBAssigned] = useState<boolean>(false);
  const [assignmentAuditRequestVVB, setAssignmentAuditRequestVVB] = useState<AssignmentAuditInfo>();
  const [assignmentCompletenessRequestARM, setAssignmentCompletenessRequestARM] = useState<AssignmentAuditInfo>();
  const [auditAccounts, setAuditAccounts] = useState<PlatformAccount[]>();
  const { activeReferenceTypes } = useReferenceTypes();
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [requestForms, setRequestForms] = useState<RequestForm[]>([]);

  const inAssignmentStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_ASSIGNMENT",
  );

  const inProgressStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_PROGRESS",
  );

  const inRoundCompletionStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_ROUND_COMPLETION",
  );

  const requestStatusHistory = request.requestStatusHistory;

  const requestHasPreviousRoundOfReview = () => {
    return request.currentRoundIndex && request.currentRoundIndex >= 1;
  };

  const requestHasPreviouslyBeenInProgress = () => {
    return requestStatusHistory.some((sh) => sh.previousStatusId == inProgressStatus?.id);
  };

  const setNewRoundOfReviewValues = () => {
    setCompletenessExists(true);

    if (request.fastTracked && inRoundCompletionStatus) {
      setChosenNextStatus(inRoundCompletionStatus.id);
      return;
    }

    if (requestHasPreviouslyBeenInProgress() && inProgressStatus) {
      setChosenNextStatus(inProgressStatus.id);
      return;
    }

    if (inAssignmentStatus) {
      setChosenNextStatus(inAssignmentStatus.id);
    }
  };

  const isAuditRequiredOrRequested = () => {
    return isAuditRequired === MandatoryStatus.REQUIRED || optionalAuditRequired;
  };

  const setStatusForAuditOrCompletenessCheck = () => {
    const completenessCheckStatus = activeReferenceTypes.RequestStatus.find(
      (requestStatus) => requestStatus.code === "COMPLETENESS_CHECK",
    );
    if (request.fastTracked && inRoundCompletionStatus && request.requestStatus.code === "IN_AUDIT") {
      setChosenNextStatus(inRoundCompletionStatus.id);
      return;
    }

    const inAuditStatus = activeReferenceTypes.RequestStatus.find((requestStatus) => requestStatus.code === "IN_AUDIT");
    if (isAuditRequiredOrRequested() && inAuditStatus) {
      setChosenNextStatus(inAuditStatus.id);
    } else if (completenessCheckStatus) {
      setChosenNextStatus(completenessCheckStatus.id);
    }
  };

  const checkCurrentStatusChange = () =>
    requestHasPreviousRoundOfReview() ? setNewRoundOfReviewValues() : setStatusForAuditOrCompletenessCheck();

  useEffect(() => {
    checkCurrentStatusChange();
  }, [
    activeReferenceTypes.RequestStatus,
    assignmentAuditRequestVVB,
    isAuditRequired,
    optionalAuditRequired,
    request.currentRoundIndex,
    request.requestStatus.code,
  ]);

  const auditAssignmentTypeId = activeReferenceTypes.AssignmentType.find((assig) => assig.code == "AUDIT")?.id;

  useEffect(() => {
    const getRequestForms = async () => {
      const formsResponse = await getAllRequestForms(request.id);
      setRequestForms(formsResponse);
    };
    getRequestForms();
  }, [request.id]);

  useEffect(() => {
    const fetchAuditAccounts = async () => {
      const fetchedAccountsForAudit = await getAllAccountsByAssignmentType(0);
      if (fetchedAccountsForAudit) {
        setAuditAccounts(fetchedAccountsForAudit);
      }
    };

    const checkIfAuditRequired = async () => {
      let auditRequired = MandatoryStatus.NOT_REQUIRED;

      if (auditAssignmentTypeId != undefined) {
        auditRequired = await checkIfAssignmentTypeRequiredForRequestType(
          request.requestType.id,
          auditAssignmentTypeId,
        );
      }
      setIsAuditRequired(auditRequired);
      if (auditRequired !== MandatoryStatus.NOT_REQUIRED) {
        fetchAuditAccounts();
      }
    };

    if (hasNoRounds(request)) {
      checkIfAuditRequired();
    }
  }, [request.requestType.id]);

  useEffect(() => {
    const fetchCurrentAssigned = async () => {
      const assignments = await getRequestAssignments(request.id);
      const auditAssignment = assignments.find((assignment) => assignment.assignmentType.code === "AUDIT");
      const completenessAssignment = assignments.find(
        (assignment) => assignment.assignmentType.code === "COMPLETENESS_CHECK",
      );

      if (auditAssignment?.id && auditAssignment?.account?.id) {
        setHasVVBAssigned(true);
        setAssignmentAuditRequestVVB({
          assignmentId: auditAssignment.id,
          accountId: auditAssignment.account?.id,
        });
      }

      if (completenessAssignment?.id) {
        setAssignmentCompletenessRequestARM({
          assignmentId: completenessAssignment.id,
          accountId: null,
        });
      }
    };

    fetchCurrentAssigned();
    checkCurrentStatusChange();
  }, [request.id]);

  const handleSelectVVBChange = (event: SelectChangeEvent) => {
    const chosenAccountId = Number(event.target.value);
    setAssignmentAuditRequestVVB({
      assignmentId: assignmentAuditRequestVVB?.assignmentId ?? null,
      accountId: chosenAccountId,
    });
  };

  const triggerAssignRequestToVVB = async () => {
    const auditType = activeReferenceTypes.AssignmentType.find((at) => at.code === "AUDIT");
    const assignmentPendingStatus = activeReferenceTypes.AssignmentStatus.find((as) => as.code === "PENDING");

    if (assignmentAuditRequestVVB && !hasVVBAssigned) {
      const assignmentPayload: AssignmentDto = {
        requestId: request.id,
        accountId: assignmentAuditRequestVVB.accountId,
        assignmentTypeId: auditType?.id,
        assignmentStatusId: assignmentPendingStatus?.id,
      };

      await assignChecksToVVBorARMTeam(assignmentAuditRequestVVB.assignmentId, assignmentPayload);
    }
  };

  const triggerAssignRequestToARMTeam = async () => {
    const completenessType = activeReferenceTypes.AssignmentType.find((at) => at.code === "COMPLETENESS_CHECK");
    const assignmentAssignedStatus = activeReferenceTypes.AssignmentStatus.find((as) => as.code === "ACCEPTED");

    if (!completenessExists) {
      const assignmentPayload: AssignmentDto = {
        requestId: request.id,
        accountId: 0, //set by the backend
        assignmentTypeId: completenessType?.id,
        assignmentStatusId: assignmentAssignedStatus?.id,
      };

      try {
        await assignChecksToVVBorARMTeam(assignmentCompletenessRequestARM?.assignmentId ?? null, assignmentPayload);
      } catch (error) {
        console.error("Error assigning to ARM team:", error);
      }
    }
  };

  const handleClickSubmit = async () => {
    setIsSubmitting(true);
    const tasks = [];

    if (assignmentAuditRequestVVB) {
      tasks.push(triggerAssignRequestToVVB());
    }

    tasks.push(triggerAssignRequestToARMTeam());

    await Promise.all(tasks);

    const requestToSubmit: Partial<CreateUpdateRequestPayload> = {
      id: request.id,
      requestStatusId: chosenNextStatus,
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
  };

  const handleOptionalAuditRadioChange = () => {
    setOptionalAuditRequired((prevValue) => !prevValue);
    setAssignmentAuditRequestVVB(undefined);
  };

  const getAuditRequiredString = (status: MandatoryStatus): string => {
    switch (status) {
      case MandatoryStatus.REQUIRED:
        return "Yes";
      case MandatoryStatus.OPTIONAL:
        return "Optional";
      case MandatoryStatus.NOT_REQUIRED:
        return "No";
      default:
        throw new Error("Unknown MandatoryStatus");
    }
  };

  const hasFormsInDraftStatus = (): boolean => {
    const draftStatus = activeReferenceTypes.RequestFormStatus?.find((status) => status.code === "DRAFT");
    if (!draftStatus) return false;
    return requestForms.some((form) => form.status.id === draftStatus.id);
  };

  const isAuditRequiredOrRequestedAndNotAssigned = () => {
    return isAuditRequiredOrRequested() && !assignmentAuditRequestVVB;
  };

  const isSubmitDisabled = (): boolean => {
    return isAuditRequiredOrRequestedAndNotAssigned() || hasFormsInDraftStatus();
  };

  return (
    <Box display="flex" flexDirection="column" p={2}>
      <FormControl component="fieldset">
        <>
          <Typography style={{ marginBottom: "2em" }}>
            Audit Required: {getAuditRequiredString(isAuditRequired)}
          </Typography>

          {isAuditRequired == MandatoryStatus.OPTIONAL && (
            <Box mb={2}>
              <FormControl>
                <FormLabel>Should this request be audited?</FormLabel>
                <RadioGroup name="assigned" value={optionalAuditRequired} onChange={handleOptionalAuditRadioChange}>
                  <FormControlLabel data-testid="optionalAuditTrue" value="true" control={<Radio />} label="Yes" />
                  <FormControlLabel data-testid="optionalAuditFalse" value="false" control={<Radio />} label="No" />
                </RadioGroup>
              </FormControl>
            </Box>
          )}

          {(isAuditRequired == MandatoryStatus.REQUIRED || optionalAuditRequired) && (
            <Box mb={2}>
              <FormControl fullWidth>
                <InputLabel id="vvb-select-label">Select VVB</InputLabel>
                <Select
                  labelId="vvb-select-label"
                  id="vvb-selection"
                  value={assignmentAuditRequestVVB?.accountId ? assignmentAuditRequestVVB.accountId.toString() : ""}
                  label="Select VVB"
                  onChange={handleSelectVVBChange}
                  data-testid="vvb-select"
                >
                  {auditAccounts?.map((account) => (
                    <MenuItem key={account.id} value={account.id} data-testid={`vvb-option-${account.id}`}>
                      {account.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Box>
          )}
        </>
      </FormControl>

      {hasFormsInDraftStatus() ? (
        <Typography sx={{ color: "red" }}>
          One or more request forms have a draft status. Please complete and submit all forms before proceeding.
        </Typography>
      ) : (
        <>
          <RequestStatusChangeDisplay chosenNextStatus={chosenNextStatus} />
          <Box mt={2}>
            <Button disabled={isSubmitDisabled()} onClick={handleClickSubmit} variant="contained" color="primary">
              Submit
            </Button>
          </Box>
        </>
      )}
      {isSubmitting && <LoadingSpinner />}
    </Box>
  );
};

export default InPreparationStatusForm;
