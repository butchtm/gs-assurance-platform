import { isAxiosError } from "axios";

import { GSRequest } from "../../../../common/request/GSRequest";
import { apiClient } from "../../../APIClient/Client";

export interface DocumentType {
  id: number;
  code: string;
  description: string;
  active?: boolean;
}

interface UploadSuccess {
  success: true;
}

interface UploadFailure {
  success: false;
  detail: string;
  serverHeader?: string;
}

type UploadResult = UploadSuccess | UploadFailure;

export async function uploadDocument(
  projectId: number | null,
  request: GSRequest,
  documentTypeId: number,
  file: File,
): Promise<UploadResult> {
  const formData = new FormData();
  formData.append("file", file);

  try {
    await apiClient.makeRequest(
      `/projects/${projectId}/requests/${request.id}/document-types/${documentTypeId}/documents`,
      "POST",
      formData,
    );
    return { success: true };
  } catch (error) {
    let detail = "Error uploading document.";
    let serverHeader: string | undefined;

    if (isAxiosError(error) && error.response) {
      const apiDetail = error.response.data?.detail;
      if (typeof apiDetail === "string" && apiDetail.trim() !== "") {
        detail = apiDetail;
      }
      const errorWithHeader = error as Error & { serverHeader?: string };
      serverHeader = errorWithHeader.serverHeader || error.response.headers?.server;
    } else {
      console.error("Unexpected error uploading document:", error);
    }

    return { success: false, detail, serverHeader };
  }
}

export async function modifyDocument(
  documentId: string,
  documentTypeId: number,
  access: boolean,
  category: boolean,
  latest: boolean,
) {
  const updatePayload = {
    access: access,
    category: category,
    documentTypeId: documentTypeId,
    isLatest: latest,
  };

  try {
    await apiClient.makeRequest(`/documents/${documentId}`, "PUT", JSON.stringify(updatePayload));

    return { success: true };
  } catch (error) {
    let errorMessage = "Error modifying document.";
    if (isAxiosError(error)) {
      if (error.response && error.response.status === 409) {
        errorMessage = "Error: filename already in use on the system, please try again";
      }
    } else {
      console.error("Error modifying document:", error);
    }
    return { success: false, errorMessage };
  }
}

export async function checkFileSupportedForPiiScan(contentType: string, documentSizeBytes: number): Promise<boolean> {
  const queryParams = new URLSearchParams({});
  queryParams.append("contentType", contentType);
  queryParams.append("documentSizeBytes", documentSizeBytes.toString());

  return await apiClient.makeRequest(`/pii-analysis/documents/supported`, "GET", undefined, { filters: queryParams });
}
