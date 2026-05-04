import { Dayjs } from "dayjs";
import { DropdownOption, MRT_SortingState } from "material-react-table";

import { ReferenceType, ReferenceTypeContextType } from "../../common/data-types/DataReferenceTypes";
import { GSDocument } from "../../common/document/GSDocument";
import { GSDocumentScrap } from "../../common/document/GSDocumentScrap";
import { CreditingPeriod, CurrentRiskSummary } from "../../common/project/Project";
import {
  CreateBatchRequestPayload,
  CreateUpdateRequestPayload,
  GSRequest,
  RequestConsultation,
  RequestRound,
} from "../../common/request/GSRequest";
import { apiClient } from "../APIClient/Client";
import { PlatformAccount } from "../PlatformAccounts/PlatformAccountTypes";

import { AssignmentTypeMapping } from "./assignment/Assignment";
import { MethodologyConfiguration, ParentRequestWithBatchedRequests } from "./create-request/Wizard/Request";
import { MandatoryStatus } from "./MandatoryStatus";

export interface FormValues {
  requestType: string;
  creditingPeriodId: number | null;
  startDate: string | null;
  endDate: string | null;
  emissionReductionAmount: number;
  batchStepProjectValues: ParentRequestWithBatchedRequests | null;
  methodologyConfigurations: MethodologyConfiguration[];
  newCreditingPeriodStartDate: string | null;
  newCreditingPeriodEndDate: string | null;
  monitoringPeriodId: number | null;
  newMonitoringPeriodStartDate: Dayjs | null;
  newMonitoringPeriodEndDate: Dayjs | null;
  feeSchedule: number | null;
}

export interface BatchedProjectRequests {
  parent: BatchedProjectRequest;
  children: BatchedProjectRequest[];
}

export interface BatchedProjectRequest {
  classification: string;
  gsid: string;
  id: number;
  name: string;
  requestId?: number;
  creditingPeriods?: CreditingPeriod[];
  methodologies?: ReferenceType[];
  currentRiskSummary?: CurrentRiskSummary;
}

interface PermittedStatus {
  requestStatusId: number;
  minimumRoundOfReviewCount: number;
  permittedDuringConsultation: boolean;
}

interface ActiveAccountType {
  accountTypeId: number;
  canProgress: boolean;
}

export interface RequiredDocumentsResponse {
  OTHER?: GSDocumentScrap[];
  REQUIRED?: GSDocumentScrap[];
  SUPPORTING?: GSDocumentScrap[];
}

interface StakeholderResponse {
  requestTypeId: number;
  requiresConsultation: boolean; //null is consultation optional
}

export interface RequestStatusResponse {
  requestStatusId: number;
  permittedStatuses: PermittedStatus[];
  activeAccountTypes: ActiveAccountType[];
}

export interface RequestRoundCreate {
  startDate: string;
  endDate: string;
}

export interface AssignedRequestFilters {
  requestStatusIds?: number[];
  projectIds?: number[];
  projectSearch?: string;
  projectAccountIds?: number[];
  reviewerAccountIds?: number[];
  vvbAccountIds?: number[];
  requestTypeIds?: number[];
}

export interface RequestRoundUpdate {
  startDate: string;
  endDate: string;
  statusId: number;
}

export interface AssignedRequestsResponse {
  submittedByGivenName: string;
  submittedByFamilyName: string;
  projectId: number;
  gsid: string;
  projectName: string;
  projectAccountId: number;
  projectAccountName: string;
  projectRisk?: boolean | null;
  projectRiskSummary?: string;
  projectSize: ReferenceType;
  projectCarbonStream: ReferenceType;
  projectLocations: ReferenceType[];
  projectTypes: ReferenceType[];

  requestId: number;
  requestName: string;
  batchId?: number;
  batchSize?: number;
  requestTypeId: number;
  requestTypeDescription: string;
  requestStatusId: number;
  requestStatus: string;
  currentRoundIndex: number | null;
  currentRoundStartDate?: string;
  currentRoundEndDate?: string;

  invoiceStatus: string;

  requestRounds: RequestRound[];

  assignmentId: number;
  assignmentStatus: string;
  assignmentCreatedDate: string;
  dueDate: string;

  lastRequestStatusChangeDate?: string;

  estimatedAnnualEmissionReductionAmount: number;

  vvbAccountId?: number;
  vvbAccountName: string;
  reviewerAccountId?: number;
  reviewerAccountName: string;
  latestActivityName: string;
  latestActivityPlannedEndDate: string;
  latestActivityResponsibleAccountId: number;
  latestActivityResponsibleAccountName: string;
  unreadMessages?: number;
}

export interface CommonDashboardRequestResponse {
  id: number;
  requestStatus: ReferenceType;
  startDate: string;
  firstRoundStartDate: string;
  currentRoundEndDate: string;
  currentRoundIndex: number;
  closedDate: string;
  type: string;
  name: string;
  projectTypesString: string;
  project: CommonDashboardRequestProjectResponse;
  invoice: CommonDashboardRequestInvoiceResponse;
  monitoringPeriod?: CommonDashboardRequestMonitoringPeriodResponse;
}

export interface CommonDashboardRequestProjectResponse {
  gsid: string;
  name: string;
  status: ReferenceType;
  poaGsid: string | null;
  realCaseVpaGsid: string | null;
  classification: ReferenceType;
  types: ReferenceType[];
  feeSchedule?: ReferenceType;
  accountName?: string;
}

export interface CommonDashboardRequestMonitoringPeriodResponse {
  id?: number;
  feeSchedule?: ReferenceType;
}

export interface CommonDashboardRequestInvoiceResponse {
  status: ReferenceType | null;
}

export interface AllRequestsResponse {
  projectId: number;
  gsid: string;
  projectName: string;
  projectCarbonStream: ReferenceType;
  projectSize: ReferenceType;
  projectAccountId: number;
  projectAccountName: string;
  projectRisk?: boolean | null;
  projectRiskSummary?: string | null;

  requestId: number;
  requestName: string;
  requestTypeId: number;
  requestTypeDescription: string;
  batchId?: number;
  batchSize?: number;
  requestStatusId: number;
  requestStatus: string;
  currentRoundIndex?: number;
  currentRoundStartDate?: string;
  currentRoundEndDate?: string;
  estimatedAnnualEmissionReductionAmount: number;

  invoiceId: number;
  invoiceStatusId: number;
  invoiceStatus: string;

  vvbAccountId?: number;
  vvbAccountName: string;
  reviewerAccountId?: number;
  reviewerAccountName: string;

  qaqcAccountId: number;
  qaqcAccountName: string;
  qaqcAccountUnreadMessages: number;

  activityName: string;
  plannedEndDate: string;
  responsibleAccountName: string;
  submittedByGivenName: string;
  submittedByFamilyName: string;
  lastRequestStatusChangeDate?: string;
}

export interface ProjectDeveloperRequestsResponse {
  submittedByGivenName: string;
  submittedByFamilyName: string;
  batched: boolean;
  carbonStream?: ReferenceType;
  currentRequestStatus: ReferenceType;
  currentRoundIndex?: number;
  gsid: string;
  id: number;
  invoiceStatus?: ReferenceType;
  projectLocations: ReferenceType[];
  projectName: string;
  projectTypes: ReferenceType[];
  requestName: string;
  requiredDocumentStatus: {
    provided: number;
    required: number;
  };
  reviewerAccountId?: number;
  reviewerAccountName?: string;
  vvbAccountId?: number;
  vvbAccountName?: string;
  activityName?: string;
  plannedDeadlineDate?: string;
  currentRoundEndDate?: string;
  lastRequestStatusChangeDate?: string;
  unreadMessages?: number;
}

export interface TacRequestsResponse {
  submittedByGivenName: string;
  submittedByFamilyName: string;
  id: number;
  projectGsid: string;
  projectName: string;
  requestName: string;
  requestStatus: ReferenceType;
  vvbAccountId: number;
  vvbAccountName: string;
  currentConsultationStartDate?: string;
  currentConsultationEndDate?: string;
  lastRequestStatusChangeDate?: string;
}

export async function getRequests(
  page: number,
): Promise<{ data: GSRequest[]; totalPages: number; pageNumber: number }> {
  try {
    const response = await apiClient.makeRequest("/requests", "GET", "{}", { page });
    return {
      data: response.content,
      totalPages: response.totalPages,
      pageNumber: response.currentPage,
    };
  } catch (error) {
    console.error("Error with API call:", error);
    throw error;
  }
}

export async function getRequestsByProject(projectId: number): Promise<GSRequest[]> {
  let allRequests: GSRequest[] = [];
  const queryParams = new URLSearchParams({});
  if (projectId) {
    queryParams.set("projectId", projectId.toString());
  }

  try {
    let currentPage = 0;
    let totalPages: number;

    do {
      const response = await apiClient.makeRequest(`/requests`, "GET", "{}", {
        page: currentPage,
        pageSize: 25,
        filters: queryParams,
      });

      allRequests = allRequests.concat(response.content);

      totalPages = response.totalPages;
      currentPage = response.currentPage + 1;
    } while (currentPage <= totalPages);

    return allRequests;
  } catch (error) {
    console.error("Error with API call:", error);
    throw error;
  }
}

export async function getRequestsInConsultation(
  page: number,
  sort?: string,
): Promise<{ data: TacRequestsResponse[]; totalPages: number; pageNumber: number }> {
  try {
    const filterParams = new URLSearchParams({});

    if (sort) {
      filterParams.append("sort", sort);
    }

    const response = await apiClient.makeRequest("/requests/consultation", "GET", "{}", {
      page,
      pageSize: 25,
      filters: filterParams,
    });
    return {
      data: response.content,
      totalPages: response.totalPages,
      pageNumber: response.currentPage,
    };
  } catch (error) {
    console.error("Error with API call:", error);
    throw error;
  }
}

export function getLiveConsultationsForRequest(requestId: number): Promise<RequestConsultation[]> {
  return apiClient.makeRequest(`/requests/${requestId}/live-stakeholder-consultations`, "GET", "{}");
}

export function getConsultationsForRequest(requestId: number): Promise<RequestConsultation[]> {
  return apiClient.makeRequest(`/requests/${requestId}/stakeholder-consultations`, "GET", "{}");
}

export async function getRequestsInConsultationWithInfo(
  page: number,
  sort?: string,
): Promise<{ data: TacRequestsResponse[]; totalPages: number; pageNumber: number }> {
  const requests = await getRequestsInConsultation(page, sort);
  return { ...requests, data: requests.data.map((request) => ({ ...request })) };
}

export async function getRequestsInConsultationByCompetencies(
  page: number,
  sort?: string,
): Promise<{ data: TacRequestsResponse[]; totalPages: number; pageNumber: number }> {
  try {
    const filterParams = new URLSearchParams({});

    if (sort) {
      filterParams.append("sort", sort);
    }

    const response = await apiClient.makeRequest("/requests/requests-in-consultation-by-competencies", "GET", "{}", {
      page,
      pageSize: 25,
      filters: filterParams,
    });
    return {
      data: response.content,
      totalPages: response.totalPages,
      pageNumber: response.currentPage,
    };
  } catch (error) {
    console.error("Error with API call:", error);
    throw error;
  }
}

export function getDocumentsForRequestByCategory(
  projectId: number,
  requestId: number,
  displayCategoryId: number,
  pageNumber: number,
  pageSize: number,
) {
  return apiClient.makeRequest(
    `/projects/${projectId}/requests/${requestId}/documents/${displayCategoryId}`,
    "GET",
    "{}",
    {
      page: pageNumber,
      pageSize: pageSize,
    },
  );
}

export async function getAllDocumentsForRequestByCategory(
  projectId: number,
  requestId: number,
  displayCategoryId: number,
) {
  try {
    let documents: GSDocument[] = [];
    let currentPage = 1;
    let totalPages = 0;

    do {
      const response = await getDocumentsForRequestByCategory(
        projectId,
        requestId,
        displayCategoryId,
        currentPage,
        100,
      );
      const pageData = response.content;
      totalPages = response.totalPages;
      currentPage = response.pageNumber;

      documents = documents.concat(pageData);

      currentPage++;
    } while (currentPage <= totalPages);

    return documents;
  } catch (error) {
    console.error("Failed to fetch documents from API", error);
    throw error;
  }
}

function getDocumentsForProjectRequest(projectId: number, requestId: number, pageNumber: number) {
  return apiClient.makeRequest(`/projects/${projectId}/requests/${requestId}/documents`, "GET", "{}", {
    page: pageNumber,
  });
}

export async function getAllDocumentsForProjectRequest(projectId: number, requestId: number) {
  try {
    let documents: GSDocument[] = [];
    let currentPage = 1;
    let totalPages = 0;

    do {
      const response = await getDocumentsForProjectRequest(projectId, requestId, currentPage);
      const pageData = response.content;
      totalPages = response.totalPages;
      currentPage = response.pageNumber;

      documents = documents.concat(pageData);

      currentPage++;
    } while (currentPage <= totalPages);

    return documents;
  } catch (error) {
    console.error("Failed to fetch documents from API", error);
    throw error;
  }
}

export async function getCurrentPathwayGroupTimeline(requestId: number, signal: AbortSignal) {
  return await apiClient.makeRequest(
    `/requests/${requestId}/current-pathway-group-timelines`,
    "GET",
    "{}",
    undefined,
    signal,
  );
}

export async function checkIfAssignmentTypeRequiredForRequestType(
  requestTypeId: number,
  assignmentTypeId: number,
): Promise<MandatoryStatus> {
  try {
    const response = await apiClient.makeRequest("/request-type-assignment-types/" + requestTypeId, "GET");
    if (response) {
      return findAssignmentStatus(assignmentTypeId, response.assignmentTypes);
    }
    return MandatoryStatus.NOT_REQUIRED;
  } catch (error) {
    console.error("Failed to fetch request assignments type mapping", error);
    throw error;
  }
}

const findAssignmentStatus = (assignmentTypeId: number, mappings: AssignmentTypeMapping[]): MandatoryStatus => {
  const mapping = mappings.find((m) => m.assignmentTypeId === assignmentTypeId);
  return toAssignmentStatus(mapping);
};

const toAssignmentStatus = (mapping?: AssignmentTypeMapping): MandatoryStatus => {
  if (mapping) {
    return mapping.mandatory ? MandatoryStatus.REQUIRED : MandatoryStatus.OPTIONAL;
  }
  return MandatoryStatus.NOT_REQUIRED;
};

export function getRequiredDocumentsForRequest(requestTypeId: number): Promise<RequiredDocumentsResponse> {
  return apiClient.makeRequest("/requests/required-document-types/" + requestTypeId, "GET");
}

export function getRequestTypeStakeHolderConsultationRequired(): Promise<StakeholderResponse[]> {
  return apiClient.makeRequest("/request-type-consultations", "GET");
}

export function getAvailableRequestStatusTransitions(requestStatusId: number): Promise<RequestStatusResponse> {
  return apiClient.makeRequest(`/request-status/${requestStatusId}`, "GET");
}

export function getRequestWithId(requestId: number): Promise<GSRequest> {
  return apiClient.makeRequest(`/requests/${requestId}`, "GET");
}

export const updateRequest = (payload: Partial<CreateUpdateRequestPayload>, projectId: number) => {
  return apiClient.makeRequest(`/projects/${projectId}/requests`, "PUT", JSON.stringify(payload));
};

export const createRequest = (payload: Partial<CreateUpdateRequestPayload>, projectId: number) => {
  return apiClient.makeRequest(`/projects/${projectId}/requests`, "POST", JSON.stringify(payload));
};

export const createBatchRequest = (payload: Partial<CreateBatchRequestPayload>) => {
  return apiClient.makeRequest(`/batch-requests`, "POST", JSON.stringify(payload));
};

export const updateRoundOfReview = (requestId: number, payload: RequestRoundUpdate, currentIndex: number) => {
  return apiClient.makeRequest(`/requests/${requestId}/round/${currentIndex}`, "PUT", JSON.stringify(payload));
};

export const skipStakeholderConsultation = (requestId: number) => {
  return apiClient.makeRequest(`/requests/${requestId}/live-stakeholder-consultations/completions`, "PUT");
};

export const getPublicBatchedProjectRequests = (batchId: number): Promise<BatchedProjectRequests> => {
  return apiClient.makeRequest(`/public/batch/${batchId}/projects`, "GET");
};

export const getPrivateBatchProjectRequests = (batchId: number): Promise<BatchedProjectRequests> => {
  return apiClient.makeRequest(`/batch-requests/${batchId}/projects`, "GET");
};

export const getBatchedRequestTypeMappings = (requestTypeId: number, projectId: number) => {
  return apiClient.makeRequest(
    `/batch-requests/types`,
    "POST",
    JSON.stringify({
      requestTypeId: requestTypeId,
      projectId: projectId,
    }),
  );
};

export const getBatchableProjects = (requestTypeId: number, batchTypeId: number, projectId: number) => {
  return apiClient.makeRequest(
    `/batch-requests/candidate-projects`,
    "POST",
    JSON.stringify({
      requestTypeId: requestTypeId,
      batchTypeId: batchTypeId,
      projectId: projectId,
    }),
  );
};

const addFilterParams = (requestParams: AssignedRequestFilters, filterParams: URLSearchParams) => {
  (Object.keys(requestParams) as (keyof AssignedRequestFilters)[]).forEach((key) => {
    const value = requestParams[key];
    if (Array.isArray(value) && value.length > 0) {
      filterParams.append(key, value.join(","));
    } else if (typeof value == "string" && value !== "") {
      filterParams.append(key, value);
    }
  });
};

export const getAssignedRequests = (
  requestParams: AssignedRequestFilters,
  page: number,
  sort?: string,
  pageSize?: number,
): Promise<{ content: AssignedRequestsResponse[]; totalPages: number; pageNumber: number; totalElements: number }> => {
  const filterParams = new URLSearchParams({});

  if (requestParams) {
    addFilterParams(requestParams, filterParams);
  }

  if (sort) {
    filterParams.append("sort", sort);
  }
  pageSize ??= 25;

  return apiClient.makeRequest(`/assigned-requests`, "GET", "{}", {
    page,
    pageSize: pageSize,
    filters: filterParams,
  });
};

export const getCommonDashboardRequests = (
  requestParams: AssignedRequestFilters,
  page: number,
  pageSize: number,
  sortingState: MRT_SortingState,
): Promise<{
  content: CommonDashboardRequestResponse[];
  totalPages: number;
  totalElements: number;
  pageNumber: number;
}> => {
  const filterParams = new URLSearchParams({});

  if (requestParams) {
    addFilterParams(requestParams, filterParams);
  }
  if (sortingState.length > 0) {
    const directionString = sortingState[0].desc ? "desc" : "asc";
    filterParams.append("sort", `${sortingState[0].id},${directionString}`);
  }
  return apiClient.makeRequest(`/common-dashboard-requests`, "GET", "{}", {
    page,
    pageSize: pageSize,
    filters: filterParams,
  });
};

export const exportCommonDashboardReport = (requestParams: AssignedRequestFilters, sortingState: MRT_SortingState) => {
  const filterParams = new URLSearchParams({});
  if (requestParams) {
    addFilterParams(requestParams, filterParams);
  }
  if (sortingState.length > 0) {
    const directionString = sortingState[0].desc ? "desc" : "asc";
    filterParams.append("sort", `${sortingState[0].id},${directionString}`);
  }
  return apiClient.makeRequest(`/common-dashboard-requests/reports`, "POST", "{}", {
    filters: filterParams,
  });
};

export const getAllRequests = (
  requestParams: AssignedRequestFilters,
  page: number,
  pageSize: number,
  sortingState: MRT_SortingState,
): Promise<{ content: AllRequestsResponse[]; totalPages: number; totalElements: number; pageNumber: number }> => {
  const filterParams = new URLSearchParams({});

  if (requestParams) {
    addFilterParams(requestParams, filterParams);
  }

  if (sortingState.length > 0) {
    const directionString = sortingState[0].desc ? "desc" : "asc";
    filterParams.append("sort", `${sortingState[0].id},${directionString}`);
  }

  return apiClient.makeRequest(`/all-requests`, "GET", "{}", {
    page,
    pageSize,
    filters: filterParams,
  });
};

export const getAssignedRequestStatuses = (): Promise<ReferenceType[]> => {
  return apiClient.makeRequest(`/assigned-requests/request-statuses`, "GET");
};

export const getAssignedRequestAccounts = (accountTypeId: number): Promise<PlatformAccount[]> => {
  return apiClient.makeRequest(`/assigned-requests/accounts?accountTypeId=${accountTypeId}`, "GET");
};

export const getAssignedRequestTypes = (): Promise<ReferenceType[]> => {
  return apiClient.makeRequest(`/assigned-requests/request-types`, "GET");
};

export const getProjectDeveloperRequests = (
  requestParams: AssignedRequestFilters,
  page: number,
  sort?: string,
): Promise<{ content: ProjectDeveloperRequestsResponse[]; totalPages: number; pageNumber: number }> => {
  const filterParams = new URLSearchParams({});

  if (sort) {
    filterParams.append("sort", sort);
  }

  return apiClient.makeRequest(`/project-developer-requests/pages`, "POST", JSON.stringify(requestParams), {
    page,
    pageSize: 25,
    filters: filterParams,
  });
};

export const getProjectDeveloperProjectOptions = (): Promise<{ gsid: string; name: string }[]> => {
  return apiClient.makeRequest(`/project-developer-requests/projects`, "GET");
};

export const getRequestTypeSelectOptions = (referenceTypes: ReferenceTypeContextType): DropdownOption[] => {
  return referenceTypes.activeReferenceTypes.RequestType.filter((requestType) => requestType.active).map(
    (requestType) => ({
      value: requestType.id,
      label: requestType.description,
    }),
  );
};
