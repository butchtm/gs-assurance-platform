import { Box } from "@mui/material";
import React, { ChangeEvent, useCallback, useEffect, useState } from "react";

import { useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import {
  isPiiAcceptedAndRelevant,
  isPiiNotAcceptedAndRelevant,
  updateProjectPiiToNotFound,
} from "../../../../common/piiAnalysis/PiiAction";
import { ReferenceType } from "../../../../common/project/Project";
import { CreateUpdateRequestPayload, GSRequest, isPreliminaryReview } from "../../../../common/request/GSRequest";
import { handleApiError } from "../../../APIClient/APIErrorHandler";
import { updateProjectStatus } from "../../../Project/Project";
import LoadingSpinner from "../../../Reusable/LoadingSpinner";
import { RequestStatusResponse, updateRequest } from "../../Requests";
import { Assignment, getRequestAssignments } from "../../assignment/Assignment";
import PiiAcceptance from "../PiiAcceptance";
import { PiiNotFound } from "../PiiNotFound";

import {
  canChangeStatus,
  canChangeToCompletedApproved,
  canChangeToCompletedRejected,
  canChangeToInAudit,
  canChangeToInPreparation,
  canChangeToInProgress,
  getNecessaryActiveReferenceTypes,
} from "./InRoundCompletion";
import InRoundCompletionControls from "./InRoundCompletionControls";
import InRoundCompletionFormContents from "./InRoundCompletionFormContents";
import InRoundCompletionFormHeader from "./InRoundCompletionFormHeader";
import InRoundCompletionOpenFindings from "./InRoundCompletionOpenFindings";
import InRoundCompletionRejectIssue from "./InRoundCompletionRejectIssue";
import InRoundCompletionUpdateProjectStatus from "./InRoundCompletionUpdateProjectStatus";
import { ConfirmCompletionModal } from "./ConfirmCompletionModal";

interface RequestStatusFormProps {
  request: GSRequest;
  availableTransitionStatuses?: RequestStatusResponse;
  onSubmit: () => void;
}

export interface AssignmentAuditInfo {
  assignmentId: number | null;
  accountId: number;
}

const InRoundCompletionStatusForm: React.FC<RequestStatusFormProps> = ({
  request,
  availableTransitionStatuses,
  onSubmit,
}) => {
  const [chosenNextStatus, setChosenNextStatus] = useState<number>(request.requestStatus.id);
  const [chosenProjectStatus, setChosenProjectStatus] = useState<number | "">("");
  const [assignmentAuditRequestVVB, setAssignmentAuditRequestVVB] = useState<AssignmentAuditInfo>();
  const [requestHasOpenFindings, setRequestHasOpenFindings] = useState<boolean | null>(null);
  const [isReviewComplete, setIsReviewComplete] = useState<boolean | null>(null);
  const [requestIsRejected, setRequestIsRejected] = useState<boolean | null>(null);
  const [statusChangePermitted, setStatusChangePermitted] = useState<boolean>(false);
  const { activeReferenceTypes } = useReferenceTypes();
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [piiAccepted, setPiiAccepted] = useState<boolean>(false);
  const [titlePiiNotFound, setTitlePiiNotFound] = useState<boolean>(false);
  const [descriptionPiiNotFound, setDescriptionPiiNotFound] = useState<boolean>(false);
  const [notesPiiNotFound, setNotesPiiNotFound] = useState<boolean>(false);
  const [modalOpen, setModalOpen] = useState<boolean>(false);

  const completedIds = activeReferenceTypes.RequestStatus.filter((status) =>
    ["COMPLETED_APPROVED", "COMPLETED_REJECTED"].includes(status.code),
  ).map((status) => status.id);

  const isStatusChangePermitted = (status: ReferenceType) => {
    return canChangeStatus(status, availableTransitionStatuses, request);
  };

  const buildPiiNotFoundSubmission = () => {
    const projectFields = [];
    if (titlePiiNotFound) {
      projectFields.push("title");
    }
    if (descriptionPiiNotFound) {
      projectFields.push("description");
    }
    if (notesPiiNotFound) {
      projectFields.push("notes");
    }
    return projectFields;
  };

  const checkCurrentStatusChange = useCallback(() => {
    const { inPreparationStatus, inAuditStatus, inProgressStatus, completedRejectedStatus, completedApprovedStatus } =
      getNecessaryActiveReferenceTypes(activeReferenceTypes);

    const piiAcceptedAndRelevant = isPiiAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance);
    const piiNotAcceptedAndRelevant = isPiiNotAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance);

    //Request review not complete + has previous review -> IN_PROGRESS
    if (
      canChangeToInProgress(
        isReviewComplete ?? false,
        isStatusChangePermitted(inProgressStatus),
        request.requestStatusHistory,
        inProgressStatus,
      )
    ) {
      setChosenNextStatus(inProgressStatus.id);
      setStatusChangePermitted(true);
    }

    //Request has issue, no VVB -> IN PREPARATION
    if (
      canChangeToInPreparation(
        requestHasOpenFindings ?? false,
        requestIsRejected ?? false,
        !!assignmentAuditRequestVVB?.accountId,
        isStatusChangePermitted(inPreparationStatus),
        piiNotAcceptedAndRelevant,
      )
    ) {
      setChosenNextStatus(inPreparationStatus.id);
      setStatusChangePermitted(true);
    }

    //Request has issue, has audit, has VVB -> IN AUDIT
    if (
      canChangeToInAudit(
        isReviewComplete ?? false,
        requestHasOpenFindings ?? false,
        requestIsRejected ?? false,
        !!assignmentAuditRequestVVB?.accountId,
        isStatusChangePermitted(inAuditStatus),
      )
    ) {
      setChosenNextStatus(inAuditStatus.id);
      setStatusChangePermitted(true);
    }

    //Request has issue, rejected -> COMPLETE_REJECTED
    if (
      canChangeToCompletedRejected(
        requestHasOpenFindings ?? false,
        requestIsRejected ?? false,
        request.inStakeholderConsultation,
        isStatusChangePermitted(completedRejectedStatus),
        piiAcceptedAndRelevant,
      )
    ) {
      setChosenNextStatus(completedRejectedStatus.id);
      setStatusChangePermitted(true);
    }

    //Review is complete and no issue -> COMPLETE_APPROVED
    if (
      canChangeToCompletedApproved(
        isReviewComplete ?? false,
        requestHasOpenFindings ?? false,
        request.inStakeholderConsultation,
        isStatusChangePermitted(completedApprovedStatus),
        piiAcceptedAndRelevant,
      )
    ) {
      setChosenNextStatus(completedApprovedStatus.id);
      setStatusChangePermitted(true);
    }
  }, [
    activeReferenceTypes.RequestStatus,
    assignmentAuditRequestVVB?.accountId,
    isReviewComplete,
    requestHasOpenFindings,
    requestIsRejected,
    request.requestStatus.id,
    request.requestStatusHistory,
    piiAccepted,
  ]);

  useEffect(() => {
    const fetchCurrentAssignedVVB = async () => {
      const assignments = await getRequestAssignments(request.id);
      const auditAssignment = assignments.find((assignment: Assignment) => assignment.assignmentType.code === "AUDIT");

      if (auditAssignment?.id && auditAssignment.account) {
        setAssignmentAuditRequestVVB({
          assignmentId: auditAssignment.id,
          accountId: auditAssignment.account?.id,
        });
      }
    };

    fetchCurrentAssignedVVB();
    checkCurrentStatusChange();
    setIsLoading(false);
  }, [checkCurrentStatusChange, request.id]);

  const handleClickSubmit = async () => {
    setModalOpen(false);
    setIsSubmitting(true);

    const piiNotFound = buildPiiNotFoundSubmission();
    if (piiNotFound.length > 0 && isPreliminaryReview(request)) {
      await updateProjectPiiToNotFound(request.project.id, piiNotFound);
    }

    //Request has issue, close current round of review and start new one

    if (chosenNextStatus && chosenNextStatus !== 99 && request.project) {
      const requestToSubmit: Partial<CreateUpdateRequestPayload> = {
        id: request.id,
        requestStatusId: chosenNextStatus,
      };

      try {
        await updateRequest(requestToSubmit, request.project.id);
      } catch (error) {
        handleApiError(error);
      } finally {
        onSubmit();
        setIsSubmitting(false);
      }
    }

    if (chosenProjectStatus && request.project) {
      const projectToUpdate = {
        id: chosenProjectStatus,
        requestId: request.id,
      };
      await updateProjectStatus(projectToUpdate, request.project.id);
    }
  };

  const handleReviewCompleteChange = (event: ChangeEvent<HTMLInputElement>) => {
    setRequestHasOpenFindings(null);
    if (event.target.value === "true") {
      setIsReviewComplete(true);
    } else {
      setIsReviewComplete(false);
    }
  };

  const showPiiAcceptance =
    Boolean(request.project.piiMetadata?.piiPresent) && (isReviewComplete ?? false) && isPreliminaryReview(request);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <>
      <Box display="flex" flexDirection="column" p={2}>
        <InRoundCompletionFormHeader assignmentAuditRequestVVB={assignmentAuditRequestVVB} request={request} />

        {!request.inStakeholderConsultation && (
          <InRoundCompletionFormContents
            isReviewComplete={isReviewComplete}
            handleReviewCompleteChange={handleReviewCompleteChange}
          />
        )}

        {request.project.piiMetadata?.piiPresent && isReviewComplete && isPreliminaryReview(request) && (
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

        {showPiiAcceptance && <PiiAcceptance piiAccepted={piiAccepted} setPiiAccepted={setPiiAccepted} />}

        {isReviewComplete &&
          !request.inStakeholderConsultation &&
          isPiiAcceptedAndRelevant(request, piiAccepted, showPiiAcceptance) && (
            <InRoundCompletionOpenFindings
              requestHasOpenFindings={requestHasOpenFindings}
              setRequestHasOpenFindings={setRequestHasOpenFindings}
            />
          )}

        {isReviewComplete && requestHasOpenFindings && (
          <InRoundCompletionRejectIssue
            requestIsRejected={requestIsRejected}
            setRequestIsRejected={setRequestIsRejected}
          />
        )}

        {isReviewComplete && !requestHasOpenFindings && request.requestType.code === "GENERIC" && (
          <InRoundCompletionUpdateProjectStatus
            chosenProjectStatus={chosenProjectStatus}
            setChosenProjectStatus={setChosenProjectStatus}
          />
        )}

        <InRoundCompletionControls
          statusChangePermitted={statusChangePermitted}
          chosenNextStatus={chosenNextStatus}
          requestHasOpenFindings={requestHasOpenFindings}
          requestIsRejected={requestIsRejected}
          request={request}
          handleClickSubmit={completedIds.includes(chosenNextStatus) ? () => setModalOpen(true) : handleClickSubmit}
          isReviewComplete={isReviewComplete}
        />

        {isSubmitting && <LoadingSpinner />}
      </Box>
      {modalOpen && (
        <ConfirmCompletionModal
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          onConfirm={handleClickSubmit}
          selectedStatus={
            activeReferenceTypes.RequestStatus.find((status) => status.id == chosenNextStatus)?.description
          }
        />
      )}
    </>
  );
};

export default InRoundCompletionStatusForm;
