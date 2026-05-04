import { ReferenceType, ReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { GSRequest, RequestStatusHistory } from "../../../../common/request/GSRequest";
import { RequestStatusResponse } from "../../Requests";

export const canChangeStatus = (
  status: ReferenceType,
  availableTransitionStatuses: RequestStatusResponse | undefined,
  request: GSRequest,
) => {
  if (!availableTransitionStatuses) {
    return false;
  }

  const permittedStatus = availableTransitionStatuses.permittedStatuses.find(
    (ref) => ref.requestStatusId === status?.id,
  );

  if (!permittedStatus) {
    return false;
  }

  if (request.inStakeholderConsultation) {
    return permittedStatus.permittedDuringConsultation;
  }

  return true;
};

const requestHasPreviouslyBeenInProgress = (
  requestStatusHistory: RequestStatusHistory[],
  inProgressStatus: ReferenceType,
) => {
  return requestStatusHistory.some((sh) => sh.previousStatusId == inProgressStatus?.id); //i.e review not skipped
};

export const canChangeToInProgress = (
  isReviewComplete: boolean,
  canThisStatusChange: boolean,
  requestStatusHistory: RequestStatusHistory[],
  inProgressStatus: ReferenceType,
) => {
  const hasBeenInProgress = requestHasPreviouslyBeenInProgress(requestStatusHistory, inProgressStatus);
  return hasBeenInProgress && !isReviewComplete && canThisStatusChange;
};

export const canChangeToInPreparation = (
  requestHasIssue: boolean,
  requestIsRejected: boolean,
  accountIdPresent: boolean,
  canThisStatusChange: boolean,
  piiNotAcceptedAndRelevant: boolean,
) => {
  return (
    (requestHasIssue || piiNotAcceptedAndRelevant) && !requestIsRejected && !accountIdPresent && canThisStatusChange
  );
};

export const canChangeToInAudit = (
  isReviewComplete: boolean,
  requestHasIssue: boolean,
  requestIsRejected: boolean,
  accountIdPresent: boolean,
  canThisStatusChange: boolean,
) => {
  return isReviewComplete && requestHasIssue && !requestIsRejected && accountIdPresent && canThisStatusChange;
};

export const canChangeToCompletedRejected = (
  requestHasIssue: boolean,
  requestIsRejected: boolean,
  requestInStakeholderConsultation: boolean,
  canThisStatusChange: boolean,
  piiAcceptedAndRelevant: boolean,
) => {
  return (
    requestHasIssue &&
    requestIsRejected &&
    !requestInStakeholderConsultation &&
    canThisStatusChange &&
    piiAcceptedAndRelevant
  );
};

export const canChangeToCompletedApproved = (
  isReviewComplete: boolean,
  requestHasIssue: boolean,
  requestInStakeholderConsultation: boolean,
  canThisStatusChange: boolean,
  isPiiAcceptedAndRelevant: boolean,
) => {
  return (
    isReviewComplete &&
    !requestHasIssue &&
    !requestInStakeholderConsultation &&
    canThisStatusChange &&
    isPiiAcceptedAndRelevant
  );
};

export const getNecessaryActiveReferenceTypes = (activeReferenceTypes: ReferenceTypes) => {
  const inPreparationStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_PREPARATION",
  );
  const inAuditStatus = activeReferenceTypes.RequestStatus.find((requestStatus) => requestStatus.code === "IN_AUDIT");
  const inAssignmentStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_ASSIGNMENT",
  );
  const inProgressStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_PROGRESS",
  );
  const completedRejectedStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "COMPLETED_REJECTED",
  );
  const completedApprovedStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "COMPLETED_APPROVED",
  );
  if (
    !inPreparationStatus ||
    !inAuditStatus ||
    !inAssignmentStatus ||
    !inProgressStatus ||
    !completedRejectedStatus ||
    !completedApprovedStatus
  ) {
    throw Error("Unable to find necessary request statuses");
  }
  return {
    inPreparationStatus,
    inAuditStatus,
    inAssignmentStatus,
    inProgressStatus,
    completedRejectedStatus,
    completedApprovedStatus,
  };
};
