import { PiiMetadata, TextAnalysisResponse } from "../../../common/piiAnalysis/PiiAnalysis";
import { apiClient } from "../../APIClient/Client";
import { AccountType } from "../../PlatformAccounts/PlatformAccountTypes";

export interface RequestComment {
  id: number;
  requestId: number;
  submittedComment: string;
  uploadedByName: string;
  uploadedByEmail: string;
  accountId: number | null;
  accountType: AccountType | null;
  uploadedDate: string;
  publicAccess: boolean;
  anonymous: boolean;
  addressed: boolean;
  suppressed: boolean;
  suppressionReasonId: number;
  deleted: boolean;
  deletedReasonId: number;
  piiMetadata?: PiiMetadata;
}

export interface RequestCommentCreate {
  requestId: number;
  comment: string;
  usersName?: string;
  usersEmail?: string;
  accountId?: number;
  publicAccess: boolean;
  anonymous: boolean;
  textAnalysisResults: TextAnalysisResponse | null;
}

export interface RequestCommentUpdate {
  id: number;
  publicAccess: boolean;
  anonymous: boolean;
  addressed: boolean;
  suppressed?: boolean;
  suppressionReasonId?: number;
  piiAction?: number | null;
}

export async function getRequestComments(requestId: number): Promise<RequestComment[]> {
  try {
    const response = await apiClient.makeRequest("/comments/" + requestId, "GET");
    const data = await response;
    return data as RequestComment[];
  } catch (error) {
    console.error("Failed to fetch comments from API", error);
    throw error;
  }
}

export const publicCreateRequestComment = async (payload: RequestCommentCreate) => {
  await apiClient.makeRequest(`/public/comments`, "POST", JSON.stringify(payload));
};

export const createRequestComment = (payload: RequestCommentCreate) => {
  return apiClient.makeRequest(`/comments`, "POST", JSON.stringify(payload));
};

export const updateRequestComment = async (payload: RequestCommentUpdate) => {
  await apiClient.makeRequest(`/comments`, "PUT", JSON.stringify(payload));
};

export const deleteRequestComment = async (commentId: number, selectedReasonId: number) => {
  const payload = {
    id: commentId,
    deletionReasonId: selectedReasonId,
  };

  await apiClient.makeRequest(`/comments`, "DELETE", JSON.stringify(payload));
};
