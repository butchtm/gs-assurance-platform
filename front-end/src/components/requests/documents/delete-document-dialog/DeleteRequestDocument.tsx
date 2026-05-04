import { apiClient } from "../../../APIClient/Client";

export async function deleteDocument(documentId: string, requestId: number) {
  try {
    await apiClient.makeRequest(`/requests/${requestId}/documents/${documentId}`, "DELETE");

    return { success: true };
  } catch (error) {
    console.error("Error deleting document:", error);
    return { success: false, errorMessage: "Error deleting document." };
  }
}
