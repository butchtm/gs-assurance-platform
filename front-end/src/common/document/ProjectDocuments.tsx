import { isAxiosError } from "axios";

import { apiClient } from "../../components/APIClient/Client";
import {
  PublicStakeholderComment,
  PublicStakeholderDocument,
} from "../../components/Public/PublicConsultation/PublicConsultation";

export interface ProjectDocumentResponse {
  gsid: string;
  name: string;
  requests: PublicProjectRequest[];
}

export interface PublicProjectRequest {
  id: number;
  name: string;
  index: number;
  requestType: string;
  documents?: PublicStakeholderDocument[];
  comments?: PublicStakeholderComment[];
  consultationStartDate?: string;
  consultationEndDate?: string;
}

export async function getPublicProjectDocuments(gsId: string): Promise<ProjectDocumentResponse | null> {
  try {
    const response = await apiClient.makeRequest("/public/project-documents/" + gsId, "GET");

    return response as ProjectDocumentResponse;
  } catch (error) {
    if (isAxiosError(error)) {
      console.error("No Documents found for gsId:", gsId);
    } else {
      console.error("Failed to fetch documents:", error);
    }
    return null;
  }
}

export async function downloadDocument(documentId: string, filename?: string): Promise<string | void> {
  return await fetchDocument(`/documents/${documentId}/download`, filename);
}

export async function downloadPublicDocument(documentId: string, filename?: string): Promise<string | void> {
  return await fetchDocument(`/public/documents/${documentId}/download`, filename);
}

async function fetchDocument(url: string, filename?: string): Promise<string | void> {
  try {
    await apiClient.makeRequest(url, "GET", undefined, {
      rawResponse: true,
      download: filename,
    });
    return;
  } catch (error) {
    console.error("Failed to fetch project documents:", error);
    throw error;
  }
}
