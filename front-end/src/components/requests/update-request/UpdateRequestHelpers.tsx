import { useLocation, useNavigate, type Location } from "react-router-dom";
import { toast } from "react-toastify";
import dayjs, { Dayjs } from "dayjs";

import {
  GSRequest,
  hasDraftCreditingPeriodValues,
  hasNewMonitoringPeriodValues,
  isNullOrUndefined,
  RequestCreditingPeriod,
  RequestMonitoringPeriod,
} from "../../../common/request/GSRequest";
import {
  BatchTypeIdWithProjects,
  ParentRequestWithBatchedRequests,
  ProjectIdWithRequestValues,
} from "../create-request/Wizard/Request";
import { BatchedProjectRequests, FormValues, updateRequest } from "../Requests";
import { getBatchedFeeScheduleValue } from "../create-request/Wizard/FeeSchedule";

const transformRequestToProjectIdWithRequestValues = (request: GSRequest): ProjectIdWithRequestValues => {
  const usesNewCreditingPeriod = hasDraftCreditingPeriodValues(request);
  const usesNewMonitoringPeriod = hasNewMonitoringPeriodValues(request);
  return {
    projectId: request.project.id,
    requestId: request.id,
    requestTypeId: request.requestType.id,
    creditingPeriodId: request.creditingPeriod.periodId ? Number(request.creditingPeriod.periodId) : null,
    startDate: request.startDate,
    endDate: request.endDate,
    emissionReductionAmount: request.emissionReductionAmount,
    methodologies: request.methodologies,
    useNewCreditingPeriod: usesNewCreditingPeriod,
    canCreateNewCreditingPeriod: usesNewCreditingPeriod,
    newCreditingPeriodStartDate: usesNewCreditingPeriod ? request.creditingPeriod.startDate : null,
    newCreditingPeriodEndDate: usesNewCreditingPeriod ? request.creditingPeriod.endDate : null,
    monitoringPeriodId: request.monitoringPeriod.id ?? null,
    isMonitoringPeriodEnabled:
      usesNewMonitoringPeriod ||
      !!(request.monitoringPeriod.id && !isNullOrUndefined(request.monitoringPeriod.feeSchedule)),
    useNewMonitoringPeriod: usesNewMonitoringPeriod,
    canCreateNewMonitoringPeriod: usesNewMonitoringPeriod,
    newMonitoringPeriodStartDate: usesNewMonitoringPeriod ? dayjs.utc(request.monitoringPeriod.startDate) : null,
    newMonitoringPeriodEndDate: usesNewMonitoringPeriod ? dayjs.utc(request.monitoringPeriod.endDate) : null,
    feeSchedule: request.monitoringPeriod.feeSchedule,
  };
};

export const transformToParentRequestWithBatchedRequests = (
  gsRequests: GSRequest[],
  batchedProjectsRequests: BatchedProjectRequests,
): ParentRequestWithBatchedRequests => {
  const parentRequest = gsRequests.find((request) => request.id === batchedProjectsRequests.parent.requestId);

  if (!parentRequest) {
    throw new Error("Parent request not found");
  }

  const batch: BatchTypeIdWithProjects = {
    batchTypeId: parentRequest.batchTypeId ?? 0,
  };

  batch[batchedProjectsRequests.parent.id] = transformRequestToProjectIdWithRequestValues(parentRequest);

  batchedProjectsRequests.children.forEach((child) => {
    const childRequest = gsRequests.find((request) => request.id === child.requestId);
    if (childRequest) {
      batch[childRequest.project.id] = transformRequestToProjectIdWithRequestValues(childRequest);
    }
  });

  const firstChildOfBatch = batchedProjectsRequests.children[0];
  const firstChildRequest = batch[firstChildOfBatch.id];

  return {
    requestTypeId: parentRequest.requestType.id,
    creditingPeriodId: parentRequest.id,
    startDate: parentRequest.currentConsultationStartDate ?? null,
    emissionReductionAmount: parentRequest.emissionReductionAmount,
    batch,
    feeSchedule: getBatchedFeeScheduleValue(parentRequest, firstChildOfBatch, firstChildRequest),
  };
};

const getCreditingPeriodForUpdate = (
  periodId: number | null,
  newPeriodStartDate: string | null,
  newPeriodEndDate: string | null,
): RequestCreditingPeriod => {
  const useNewPeriod = Boolean(newPeriodStartDate) && Boolean(newPeriodEndDate);
  return {
    periodId: useNewPeriod ? null : periodId,
    startDate: useNewPeriod ? newPeriodStartDate : null,
    endDate: useNewPeriod ? newPeriodEndDate : null,
  };
};

const getMonitoringPeriodForUpdate = (
  periodId: number | null,
  newPeriodStartDate: Dayjs | null,
  newPeriodEndDate: Dayjs | null,
  feeSchedule: number | null,
): RequestMonitoringPeriod => {
  const useNewPeriod = Boolean(newPeriodStartDate) && Boolean(newPeriodEndDate);
  return {
    id: useNewPeriod ? null : periodId,
    startDate: useNewPeriod && newPeriodStartDate ? newPeriodStartDate?.toISOString() : null,
    endDate: useNewPeriod && newPeriodEndDate ? newPeriodEndDate?.toISOString() : null,
    feeSchedule: feeSchedule,
  };
};

export function useUpdateRequest() {
  const navigate = useNavigate();
  const location: Location<{
    request: GSRequest;
    requestBatches: BatchedProjectRequests;
  }> = useLocation();

  return async (formValues: FormValues) => {
    try {
      if (formValues.batchStepProjectValues) {
        const requestsToUpdate: [string, ProjectIdWithRequestValues][] = Object.entries(
          formValues.batchStepProjectValues.batch,
        ).filter(([key]) => key !== "batchTypeId");
        const parentRequest = requestsToUpdate[0][1];
        await updateRequest(
          {
            id: parentRequest.requestId,
            creditingPeriod: getCreditingPeriodForUpdate(
              parentRequest.creditingPeriodId,
              parentRequest.newCreditingPeriodStartDate,
              parentRequest.newCreditingPeriodEndDate,
            ),
            emissionReductionAmount: parentRequest.emissionReductionAmount,
            methodologies: parentRequest.methodologies,
            monitoringPeriod: getMonitoringPeriodForUpdate(
              parentRequest.monitoringPeriodId,
              parentRequest.newMonitoringPeriodStartDate,
              parentRequest.newMonitoringPeriodEndDate,
              parentRequest.isMonitoringPeriodEnabled || !isNullOrUndefined(parentRequest.monitoringPeriodId)
                ? formValues.feeSchedule
                : null,
            ),
            paa: formValues.paa,
          },
          parentRequest.projectId,
        );
        const childRequests = requestsToUpdate.splice(1, requestsToUpdate.length - 1);
        await Promise.all(
          childRequests.map(([projectId, projectData]) =>
            updateRequest(
              {
                id: projectData.requestId,
                creditingPeriod: getCreditingPeriodForUpdate(
                  projectData.creditingPeriodId,
                  projectData.newCreditingPeriodStartDate,
                  projectData.newCreditingPeriodEndDate,
                ),
                emissionReductionAmount: projectData.emissionReductionAmount,
                methodologies: projectData.methodologies,
                monitoringPeriod: getMonitoringPeriodForUpdate(
                  projectData.monitoringPeriodId,
                  projectData.newMonitoringPeriodStartDate,
                  projectData.newMonitoringPeriodEndDate,
                  formValues.feeSchedule ?? null,
                ),
                paa: formValues.paa,
              },
              parseInt(projectId, 10),
            ),
          ),
        );
        toast.success(`Requests updated successfully`);
        navigate(`/projects/${requestsToUpdate[0][1].projectId}/requests?expand=${requestsToUpdate[0][1].requestId}`);
      } else {
        await updateRequest(
          {
            id: location.state.request.id,
            creditingPeriod: getCreditingPeriodForUpdate(
              formValues.creditingPeriodId,
              formValues.newCreditingPeriodStartDate,
              formValues.newCreditingPeriodEndDate,
            ),
            emissionReductionAmount: formValues.emissionReductionAmount,
            methodologies: formValues.methodologyConfigurations,
            monitoringPeriod: {
              id: formValues.monitoringPeriodId,
              startDate: formValues.newMonitoringPeriodStartDate?.toISOString() ?? null,
              endDate: formValues.newMonitoringPeriodEndDate?.toISOString() ?? null,
              feeSchedule: formValues.feeSchedule,
            },
            paa: formValues.paa,
          },
          location.state.request.project?.id,
        );
        toast.success(`Request updated successfully`);
        navigate(`/projects/${location.state.request.project?.id}/requests?expand=${location.state.request.id}`);
      }
    } catch (error) {
      console.error("Failed to update request(s):", error);
      toast.error("Failed to update request(s)");
    }
  };
}
