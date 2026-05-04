import { Dayjs, isDayjs } from "dayjs";

import { PagedResource } from "../../../common/filterable-sortable-table/model/PagedResource";
import { apiClient } from "../../APIClient/Client";
import { getAccounts } from "../../PlatformAccounts/PlatformAccount";
import { PlatformAccount } from "../../PlatformAccounts/PlatformAccountTypes";

export interface AssignmentDto {
  requestId: number;
  accountId: number | null;
  assignmentTypeId: number | undefined | null;
  assignmentStatusId: number | undefined | null;
}

export interface Assignment {
  id?: number | null;
  assignmentId?: number | null;
  assignmentType: AssignmentType;
  assignmentStatus: AssignmentStatus;
  assignmentTypeId: number;
  assignmentStatusId: number;
  account: PlatformAccount | null;
  requestId: number;
}

export interface AssignmentType {
  id: number;
  code: string;
  description: string;
}

export interface AssignmentStatus {
  id: number;
  code: string;
  description: string;
  active?: boolean;
}

export interface AssignmentTypeMapping {
  assignmentTypeId: number;
  mandatory: boolean;
}

export interface AssignmentSummaryFilters {
  assignmentTypeId?: number;
  qaqcAssignedToCallingAccount: boolean;
  lastSubmittedAfter?: Dayjs;
  lastSubmittedBefore?: Dayjs;
  requestDueBefore?: Dayjs;
  projectTypeIds?: number[];
  requestTypeIds?: number[];
}

export interface AssignmentSummary {
  accountName: string;
  openRequests?: number;
  closedRequests?: number;
  roundOneRequests?: number | null;
  inProgressRequests?: number | null;
  upcomingRequests?: number | null;
  dueRequests?: number | null;
  overdueRequests?: number | null;
}

export interface AssignmentSummaryResponse {
  assignmentSummary: PagedResource<AssignmentSummary[]>;
  assignmentTotals: AssignmentSummary;
}

export async function getAllAccountsByAssignmentType(assignmentTypeId: number): Promise<PlatformAccount[]> {
  const filters = { assignmentTypeId: assignmentTypeId, active: true };
  const response = await getAccounts(filters);
  return response.content;
}

export async function getAssignmentTypeByRequestType(requestTypeId: number): Promise<AssignmentTypeMapping[]> {
  try {
    const response = await apiClient.makeRequest("/request-type-assignment-types/" + requestTypeId, "GET");
    return response.assignmentTypes ?? [];
  } catch (error) {
    console.error("Failed to fetch assignments type mapping", error);
    throw error;
  }
}

export async function getRequestAssignments(requestId: number): Promise<Assignment[]> {
  try {
    const response = await apiClient.makeRequest(`/requests/${requestId}/assignments`, "GET");
    return response as Assignment[];
  } catch (error) {
    console.error("Failed to fetch assignments", error);
    throw error;
  }
}

export async function getAssignment(assignmentId: number): Promise<Assignment> {
  try {
    const response = await apiClient.makeRequest("/assignments/" + assignmentId, "GET");
    return response as Assignment;
  } catch (error) {
    console.error("Failed to fetch assignment", error);
    throw error;
  }
}

export async function getRoeAssignmentSummaryResponse(
  filters: AssignmentSummaryFilters,
  page?: number,
  pageSize?: number,
): Promise<AssignmentSummaryResponse> {
  return await fetchAssignmentSummary(page, pageSize, filters, "review");
}

export async function getArmAssignmentSummaryResponse(
  filters: AssignmentSummaryFilters,
  page?: number,
  pageSize?: number,
): Promise<AssignmentSummaryResponse> {
  return await fetchAssignmentSummary(page, pageSize, filters, "arm");
}

export const exportRoeAssignmentSummaryReport = async (filters: AssignmentSummaryFilters): Promise<void> => {
  await exportAssignmentSummaryReport(filters, "review");
};

export const exportArmAssignmentSummaryReport = async (filters: AssignmentSummaryFilters): Promise<void> => {
  await exportAssignmentSummaryReport(filters, "arm");
};

const exportAssignmentSummaryReport = async (filters: AssignmentSummaryFilters, type: string): Promise<void> => {
  try {
    await apiClient.makeRequest(`/assignment-summaries/reports/${type}`, "POST", "{}", {
      filters: toQueryParams(filters),
      rawResponse: true,
    });
  } catch (error) {
    console.error(`Failed to download ${type} Assignment Summary report: `, error);
    throw error;
  }
};

const toQueryParams = (filters: AssignmentSummaryFilters) => {
  const queryParams = new URLSearchParams({});
  if (!filters) {
    return queryParams;
  }

  for (const [key, value] of Object.entries(filters)) {
    if (isDayjs(value)) {
      queryParams.set(key, value.toISOString());
    } else if (value != undefined && value != null) {
      queryParams.set(key, value.toString());
    }
  }
  return queryParams;
};

export const assignChecksToVVBorARMTeam = async (assignmentId: number | null, payload: AssignmentDto) => {
  if (assignmentId) {
    await apiClient.makeRequest(`/assignments/` + assignmentId, "PUT", JSON.stringify(payload));
  } else {
    await apiClient.makeRequest(`/assignments`, "POST", JSON.stringify(payload));
  }
};

export const assignRequest = async (payload: Partial<Assignment>) => {
  await apiClient.makeRequest(`/assignments`, "POST", JSON.stringify(toDto(payload)));
};

export const updateRequestAssignment = async (assignmentId: number, payload: Partial<Assignment>) => {
  if (assignmentId) {
    await apiClient.makeRequest(`/assignments/` + assignmentId, "PUT", JSON.stringify(toDto(payload)));
  }
};

const toDto = (input: Partial<Assignment>) => {
  return {
    requestId: input.requestId,
    accountId: input.account?.id,
    assignmentTypeId: input.assignmentTypeId,
    assignmentStatusId: input.assignmentStatusId,
  };
};

export const updateRequestAssignmentStatus = async (assignmentId: number, payload: AssignmentStatusPayload) => {
  await apiClient.makeRequest(`/assignments/` + assignmentId + `/status`, "PUT", JSON.stringify(payload));
};

interface AssignmentStatusPayload {
  assignmentStatusId: number | undefined;
}

async function fetchAssignmentSummary(
  page: number | undefined,
  pageSize: number | undefined,
  filters: AssignmentSummaryFilters,
  type: string,
): Promise<AssignmentSummaryResponse> {
  return await apiClient.makeRequest(`/assignment-summaries/${type}`, "GET", "{}", {
    page: page,
    pageSize: pageSize,
    filters: toQueryParams(filters),
  });
}
