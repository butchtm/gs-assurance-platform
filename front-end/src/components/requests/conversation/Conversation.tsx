import { ReferenceType } from "../../../common/data-types/DataReferenceTypes";
import { apiClient } from "../../APIClient/Client";

export const openConversation = (projectId: number, requestId: number) => {
  return apiClient.makeRequest(`/projects/${projectId}/requests/${requestId}/conversations`, "POST", "{}");
};

export const updateConversationStatus = (
  projectId: number,
  requestId: number,
  conversationId?: number,
  status?: ReferenceType,
) => {
  return apiClient.makeRequest(
    `/projects/${projectId}/requests/${requestId}/conversations/${conversationId}/status`,
    "PUT",
    JSON.stringify(status),
  );
};

export const getMessages = (
  projectId: number,
  requestId: number,
  conversationId: number,
  page: number,
): Promise<MessageResponse> => {
  return apiClient.makeRequest(
    `/projects/${projectId}/requests/${requestId}/conversations/${conversationId}/messages/pages`,
    "GET",
    "{}",
    {
      page: page,
      pageSize: 10,
    },
  );
};

export const sendMessage = (projectId: number, requestId: number, conversationId: number, message: string) => {
  return apiClient.makeRequest(
    `/projects/${projectId}/requests/${requestId}/conversations/${conversationId}/messages`,
    "POST",
    JSON.stringify({
      message: message,
    }),
  );
};

export const markRead = (projectId: number, requestId: number, conversationId: number) => {
  return apiClient.makeRequest(
    `/projects/${projectId}/requests/${requestId}/conversations/${conversationId}/messages/reads`,
    "POST",
    "{}",
  );
};

export const getUnreadMessages = () => {
  return apiClient.makeRequest("/conversations/unread-messages", "GET");
};

export interface Conversation {
  id: number;
  status: ReferenceType;
  unreadMessages?: number;
}

export interface Message {
  id: number;
  content: string;
  senderUsername: string;
  senderAccountType: ConversationParticipant | null;
  sentTimestamp: string;
  readByAccountTypes: ConversationParticipant[];
  readByUsers: string[];
}

interface MessageResponse {
  content: Message[];
  pageSize: number;
  pageNumber: number;
  totalPages: number;
  totalElements: number;
}

export enum ConversationParticipant {
  ProjectDeveloper = "Project Developer",
  VVB = "VVB",
  GSARMTeam = "Gold Standard ARM Team",
}

export interface UnreadMessages {
  requestId: number;
  conversationId: number;
  unreadCount: number;
}
