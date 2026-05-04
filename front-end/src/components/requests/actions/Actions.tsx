import { RequestActivity } from "../../../common/request/GSRequest";
import { AuthContextType } from "../../Auth/Login/AuthProvider";
import { Assignment } from "../assignment/Assignment";
import { isEndConsultationEnabled } from "../../../common/env/env";
import { ReferenceType } from "../../../common/data-types/DataReferenceTypes";

export const enum Action {
  SubmitRequest,
  CancelRequest,
  UpdateRequest,
  UploadDocument,
  UpdateAssignments,
  UpdateRorDates,
  CreateComment,
  EndConsultation,
  CreateInvoice,
  UpdateInvoice,
  AcceptDeclineAssignment,
  CloseActivity,
  ApproveRequest,
  OpenConversation,
  ViewConversation,
}

const canUpdateOrCreateInvoice = (authContext: AuthContextType) => {
  return authContext.isFinanceUser() || authContext.isAdminUser();
};

const findAssignment = (assignments: Assignment[], typeCode: string, statusCodes: string[]): Assignment | undefined => {
  if (!assignments) {
    return undefined;
  }
  return assignments.find((a) => a.assignmentType.code === typeCode && statusCodes.includes(a.assignmentStatus.code));
};

const hasAssignment = (assignments: Assignment[], assignmentType: string, assignmentStatus: string): boolean => {
  return findAssignment(assignments, assignmentType, [assignmentStatus]) !== undefined;
};

export const hasReviewerOrRegistryAssignment = (assignments: Assignment[]) => {
  return assignments.some((assignment) => assignment.assignmentType?.code === "REVIEW" && assignment.account != null);
};

const canAcceptOrDeclineAssignment = (authContext: AuthContextType, assignments: Assignment[]) => {
  if (authContext.isVvbUser() && hasAssignment(assignments, "AUDIT", "PENDING")) {
    return true;
  }
  return (
    (authContext.isReviewerUser() || authContext.isRegistryUser()) && hasAssignment(assignments, "REVIEW", "PENDING")
  );
};

const isClosed = (requestStatusCode: string) => {
  const closedStatuses = ["COMPLETED_APPROVED", "COMPLETED_REJECTED", "CANCELLED", "OMITTED", "NOT_REQUESTED"];
  return closedStatuses.includes(requestStatusCode);
};

const canSubmitRequest = (authContext: AuthContextType, requestStatusCode: string) => {
  if (isClosed(requestStatusCode)) {
    return false;
  }
  if (authContext.isAdminUser()) {
    return true;
  }
  if (authContext.isProjectDeveloperUser() && requestStatusCode === "IN_PREPARATION") {
    return true;
  }
  if (authContext.isVvbUser() && requestStatusCode === "IN_AUDIT") {
    return true;
  }
  if ((authContext.isReviewerUser() || authContext.isRegistryUser()) && requestStatusCode === "IN_PROGRESS") {
    return true;
  }
  const ARMRequestStatuses = ["COMPLETENESS_CHECK", "IN_ASSIGNMENT", "IN_ROUND_COMPLETION"];
  return authContext.isArmUser() && ARMRequestStatuses.includes(requestStatusCode);
};

const shouldCheckAcceptedAssignments = (authContext: AuthContextType) => {
  return authContext.isVvbUser() || authContext.isReviewerUser() || authContext.isRegistryUser();
};

const anyAcceptedAssignments = (authContext: AuthContextType, assignments: Assignment[], requestStatusCode: string) => {
  if (authContext.isVvbUser()) {
    return hasAssignment(assignments, "AUDIT", "ACCEPTED") && requestStatusCode === "IN_AUDIT";
  }
  if (authContext.isReviewerUser() || authContext.isRegistryUser()) {
    return hasAssignment(assignments, "REVIEW", "ACCEPTED") && requestStatusCode === "IN_PROGRESS";
  }
  return false;
};

const canUpdateRequest = (authContext: AuthContextType, requestStatusCode: string) => {
  if (authContext.isAdminUser()) {
    return true;
  }
  if (isClosed(requestStatusCode)) {
    return false;
  }
  if (authContext.isProjectDeveloperUser() && requestStatusCode === "IN_PREPARATION") {
    return true;
  }
  return (
    authContext.isArmUser() || authContext.isVvbUser() || authContext.isReviewerUser() || authContext.isReviewerUser()
  );
};

const canCancelRequest = (
  authContext: AuthContextType,
  requestStatusCode: string,
  requestInStakeholderConsultation: boolean,
) => {
  if (isClosed(requestStatusCode)) {
    return false;
  }
  if (authContext.isProjectDeveloperUser() && requestStatusCode === "IN_PREPARATION") {
    return true;
  }
  if (
    ["IN_ASSIGNMENT", "IN_PROGRESS", "IN_ROUND_COMPLETION"].includes(requestStatusCode) &&
    requestInStakeholderConsultation
  ) {
    return false;
  }
  return authContext.isAdminUser() || authContext.isArmUser() || authContext.isVvbUser();
};

const canUpdateAssignments = (authContext: AuthContextType) => {
  return authContext.isAdminUser() || authContext.isArmUser();
};

const canUpdateRorDates = (authContext: AuthContextType, requestStatusCode: string) => {
  if (isClosed(requestStatusCode)) {
    return false;
  }

  return authContext.isArmUser() && authContext.isAdminUser();
};

const canUploadDocument = (authContext: AuthContextType, assignments: Assignment[], requestStatusCode: string) => {
  if (authContext.isAdminUser() || authContext.isArmUser()) {
    return true;
  }
  if (isClosed(requestStatusCode)) {
    return false;
  }
  if (authContext.isProjectDeveloperUser() && requestStatusCode === "IN_PREPARATION") {
    return true;
  }
  if (authContext.isReviewerUser() && requestStatusCode === "IN_PROGRESS") {
    return hasAssignment(assignments, "REVIEW", "ACCEPTED");
  }
  return authContext.isVvbUser() || authContext.isRegistryUser();
};

const canEndConsultation = (authContext: AuthContextType, inConsultation: boolean) => {
  if (!inConsultation || !isEndConsultationEnabled()) {
    return false;
  }
  return authContext.isAdminUser() || authContext.isArmUser();
};

const canCreateComment = (authContext: AuthContextType, inConsultation: boolean) => {
  return inConsultation && authContext.isTacUser();
};

const canCloseActivity = (
  authContext: AuthContextType,
  requestStatusCode: string,
  latestRequestActivity: RequestActivity | undefined,
) => {
  if (isClosed(requestStatusCode) || !latestRequestActivity) {
    return false;
  }
  if (latestRequestActivity.endDate != null || latestRequestActivity.name != "Assigning for CC") {
    return false;
  }
  return authContext.isAdminUser() || authContext.isArmUser();
};

const getNotRequestedStatusAvailableActions = (authContext: AuthContextType): Action[] => {
  const availableActions = new Array<Action>();
  if (authContext.isAdminUser() || authContext.isArmUser()) {
    availableActions.push(Action.UploadDocument);
    availableActions.push(Action.ApproveRequest);
  }
  return availableActions;
};

const canOpenConversation = (
  authContext: AuthContextType,
  requestStatusCode: string,
  conversationStatus?: ReferenceType,
): boolean => {
  if (isClosed(requestStatusCode)) {
    return false;
  }
  return (authContext.isAdminUser() || authContext.isArmUser()) && !conversationStatus;
};

const canViewConversation = (
  authContext: AuthContextType,
  assignments: Assignment[],
  accountId: number,
  conversationStatus?: ReferenceType,
): boolean => {
  const auditAssignment = findAssignment(assignments, "AUDIT", ["ACCEPTED", "PENDING"]);
  const isRelevantVVB =
    authContext.isVvbUser() && !!auditAssignment?.account && authContext.isUserOwnerOf(auditAssignment.account?.id);
  const isRelevantPD = authContext.isProjectDeveloperUser() && !!accountId && authContext.isUserOwnerOf(accountId);
  return (
    (authContext.isAdminUser() || authContext.isArmUser() || isRelevantPD || isRelevantVVB) && !!conversationStatus
  );
};

const getConsultationActions = (authContext: AuthContextType, requestInStakeholderConsultation: boolean) => {
  const actions: Action[] = [];
  if (canEndConsultation(authContext, requestInStakeholderConsultation)) {
    actions.push(Action.EndConsultation);
  }

  if (canCreateComment(authContext, requestInStakeholderConsultation)) {
    actions.push(Action.CreateComment);
  }
  return actions;
};

const getRequestActions = (
  authContext: AuthContextType,
  requestStatusCode: string,
  requestInStakeholderConsultation: boolean,
) => {
  const actions: Action[] = [];
  if (canSubmitRequest(authContext, requestStatusCode)) {
    actions.push(Action.SubmitRequest);
  }

  if (canUpdateRequest(authContext, requestStatusCode)) {
    actions.push(Action.UpdateRequest);
  }

  if (canCancelRequest(authContext, requestStatusCode, requestInStakeholderConsultation)) {
    actions.push(Action.CancelRequest);
  }

  if (canUpdateRorDates(authContext, requestStatusCode)) {
    actions.push(Action.UpdateRorDates);
  }
  return actions;
};

export const getAvailableActions = (
  authContext: AuthContextType,
  assignments: Assignment[],
  requestStatusCode: string,
  requestInStakeholderConsultation: boolean,
  latestRequestActivity: RequestActivity | undefined,
  accountId: number,
  conversationStatus?: ReferenceType,
): Action[] => {
  const actions: Action[] = [];

  if (requestStatusCode === "NOT_REQUESTED") {
    return getNotRequestedStatusAvailableActions(authContext);
  }

  if (authContext.isSupportUser()) {
    return [];
  }

  if (canUpdateOrCreateInvoice(authContext)) {
    actions.push(Action.CreateInvoice);
    actions.push(Action.UpdateInvoice);
  }

  if (canAcceptOrDeclineAssignment(authContext, assignments)) {
    actions.push(Action.AcceptDeclineAssignment);
  }

  if (canViewConversation(authContext, assignments, accountId, conversationStatus)) {
    actions.push(Action.ViewConversation);
  }

  if (
    shouldCheckAcceptedAssignments(authContext) &&
    !anyAcceptedAssignments(authContext, assignments, requestStatusCode)
  ) {
    return actions;
  }

  if (canOpenConversation(authContext, requestStatusCode, conversationStatus)) {
    actions.push(Action.OpenConversation);
  }

  if (canUpdateAssignments(authContext)) {
    actions.push(Action.UpdateAssignments);
  }

  if (canUploadDocument(authContext, assignments, requestStatusCode)) {
    actions.push(Action.UploadDocument);
  }

  if (canCloseActivity(authContext, requestStatusCode, latestRequestActivity)) {
    actions.push(Action.CloseActivity);
  }

  actions.push(...getRequestActions(authContext, requestStatusCode, requestInStakeholderConsultation));
  actions.push(...getConsultationActions(authContext, requestInStakeholderConsultation));

  return actions;
};
