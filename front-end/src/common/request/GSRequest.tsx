import dayjs from "dayjs";

import { AccountType } from "../../components/PlatformAccounts/PlatformAccountTypes";
import {
  CreateRequestForm,
  MethodologyConfiguration,
  MonitoringPeriodConfiguration,
  ProjectIdWithRequestValues,
} from "../../components/requests/create-request/Wizard/Request";
import { BatchedProjectRequest } from "../../components/requests/Requests";
import { ReferenceType } from "../data-types/DataReferenceTypes";
import { PiiMetadata } from "../piiAnalysis/PiiAnalysis";
import { Conversation } from "../../components/requests/conversation/Conversation";

export type RequestStatusCode =
  | "NOT_REQUESTED"
  | "BOOKED"
  | "REVIEW_PLANNED_TO_START"
  | "TODO: UNKNOWN STATUS"
  | "COMPLETED_REJECTED"
  | "COMPLETED_APPROVED"
  | "IN_PREPARATION"
  | "OMITTED"
  | "CANCELLED"
  | "COMPLETENESS_CHECK"
  | "IN_PROGRESS"
  | "IN_AUDIT"
  | "IN_ASSIGNMENT"
  | "IN_ROUND_COMPLETION";

export interface GSRequest {
  id: number;
  startDate: string;
  endDate: string;
  bookingDate: string;
  requestType: {
    id: number;
    code: string;
    description: string;
  };
  index: number;
  reviewName: string;
  sameReviewTypeCount: number;
  reviewDocumentStatus: number;
  creditingPeriod: RequestCreditingPeriod;
  monitoringPeriod: RequestMonitoringPeriod;
  manuallyAdded: boolean;
  invoiceId: number | null;
  projectId: number;
  project: {
    gsid: string;
    id: number;
    name: string;
    programmeOfActivitiesId: number;
    projectClassificationId?: number;
    projectStatus?: {
      id: number;
      description: string;
    };
    piiMetadata?: PiiMetadata;
  };
  requestStatus: {
    id: number;
    code: RequestStatusCode;
    description: string;
  };
  currentRoundIndex: number | null;
  requestRounds: RequestRound[];
  requestStatusHistory: RequestStatusHistory[];
  inStakeholderConsultation: boolean;
  fastTracked?: boolean;
  internal?: boolean;
  emissionReductionAmount: number;
  batchId: number | null;
  batchTypeId: number | null;
  currentConsultationStartDate?: string;
  currentConsultationEndDate?: string;
  legacyBatchProjectIds?: number[];
  migrated: boolean;
  methodologies: MethodologyConfiguration[];
  latestRequestActivity?: RequestActivity;
  conversation?: Conversation;
  cancellationReason?: ReferenceType;
  otherCancellationReason?: string;
}

export interface RequestActivity {
  id: number;
  index: number;
  name: string;
  startDate: string;
  endDate?: string;
  dueDate: string;
  activity: Activity;
  skipped: boolean;
  account: ResponsibleAccount;
}

export interface TimelineActivity {
  index: number;
  name: string;
  startDate: string;
  dueDate: string;
  endDate?: string;
  completed: boolean;
  current: boolean;
  externalResponsibility: boolean;
  skipped: boolean;
}

export interface RequestCreditingPeriod {
  periodId: number | null;
  startDate: string | null;
  endDate: string | null;
  monitoringPeriods?: RequestMonitoringPeriod[];
}

export interface RequestMonitoringPeriod {
  id: number | null;
  startDate: string | null;
  endDate: string | null;
  feeSchedule: number | null;
}

interface Activity {
  id: number;
  index: number;
  name: string;
  displayName: string;
  totalDays: number;
  internal: boolean;
}

interface ResponsibleAccount {
  id: number;
  name: string;
  accountType: AccountType;
}

const closedRequestStatusCodes = ["COMPLETED_APPROVED", "COMPLETED_REJECTED", "CANCELLED", "OMITTED", "NOT_REQUESTED"];

const cancelledRequestStatusCodes = ["CANCELLED", "OMITTED"];

export function hasClosedStatus(request: GSRequest): boolean {
  return isClosedStatus(request.requestStatus);
}

export function hasCancelledStatus(request: GSRequest): boolean {
  if (!request.requestStatus) {
    return false;
  }
  return cancelledRequestStatusCodes.includes(request.requestStatus.code);
}

export function isClosedStatus(status?: ReferenceType): boolean {
  if (!status) {
    return false;
  }
  return closedRequestStatusCodes.includes(status.code);
}

export interface RequestStatusHistory {
  id: number;
  previousStatusId: number;
  nextStatusId: number;
  changedAt: string;
}

export interface RequestRound {
  roundIndex: number;
  startDate: string;
  endDate: string;
  statusId: number;
}

export interface CreateUpdateRequestPayload {
  id: number;
  requestTypeId: number;
  creditingPeriod: RequestCreditingPeriod;
  requestStatusId: number;
  emissionReductionAmount: number | undefined;
  startStakeholderConsultation?: boolean;
  manuallyAdded: boolean;
  startDate: string;
  methodologies: MethodologyConfiguration[];
  fastTrack: boolean | undefined;
  internal: boolean | undefined;
  monitoringPeriod: MonitoringPeriodConfiguration | null;
  cancellationReasonId: string | undefined;
  otherCancellationReason: string | undefined;
}

interface ProjectRequestInfo {
  projectId: number;
  creditingPeriod: RequestCreditingPeriod;
  emissionReductionAmount: number;
  methodologies: MethodologyConfiguration[];
}

export interface CreateBatchRequestPayload {
  requestTypeId: number;
  batchTypeId: number;
  projectRequestInfos: ProjectRequestInfo[];
}

export interface RequestConsultation {
  id: number;
  startDate: string;
  endDate: string;
  type: {
    id: number;
    userType: string;
    workingDays: number;
  };
  project: {
    gsid: number;
    id: number;
    name: string;
  };
  request: {
    id: number;
    index: number;
  };
}

const preliminaryReviewCodes = [
  "POA_DESIGN_CONSULTATION",
  "PRELIMINARY_REVIEW",
  "PRELIMINARY_REVIEW_TRANSITION",
  "PRELIMINARY_REVIEW_REGULAR_CASE_VPA",
  "PRELIMINARY_REVIEW_REAL_OR_REGULAR_CASE_POA",
];

export function isPreliminaryReview(request: GSRequest): boolean {
  return preliminaryReviewCodes.includes(request.requestType.code);
}

const CAN_CREATE_CREDITING_PERIOD_TYPES = [
  "DESIGN_CERTIFICATION_RENEWAL_|_PROJECT",
  "DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA",
  "RENEWAL_|_REAL_CASE_VPA",
  "RENEWAL_|_REGULAR_VPA",
];

const CAN_CREATE_MONITORING_PERIOD_TYPES = [
  "PERFORMANCE_CERTIFICATION_|_PROJECT",
  "PERFORMANCE_CERTIFICATION_|_VPA",
  "ISSUANCE",
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA",
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT",
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA",
  "GENERIC",
];

const CAN_SET_FEE_SCHEDULE_TYPES = [
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA",
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT",
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA",
  "PERFORMANCE_CERTIFICATION_|_PROJECT",
  "PERFORMANCE_CERTIFICATION_|_VPA",
  "GENERIC",
];

const EMISSION_REDUCTION_REQUIRED_TYPES = [
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA",
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT",
  "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA",
  "DESIGN_CERTIFICATION_RENEWAL_|_PROJECT",
  "DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA",
  "RENEWAL_|_REAL_CASE_VPA",
  "RENEWAL_|_REGULAR_VPA",
  "DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA",
  "DESIGN_CERTIFICATION_PROJECT",
  "DESIGN_CERTIFICATION_REAL_CASE_VPA",
  "DESIGN_CHANGE_|_WITH_VVB",
  "INCLUSION_REGULAR_VPA",
  "PRELIMINARY_REVIEW",
  "TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER",
];

const CAN_VIEW_FEE_SCHEDULE_TYPES = [...CAN_SET_FEE_SCHEDULE_TYPES, "ISSUANCE"];

export function canRequestTypeCreateCreditingPeriod(requestTypeCode: string) {
  return CAN_CREATE_CREDITING_PERIOD_TYPES.includes(requestTypeCode);
}

export function canRequestTypeCreateMonitoringPeriod(requestTypeCode: string) {
  return CAN_CREATE_MONITORING_PERIOD_TYPES.includes(requestTypeCode);
}

export function canRequestTypeSetFeeSchedule(requestTypeCode: string) {
  return CAN_SET_FEE_SCHEDULE_TYPES.includes(requestTypeCode);
}

export function canRequestTypeViewFeeSchedule(requestTypeCode: string) {
  return CAN_VIEW_FEE_SCHEDULE_TYPES.includes(requestTypeCode);
}

export function getFeeScheduleRequestTypeIds(requestTypes: ReferenceType[]) {
  return requestTypes.filter((x) => CAN_SET_FEE_SCHEDULE_TYPES.includes(x.code)).map((x) => x.id);
}

export function getEmissionReductionRequiredIds(requestTypes: ReferenceType[]) {
  return requestTypes.filter((x) => EMISSION_REDUCTION_REQUIRED_TYPES.includes(x.code)).map((x) => x.id);
}

export function isEmissionReductionRequired(requestTypeCode: string) {
  return EMISSION_REDUCTION_REQUIRED_TYPES.includes(requestTypeCode);
}

export function hasDraftCreditingPeriodValues(request: GSRequest): boolean {
  return (
    request.creditingPeriod?.periodId === null &&
    request.creditingPeriod?.startDate !== null &&
    request.creditingPeriod?.endDate !== null
  );
}

export function hasNewMonitoringPeriodValues(request: GSRequest): boolean {
  return (
    request.monitoringPeriod?.id === null &&
    request.monitoringPeriod?.startDate !== null &&
    request.monitoringPeriod?.endDate !== null
  );
}

export function isNullOrUndefined(value: unknown): boolean {
  return value == null || value == undefined;
}

const CLASSIFICATION_DESCRIPTIONS_TO_TEST = [
  "VPA/CPA",
  "Regular case VPA",
  "Real case VPA",
  "Real Case VPA",
  "Regular VPA",
];

export const findFirstVpa = (batchedProjects: BatchedProjectRequest[]) => {
  return batchedProjects.find((p) => CLASSIFICATION_DESCRIPTIONS_TO_TEST.includes(p.classification));
};

export const getFirstVpaMonitoringPeriodEndDate = (
  firstVpa: BatchedProjectRequest,
  firstVpaValues?: ProjectIdWithRequestValues,
) => {
  if (firstVpa?.creditingPeriods) {
    if (firstVpaValues?.monitoringPeriodId) {
      const selectedPeriod = firstVpa?.creditingPeriods.find(
        (period) => period.periodId == firstVpaValues?.creditingPeriodId,
      );
      if (selectedPeriod?.monitoringPeriods) {
        const monitoringPeriod = selectedPeriod?.monitoringPeriods.find(
          (period) => period.id == firstVpaValues?.monitoringPeriodId,
        );
        return monitoringPeriod?.endDate;
      }
    } else {
      const newEndDate = firstVpaValues?.newMonitoringPeriodEndDate;
      if (newEndDate?.isValid()) {
        return newEndDate?.toISOString();
      }
    }
  }
};

export const findMonitoringPeriodWithMatchingEndDate = (
  firstVpa: BatchedProjectRequest,
  project: BatchedProjectRequest,
  firstVpaValues?: ProjectIdWithRequestValues,
) => {
  const firstEndDate = getFirstVpaMonitoringPeriodEndDate(firstVpa, firstVpaValues);
  if (firstEndDate && project.creditingPeriods) {
    const monitoringPeriods = project.creditingPeriods.flatMap(({ periodId, monitoringPeriods }) =>
      monitoringPeriods?.map((period) => ({ creditingPeriodId: periodId, ...period })),
    );
    return monitoringPeriods.find((period) => (period ? sameDay(period?.endDate, firstEndDate) : false));
  }
};

const sameDay = (date1: string, date2: string) => {
  return dayjs.utc(date1).startOf("day").isSame(dayjs.utc(date2).startOf("day"));
};

export function hasNoRounds(request: GSRequest): boolean {
  return (
    request.requestRounds.length === 0 && (request.currentRoundIndex == undefined || request.currentRoundIndex === 0)
  );
}

const ISSUANCE_REQUEST_TYPE_ID = 1012;
export function isForIssuanceRequest(values: CreateRequestForm) {
  return values.requestType && Number(values.requestType) === ISSUANCE_REQUEST_TYPE_ID;
}
