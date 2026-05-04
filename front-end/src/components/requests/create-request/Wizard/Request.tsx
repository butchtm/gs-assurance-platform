import { Dayjs } from "dayjs";

import { GSDocumentScrap } from "../../../../common/document/GSDocumentScrap";
import { FormValues } from "../../Requests";

export interface MethodologyConfiguration {
  methodologyId: number | "";
  methodologyDescription?: string;
  methodologyVersion: number | null;
  creditingPeriodId: number | null;
}

export interface MonitoringPeriodConfiguration {
  id: number | null;
  startDate: string | null;
  endDate: string | null;
  feeSchedule: number | null;
}

export interface CreateRequestForm extends FormValues {
  requiredDocuments: GSDocumentScrap[] | null;
  createBatchRequest: string;
  useNewCreditingPeriod: boolean;
  canCreateNewCreditingPeriod: boolean;
  batchRequestTypeId: number | null;
  batchProjectIds: number[];
  batchStepProjectValues: ParentRequestWithBatchedRequests | null;
  methodologyConfigurations: MethodologyConfiguration[];
  isMonitoringPeriodEnabled: boolean;
  canCreateNewMonitoringPeriod: boolean;
  useNewMonitoringPeriod: boolean;
}

export interface BatchRequestIdWithProjects {
  requestTypeId: number;
  creditingPeriodId: number;
  emissionReductionAmount: number;
  batchRequestTypeId: number;
  projects: number[];
}

export interface ProjectIdWithRequestValues {
  projectId: number;
  requestId: number;
  requestTypeId?: number;
  creditingPeriodId: number | null;
  startDate: string | null;
  endDate: string | null;
  emissionReductionAmount: number;
  methodologies: MethodologyConfiguration[];
  useNewCreditingPeriod: boolean;
  canCreateNewCreditingPeriod: boolean;
  newCreditingPeriodStartDate: string | null;
  newCreditingPeriodEndDate: string | null;
  isMonitoringPeriodEnabled: boolean;
  canCreateNewMonitoringPeriod: boolean;
  useNewMonitoringPeriod: boolean;
  monitoringPeriodId: number | null;
  newMonitoringPeriodStartDate: Dayjs | null;
  newMonitoringPeriodEndDate: Dayjs | null;
  feeSchedule: number | null;
}

export interface BatchTypeIdWithProjects {
  batchTypeId: number;
  [projectId: number]: ProjectIdWithRequestValues;
}

export interface ParentRequestWithBatchedRequests {
  requestTypeId: number | null;
  creditingPeriodId: number | null;
  startDate: string | null;
  emissionReductionAmount: number;
  batch: BatchTypeIdWithProjects;
  feeSchedule: number | null;
  finalProjectInitialSubmitCount?: number;
}
