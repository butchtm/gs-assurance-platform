import { apiClient } from "../../../APIClient/Client";

interface DocumentPiiResults {
  totalElements: number;
  content: PageGroupedPii[];
}

export interface PageGroupedPii {
  entity: string;
  probability: string;
  pageNumber: number | null;
  location: string | null;
  occurrences: number;
}

export const getPiiResults = (documentId: string, page: number, pageSize: number): Promise<DocumentPiiResults> => {
  return apiClient.makeRequest(`/pii-analysis/documents/${documentId}/results`, "GET", "{}", {
    page,
    pageSize,
  });
};
