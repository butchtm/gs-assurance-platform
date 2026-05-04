import dayjs from "dayjs";

import { ReferenceType } from "../../../common/data-types/DataReferenceTypes";
import { CreditingPeriod } from "../../../common/project/Project";
import { RequestCreditingPeriod } from "../../../common/request/GSRequest";
import { apiClient } from "../../APIClient/Client";
import { MethodologyConfiguration } from "../../requests/create-request/Wizard/Request";

export interface ProjectInConsultation {
  id?: number;
  name?: string;
  gsid: string;
  projectStatus: string;
  accountName: string;
  currentStandardVersion: string;
  projectDescription: string;
  masterProject: number;
  parentProject: number;
  masterProjectGsid: string;
  projectClassification: string;
  projectStartDate: string;
  registrationType: string;
  hasProjectAssets: boolean;
  carbonStream: string;
  projectSize: string;
  bundleId: number;
  designCertificationDate: string;
  estimatedAnnualUnits: number;
  listingDate: string;
  transitionProjectLinkInOtherScheme: string;
  transitionCreditingPeriodStartDate: string;
  transitionCreditingPeriodEndDate: string;
  transitionFromOtherScheme: string;
  transitionPathway: string;
  certifiedProjectDate: string;
  lastDesignCertificationRenewalDate: string;
  lastPerformanceReviewDate: string;
  consultationStartDate: string;
  consultationEndDate: string;
  projectLocations: string[];
  projectAssets: string[];
  projectSustainabilityGoals: string[];
  projectSustainabilityGoalsTypes: ReferenceType[];
  projectTypes: string[];
  projectMethodologies: string[];
  projectCreditingPeriods: CreditingPeriod[];
  requests: RequestInConsultation[];
  linkedBatchProjects: string[];
}

export interface RequestInConsultation {
  batchId: number | null;
  batchedProjectChildren: ProjectInConsultation[];
  comments: PublicStakeholderComment[];
  consultationEndDate: string;
  consultationStartDate: string;
  documents: PublicStakeholderDocument[];
  emissionReductionAmount: number;
  requestCreditingPeriod: RequestCreditingPeriod;
  requestId: number;
  requestName: string;
  requestStatus: string;
}

export interface ProjectInConsultationSummary {
  gsid: string;
  name: string;
  requests: RequestInConsultationSummary[];
  projectLocations: string[];
  projectMethodologies: string[];
}

export interface RequestInConsultationSummary {
  batchId: number | null;
  consultationEndDate: string;
  consultationStartDate: string;
  requestId: number;
  requestName: string;
  requestStatus: string;
  requestMethodologies?: MethodologyConfiguration[];
  requestCreditingPeriod?: CreditingPeriod;
}

export interface PublicStakeholderDocument {
  id: string;
  filename: string;
  access?: boolean;
  category?: boolean;
  type?: string;
  uploadedTimestamp: string;
  modifiedTimestamp: string;
  isLatest?: boolean;
  displayCategoryType: string;
  isMissing?: boolean;
}

export interface PublicStakeholderComment {
  id: number;
  submittedComment: string;
  uploadedByName: string;
  uploadedDate: string;
  anonymous: boolean;
  addressed: boolean;
}

export const getCreditingPeriod = (request: RequestInConsultation): string => {
  const period = request.requestCreditingPeriod;
  const label = period.periodId ? `Period ${period.periodId}` : "New Period";
  if (period?.startDate && period.endDate) {
    return `${label} - ${dayjs.utc(period.startDate).format("DD/MM/YYYY")} to ${dayjs
      .utc(period.endDate)
      .format("DD/MM/YYYY")}`;
  } else {
    return "Not set";
  }
};

export async function getPublicConsultation(currentPage: number): Promise<{
  data: ProjectInConsultationSummary[];
  totalPages: number;
  pageNumber: number;
}> {
  try {
    const response = await apiClient.makeRequest("/public/consultations/pages", "GET", undefined, {
      page: currentPage,
      pageSize: 25,
    });

    return {
      data: response.content,
      totalPages: response.totalPages,
      pageNumber: response.currentPage,
    };
  } catch (error) {
    console.error("Failed to fetch projects in consultation:", error);
    throw error;
  }
}

export function getSpecificProjectConsultation(gsid: string): Promise<ProjectInConsultation> {
  return apiClient.makeRequest(`/public/consultations/${gsid}`, "GET");
}

export function getRequestProjectGsid(requestId: number): Promise<string> {
  return apiClient.makeRequest(`/public/requests/${requestId}/gsid`, "GET");
}
