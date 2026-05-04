import { apiClient } from "../../components/APIClient/Client";
import { ReferenceTypes } from "../data-types/DataReferenceTypes";
import { GSRequest, isPreliminaryReview } from "../request/GSRequest";

import { PiiActionOption } from "./PiiActionDropdown";

export enum PiiActionCode {
  NEW = "NEW",
  UNDER_INVESTIGATION = "UNDER_INVESTIGATION",
  ACCEPTED = "ACCEPTED",
  PII_NOT_SUITABLE = "PII_NOT_SUITABLE",
  DOCUMENT_REMOVED = "DOCUMENT_REMOVED",
  NO_PII_FOUND = "NO_PII_FOUND",
}

interface PiiActionResult {
  success: boolean;
}

export interface PiiAction {
  code: PiiActionCode;
}

export const getSupportedActionOptions = (supportedActions: string[], activeReferenceTypes: ReferenceTypes) => {
  return activeReferenceTypes.PiiAction.filter((action) => supportedActions.includes(action.code)).map((action) => ({
    value: action.id,
    label: action.description,
    code: action.code,
  }));
};

export const getActionOptionByCode = (
  supportedActionOptions: PiiActionOption[],
  actionCode?: string,
): PiiActionOption | null => {
  return supportedActionOptions.find((action) => action.code == actionCode) ?? null;
};

export const submitPiiAction = async (
  piiAction: PiiActionOption | null,
  projectId?: number,
  requestId?: number,
  commentId?: number,
  documentId?: string,
): Promise<PiiActionResult> => {
  const piiActionPayload = {
    piiAction: piiAction?.value,
    projectId: projectId,
    requestId: requestId,
    commentId: commentId,
    documentId: documentId,
  };
  try {
    await apiClient.makeRequest(`/pii-metadata/action`, "POST", JSON.stringify(piiActionPayload));
    return { success: true };
  } catch (error) {
    console.error("Unexpected error uploading document:", error);
    return { success: false };
  }
};

export const isPiiAcceptedAndRelevant = (request: GSRequest, piiAccepted: boolean, showPiiAcceptance: boolean) => {
  if (!isPreliminaryReview(request)) {
    return true;
  }
  if (!showPiiAcceptance) {
    return true;
  }
  return piiAccepted;
};

export const isPiiNotAcceptedAndRelevant = (request: GSRequest, piiAccepted: boolean, showPiiAcceptance: boolean) => {
  if (!isPreliminaryReview(request)) {
    return false;
  }
  if (!showPiiAcceptance) {
    return false;
  }
  return !piiAccepted;
};

export const canPiiActionBeMadePublic = (actionCode: PiiActionCode | undefined) => {
  if (actionCode) {
    return [PiiActionCode.ACCEPTED, PiiActionCode.NO_PII_FOUND].includes(actionCode);
  }
  return true;
};

export const updateProjectPiiToNotFound = async (projectId: number, projectFields: string[]) => {
  try {
    await apiClient.makeRequest(`/pii-metadata/${projectId}/action/not-found`, "POST", JSON.stringify(projectFields));
    return { success: true };
  } catch (error) {
    console.error("Unexpected error uploading document:", error);
    return { success: false };
  }
};
