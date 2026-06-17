import dayjs from "dayjs";
import { MRT_SortingState } from "material-react-table";
import { toast } from "react-toastify";

import {
  BasicRiskSummary,
  CreateProjectForm,
  CreateProjectPayload,
  Project,
  ProjectBundleInfo,
  ProjectBundleResponse,
  RiskSummaryResponse,
  UpdateProjectPayload,
} from "../../common/project/Project";
import { apiClient } from "../APIClient/Client";

import { MethodologyResponse } from "./view-project/key-project-information/MethodologiesHistory";

interface GetProjectsResponse {
  content: Project[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
}

export const predefinedGSV = "GS4GG";

export interface ProjectTree {
  gsid: string;
  id: number;
  name: string;
  classificationId: number;
  bundle?: ProjectBundleInfo;
  children?: ProjectTree[];
}

export const getInitialEmptyValues = (): CreateProjectForm => {
  return {
    accountId: null,
    accountName: null,
    projectClassificationId: null,
    masterProjectId: null,
    parentProjectId: null,
    locationIds: [],
    name: "",
    description: "",
    projectAssetIds: [],
    typeIds: [],
    transitioningProject: "",
    creditingPeriodStartDateOtherSchemes: dayjs.utc().toISOString(),
    creditingPeriodEndDateOtherSchemes: dayjs.utc().add(7, "year").subtract(1, "day").toISOString(),
    registrationTypeId: null,
    carbonStreamId: null,
    estimatedAnnualUnits: null,
    sustainableDevelopmentGoalIds: [],
    feeScheduleId: null,
    sizeId: null,
    startDate: null,
    transitionFromOtherSchemeId: null,
    transitionPathwayId: null,
    transitionApprovalProcedureId: null,
    notes: "",
    projectLinkInOtherScheme: "",
    creditingPeriods: [
      {
        periodId: 1,
        startDate: dayjs.utc().toISOString(),
        endDate: dayjs.utc().add(5, "year").subtract(1, "day").toISOString(),
        methodologies: [{ id: -1, version: null }],
      },
    ],
    currentStandardId: null,
    designCertificationDate: null,
    certifiedProjectDate: null,
    lastDesignCertificationRenewalDate: null,
    lastPerformanceReviewDate: null,
    projectStatusId: 1, // NEW
    listingDate: null,
    listingDateManualSet: null,
    textAnalysisResults: null,
    displayNotRequestedStatusRequests: null,
  };
};

export async function getAllProjectsForAccount(accountId: number): Promise<Project[]> {
  const filters = {
    accountId: accountId.toString(),
  };
  return await getAllProjects(filters);
}
type stringOrNumberOrNumberListOrNull = string | number | null | number[];
export async function getAllProjects(
  filters?: Record<string, stringOrNumberOrNumberListOrNull> | null,
): Promise<Project[]> {
  const pageSize = 99999;
  const firstPage = await getProjectPage(1, filters, pageSize);
  const totalPages = firstPage.totalPages;

  const requests = [];
  for (let page = 1; page < totalPages; page++) {
    requests.push(makeProjectPageRequest(page, filters, pageSize));
  }

  const responses = await Promise.all(
    requests.map((p) =>
      p.catch((e) => {
        console.error("Error fetching a page:", e);
        return { content: [] }; // Return empty array on error
      }),
    ),
  );

  return firstPage.content.concat(
    responses.reduce((acc: Project[], page) => {
      return acc.concat(page.content);
    }, []),
  );
}

export async function getProjectPage(
  page: number,
  filters?: Record<string, stringOrNumberOrNumberListOrNull> | null,
  pageSize?: number,
): Promise<GetProjectsResponse> {
  try {
    if (!pageSize) {
      pageSize = 25;
    }

    const queryParams = new URLSearchParams({});
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value) queryParams.set(key, value.toString());
      });
    }

    return await makeProjectPageRequest(page, filters, pageSize);
  } catch (error) {
    console.error("Error fetching projects:", error);
    throw error;
  }
}

function makeProjectPageRequest(
  page: number,
  filters?: Record<string, stringOrNumberOrNumberListOrNull> | null,
  pageSize?: number,
): Promise<GetProjectsResponse> {
  const queryParams = new URLSearchParams({});
  if (filters) {
    Object.entries(filters).forEach(([key, value]) => {
      if (value) queryParams.set(key, value.toString());
    });
  }
  return apiClient.makeRequest("/projects", "GET", "{}", { page, pageSize, filters: queryParams });
}

export function getProject(projectId: number): Promise<Project> {
  return apiClient.makeRequest(`/projects/${projectId}`, "GET");
}

export interface CandidateParentDetailsRequest {
  accountId: number;
  classificationId: number;
  candidateProjectId: number;
  masterProjectId?: number | null;
}

export type CandidateParentProjectDetails = Pick<
  Project,
  | "id"
  | "gsid"
  | "name"
  | "projectClassificationId"
  | "projectClassification"
  | "projectStatus"
  | "projectStartDate"
  | "carbonStream"
  | "projectSize"
  | "projectTypes"
  | "projectLocations"
  | "projectAssets"
  | "sustainableDevelopmentGoals"
  | "creditingPeriods"
>;

export function getCandidateParentDetails(
  request: CandidateParentDetailsRequest,
): Promise<CandidateParentProjectDetails> {
  return apiClient.makeRequest(`/projects/candidate-parent-details`, "POST", JSON.stringify(request));
}

export function getProjectByGsid(gsid: string): Promise<Project> {
  return apiClient.makeRequest(`/projects/gsid/${gsid}`, "GET");
}

export const createProject = (payload: CreateProjectPayload) => {
  return apiClient.makeRequest(`/projects`, "POST", JSON.stringify(payload));
};

export const updateProject = (payload: UpdateProjectPayload, projectId: number) => {
  try {
    return apiClient.makeRequest(`/projects/${projectId}`, "PUT", JSON.stringify(payload));
  } catch {
    toast.error("Failed to update project.");
    return false;
  }
};

export const updateProjectStatus = (payload: { id: number }, projectId: number) => {
  try {
    return apiClient.makeRequest(`/projects/${projectId}/status`, "PUT", JSON.stringify(payload));
  } catch (error) {
    console.error("Error updating project status:", error);
    return false;
  }
};

export const getProjectBundles = (bundleId: number, page: number): Promise<ProjectBundleResponse> => {
  return apiClient.makeRequest(`/bundles/${bundleId}/projects/pages`, "GET", "{}", {
    page,
    pageSize: 5,
  });
};

export const getBatchProjects = async (batchProjectIds: number[], page: number): Promise<GetProjectsResponse> => {
  const queryParams = new URLSearchParams({});

  const queryString = batchProjectIds.join(",");
  queryParams.set("projectIds", queryString);

  const response = await apiClient.makeRequest("/projects", "GET", "{}", { page, pageSize: 5, filters: queryParams });

  if (response?.content?.length > 0) {
    const detailedProjects = await Promise.all(
      response.content.map(async (project: Project) => {
        const fullProjectDetails = await getProject(project.id);
        return {
          ...project,
          creditingPeriods: fullProjectDetails.creditingPeriods,
        };
      }),
    );

    response.content = detailedProjects;
  }

  return response;
};

export const getProjectTree = async (projectId: number): Promise<ProjectTree> => {
  return await apiClient.makeRequest(`/projects/${projectId}/trees`, "GET");
};

export const getCandidatePOAs = (accountId: number, classificationId: number): Promise<Project[]> => {
  return apiClient.makeRequest(`/projects/candidate-poas`, "POST", JSON.stringify({ accountId, classificationId }));
};

export const getCandidateRealCaseVPAs = (
  accountId: number,
  masterProjectId: number,
  classificationId: number | null,
): Promise<Project[]> => {
  return apiClient.makeRequest(
    `/projects/candidate-real-case-vpas`,
    "POST",
    JSON.stringify({ accountId, masterProjectId, classificationId }),
  );
};

export const getMethodologyHistory = (
  projectId: number,
  periodId: string,
  sortingState: MRT_SortingState,
  page: number,
  pageSize: number,
): Promise<MethodologyResponse> => {
  const queryParams = new URLSearchParams();
  if (sortingState.length > 0) {
    const directionString = sortingState[0].desc ? "desc" : "asc";
    queryParams.set("sort", `${sortingState[0].id.trim()},${directionString}`);
  }
  return apiClient.makeRequest(`/projects/${projectId}/methodology-history/pages?periodId=${periodId}`, "GET", "{}", {
    page,
    pageSize,
    filters: queryParams,
  });
};

export const updateProjectRisk = async (projectId: number, riskSummary: BasicRiskSummary) => {
  try {
    await apiClient.makeRequest(`/projects/${projectId}/risk-summaries`, "POST", JSON.stringify(riskSummary));
  } catch (error) {
    console.error(error);
    toast.error("Unable to update risk flag status");
  }
};

export const getProjectRiskHistory = (
  projectId: number,
  sortingState: MRT_SortingState,
  page: number,
  pageSize: number,
): Promise<RiskSummaryResponse> => {
  const queryParams = new URLSearchParams();
  if (sortingState.length > 0) {
    const directionString = sortingState[0].desc ? "desc" : "asc";
    queryParams.set("sort", `${sortingState[0].id.trim()},${directionString}`);
  }
  return apiClient.makeRequest(`/projects/${projectId}/risk-summaries`, "GET", "{}", {
    page,
    pageSize,
    filters: queryParams,
  });
};
