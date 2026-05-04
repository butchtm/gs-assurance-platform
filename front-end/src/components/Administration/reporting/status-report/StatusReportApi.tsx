import { apiClient } from "../../../APIClient/Client";

interface StatusReportResponse {
  projects: StatusReportProjectGroup[];
  requests: StatusReportRequestGroup[];
  allRequests: StatusReportAllRequests;
}

export interface StatusReportProjectGroup {
  type: string;
  description: string;
  count: number;
}

export interface StatusReportRequestGroup {
  type: string;
  description: string;
  completedCount: number;
}

interface StatusReportAllRequests {
  completedCount: number;
  successfulCompletenessChecksCount: number;
}

export const getStatusReport = (startDate?: string, endDate?: string): Promise<StatusReportResponse> => {
  const params = new URLSearchParams();
  if (startDate) {
    params.set("startDate", startDate);
  }
  if (endDate) {
    params.set("endDate", endDate);
  }

  return apiClient.makeRequest("/status-report", "GET", "{}", { filters: params });
};

export const exportStatusReportExcel = async (startDate?: string, endDate?: string): Promise<void> => {
  const params = new URLSearchParams();
  if (startDate) {
    params.set("startDate", startDate);
  }
  if (endDate) {
    params.set("endDate", endDate);
  }

  try {
    await apiClient.makeRequest("/status-report/excel", "GET", "{}", {
      filters: params,
      rawResponse: true,
    });
  } catch (error) {
    console.error("Failed to download status report:", error);
    throw error;
  }
};
