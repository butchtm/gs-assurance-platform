import dayjs, { Dayjs } from "dayjs";

import { BatchedProjectRequests } from "../../components/requests/Requests";
import { PiiMetadata, TextAnalysisResponse } from "../piiAnalysis/PiiAnalysis";

export interface ReferenceType {
  id: number;
  code: string;
  description: string;
}

export interface Methodology {
  id: number;
  code?: string;
  description?: string;
  version: number | null;
  methodologyId?: number;
}

export interface MonitoringPeriod {
  id: number;
  startDate: string;
  endDate: string;
  originRequestId?: number | null;
  feeSchedule: number | null;
}

export interface CreditingPeriod {
  periodId: number;
  startDate: string;
  endDate: string;
  methodologies: Methodology[];
  monitoringPeriods?: MonitoringPeriod[];
}

export interface ProjectOverview {
  id: number | undefined;
  name: string | undefined;
  description: string | undefined;
  gsid: string | undefined;
  status: boolean;
  projectTypes: ReferenceType[] | undefined;
  projectStatus: ReferenceType;
  countries: CountryReferenceType[];
}

interface ProjectReference {
  id: number;
  gsid: string;
  name: string;
  projectClassificationId: number;
}

interface ProjectBundle {
  id: number;
  name: string;
}

export interface ProjectBundleInfo {
  id: number;
  gsid: string;
  name: string;
  currentRequest: {
    id: number;
    name: string;
  };
}

export interface ProjectBundleResponse {
  content: ProjectBundleInfo[];
  totalPages: number;
  pageNumber: number;
}

export interface BasicRiskSummary {
  risk: boolean;
  summary: string;
  dateApplied: string;
}

export interface CurrentRiskSummary extends BasicRiskSummary {
  projectId: number;
}

export interface DetailedRiskSummary extends CurrentRiskSummary {
  user: string;
}

export interface RiskSummaryResponse {
  content: DetailedRiskSummary[];
  pageSize: number;
  pageNumber: number;
  totalElements: number;
}

export interface Project {
  id: number;
  accountId: number;
  gsid: string;
  status: boolean;
  name: string;
  description: string;
  notes: string;
  projectDeveloperName: string;
  projectDeveloperEmail: string;
  streetAndNumber: string;
  regionStateProvince: string;
  cityTownCommunity: string;
  projectProgrammeActivities: ReferenceType;
  projectStartDate: string;
  projectCategory: ReferenceType;
  registrationType: ReferenceType;
  projectStatus: ReferenceType;
  projectStatusId: number | null;
  projectClassification: ReferenceType;
  projectClassificationId: number;
  sizeId: ReferenceType;
  projectTypes: ReferenceType[];
  parentProject: ProjectReference;
  masterProject: ProjectReference;
  currentRequestId: number;
  bundleId: number;
  bundle?: ProjectBundle;
  projectSize: ReferenceType;
  projectLocation: ReferenceType | null;
  projectLocations: ReferenceType[] | undefined;
  locationIds: [];
  methodologies: ReferenceType[] | undefined;
  certifiedProduct: ReferenceType[] | null;
  currentStandardVersion: ReferenceType;
  feeSchedule: ReferenceType;
  carbonStream: ReferenceType | null;
  sustainableDevelopmentGoals: ReferenceType[] | null;
  sdgImpactsCarbonStream: ReferenceType | null;
  estimatedAnnualUnits: number;
  hasAssets: boolean;
  projectAssets: ReferenceType[] | null;
  selectAsset: { label: string; value: string } | null;
  designCertificationDate: string;
  certifiedProjectDate: string;
  lastDesignCertificationRenewalDate: string;
  lastPerformanceReviewDate: string;
  listingDate: string;
  listingDateManualSet: boolean;
  creditingPeriods: CreditingPeriod[];
  transitionDetail: TransitionDetail | null;
  transitioningProject: boolean;
  transitingFromAnotherScheme: { label: string; value: string } | null;
  projectLinkInAnotherScheme: string;
  transitionPathway: { label: string; value: string | number } | null;
  creditingPeriodOtherScheme: Date;
  energyProject: EnergyProject;
  countries: CountryReferenceType[];
  currentRiskSummary?: CurrentRiskSummary;
  piiMetadata?: PiiMetadata;
  migrated: boolean;
  displayNotRequestedStatusRequests: boolean | null;
  registryUrl: string;
}

export interface CountryReferenceType {
  id: number;
  countryCode: string;
  name: string;
}

export interface EnergyProject {
  projectLinkInOtherScheme: string | null;
  creditingPeriodStartDateOtherSchemes: string | null;
  creditingPeriodEndDateOtherSchemes: string | null;
  transitionApprovalProcedureId: ReferenceType | null;
  transitionFromOtherSchemeId: ReferenceType | null;
  transitionPathwayId: ReferenceType | null;
}

export interface CreateProjectForm {
  accountId: number | null;
  accountName: string | null;
  projectClassificationId: number | null;
  masterProjectId: number | null;
  parentProjectId: number | null;
  locationIds: number[];
  description: string;
  sizeId: number | null;
  typeIds: number[];
  transitioningProject: string;
  name: string;
  startDate: string | null;
  registrationTypeId: number | null;
  projectAssetIds: number[];
  carbonStreamId: number | null;
  transitionFromOtherSchemeId: number | null;
  creditingPeriods: CreateCreditingPeriod[];
  creditingPeriodStartDateOtherSchemes: string | null;
  creditingPeriodEndDateOtherSchemes: string | null;
  transitionPathwayId: number | null;
  transitionApprovalProcedureId: number | null;
  projectLinkInOtherScheme: string | undefined;
  estimatedAnnualUnits: number | null;
  sustainableDevelopmentGoalIds: number[];
  feeScheduleId: number | null;
  notes: string;
  currentStandardId: number | null;
  designCertificationDate: string | null;
  certifiedProjectDate: string | null;
  lastDesignCertificationRenewalDate: string | null;
  lastPerformanceReviewDate: string | null;
  projectStatusId: number | null;
  listingDate: string | null;
  listingDateManualSet: boolean | null;
  textAnalysisResults: TextAnalysisResponse | null;
  displayNotRequestedStatusRequests: boolean | null;
  masterValues?: {
    locationIds?: number[];
    carbonStreamId?: number | null;
    sustainableDevelopmentGoalIds?: number[];
    sizeId?: number | null;
    startDate?: string | null;
    projectAssetIds?: number[];
    typeIds?: number[];
  };
}

export interface CreateProjectPayload {
  projectClassificationId: number | null;
  locationIds: number[];
  description: string;
  sizeId: number | null;
  typeIds: number[];
  name: string;
  startDate: string | null;
  registrationTypeId: number | null;
  projectAssetIds: number[];
  carbonStreamId: number | null;
  estimatedAnnualUnits: number | null;
  sustainableDevelopmentGoalIds: number[];
  feeScheduleId: number | null;
  notes: string;
  transitionProject: TransitionProjectDetails | null;
  accountId: number;
  masterProjectId: number | null;
  parentProjectId: number | null;
  currentStandardId: number;
  creditingPeriods: CreateCreditingPeriod[] | null;
}

export interface TransitionProjectDetails {
  projectLinkInOtherScheme: string | undefined;
  creditingPeriodStartDateOtherSchemes: string | null;
  creditingPeriodEndDateOtherSchemes: string | null;
  transitionApprovalProcedureId: number | null;
  transitionFromOtherSchemeId: number | null;
  transitionPathwayId: number | null;
}

export interface TransitionDetail {
  projectLinkInOtherScheme: string;
  creditingPeriodStartDateOtherSchemes: string | null;
  creditingPeriodEndDateOtherSchemes: string | null;
  transitionApprovalProcedure: ReferenceType | null;
  transitionFromOtherScheme: ReferenceType | null;
  transitionPathway: ReferenceType | null;
}

export interface UpdateProjectPayload {
  accountId: number | null;
  projectClassificationId: number | null;
  locationIds: number[];
  description: string;
  sizeId: number | null;
  typeIds: number[];
  name: string;
  startDate: string | null;
  registrationTypeId: number | null;
  projectAssetIds: number[];
  carbonStreamId: number | null;
  estimatedAnnualUnits: number | null;
  sustainableDevelopmentGoalIds: number[];
  feeScheduleId: number | null;
  notes: string;
  transitionProject: TransitionProjectDetails | null;
  masterProjectId: number | null;
  parentProjectId: number | null;
  creditingPeriods: CreateCreditingPeriod[] | null;
  currentStandardId: number | null;
  designCertificationDate: string | null;
  certifiedProjectDate: string | null;
  lastDesignCertificationRenewalDate: string | null;
  lastPerformanceReviewDate: string | null;
  listingDate: string | null;
  listingDateManualSet: boolean | null;
  displayNotRequestedStatusRequests: boolean | null;
}

export interface CreateCreditingPeriod {
  periodId: number;
  startDate: string;
  endDate: string;
  methodologies: CreateMethodology[];
  monitoringPeriods?: CreateMonitoringPeriod[];
}

export interface CreateMethodology {
  id: number | null;
  version: number | null;
}

interface CreateMonitoringPeriod {
  id: number;
  startDate: string;
  endDate: string;
  feeSchedule: number | null;
}

export enum ProjectStatus {
  New = "NEW",
  Listed = "LISTED",
  DesignCertified = "DESIGN_CERTIFIED",
  CertifiedProject = "CERTIFIED_PROJECT",
  DeCertified = "DE_CERTIFIED",
}

export const mockBatchedProjectsRealCase: BatchedProjectRequests = {
  parent: {
    id: 1,
    name: "Real Case VPA 1",
    classification: "Real Case VPA",
    requestId: 1,
    gsid: "GS1",
    methodologies: [],
  },
  children: [
    {
      id: 1,
      name: "Real Case VPA 1",
      classification: "Real Case VPA",
      requestId: 2,
      gsid: "GS1",
      methodologies: [],
    },
    {
      id: 2,
      name: "Real Case VPA 2",
      classification: "Real Case VPA",
      requestId: 2,
      gsid: "GS2",
      methodologies: [],
    },
    {
      id: 3,
      name: "Real Case VPA 3",
      classification: "Real Case VPA",
      requestId: 3,
      gsid: "GS3",
      methodologies: [],
    },
  ],
};

const POA_CLASSIFICATION_CODES = ["POA", "PROGRAMME_OF_ACTIVITIES", "REAL_OR_REGULAR_CASE_POA"];

export const isPoa = (classification: ReferenceType) => {
  return POA_CLASSIFICATION_CODES.includes(classification?.code);
};

export const findNewCreditingPeriodStartDate = (periods: CreditingPeriod[]): Dayjs => {
  const latestPeriod = findLatestCreditingPeriod(periods);
  return latestPeriod?.endDate ? dayjs.utc(latestPeriod.endDate).add(1, "day") : dayjs.utc();
};

export const findLatestCreditingPeriod = (periods: CreditingPeriod[]): CreditingPeriod | null => {
  return [...periods].sort((a, b) => dayjs.utc(b.endDate).unix() - dayjs.utc(a.endDate).unix())[0];
};

export const findAllMethodologyIds = (periods: CreateCreditingPeriod[]): number[] => {
  const isNotNull = <T,>(item: T | null): item is T => item !== null;
  return findAllMethodologies(periods)
    .map(({ id }) => id)
    .filter(isNotNull);
};

const findAllMethodologies = (periods: CreateCreditingPeriod[]): CreateMethodology[] => {
  if (periods?.length) {
    return periods.flatMap(({ methodologies }) => methodologies);
  }
  return [];
};

interface Period {
  startDate: string;
  endDate: string;
}

function toPreviousPeriod(periods: Period[], index: number): Period | null {
  const prevousIndex = index - 1;
  if (prevousIndex < 0) {
    return null;
  }
  return periods[prevousIndex];
}

function toNextPeriod(periods: Period[], index: number): Period | null {
  const nextIndex = index + 1;
  if (nextIndex >= periods.length) {
    return null;
  }
  return periods[nextIndex];
}

function toLastPeriod(periods: Period[]): Period | null {
  if (periods.length < 1) {
    return null;
  }
  return periods[periods.length - 1];
}

function addOneDay(date: string): Dayjs {
  return dayjs.utc(date).add(1, "day");
}

function subtractOneDay(date: string): Dayjs {
  return dayjs.utc(date).subtract(1, "day");
}

function toDayBeforeNextStartDate(periods: Period[], index: number): Dayjs | undefined {
  const nextPeriod = toNextPeriod(periods, index);
  if (nextPeriod) {
    return subtractOneDay(nextPeriod.startDate);
  }
  return undefined;
}

export function toMinStartDate(periods: Period[], index: number): Dayjs | undefined {
  const previousPeriod = toPreviousPeriod(periods, index);
  if (previousPeriod) {
    return addOneDay(previousPeriod.endDate);
  }
  return undefined;
}

export function toMaxStartDate(periods: Period[], index: number): Dayjs | undefined {
  return toDayBeforeNextStartDate(periods, index);
}

export function toMinEndDate(startDate: string): Dayjs {
  return addOneDay(startDate);
}

export function toMaxEndDate(periods: Period[], index: number): Dayjs | undefined {
  return toDayBeforeNextStartDate(periods, index);
}

export function toNewStartDate(periods: Period[]): Dayjs {
  const lastPeriod = toLastPeriod(periods);
  if (lastPeriod) {
    return addOneDay(lastPeriod.endDate);
  }
  return dayjs.utc();
}

export function toDefaultEndDate(startDate: Dayjs): Dayjs {
  return startDate.add(5, "year").subtract(1, "day");
}

function sanitizeMethodologyForComparison(methodologies: Methodology[] | undefined) {
  return methodologies?.map(({ code, description, version }) => ({ code, description, version }));
}

export function methodologiesExactlyTheSame(setOne: Methodology[] | undefined, setTwo: Methodology[] | undefined) {
  return (
    JSON.stringify(sanitizeMethodologyForComparison(setOne)) != JSON.stringify(sanitizeMethodologyForComparison(setTwo))
  );
}
