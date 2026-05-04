import dayjs from "dayjs";
import * as Yup from "yup";

import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";
import { isMethodologyUpdateRequestType } from "../../../../common/data-types/MethodologyRequestTypes";
import { findFirstVpa } from "../../../../common/request/GSRequest";
import { BatchedProjectRequest } from "../../Requests";

import {
  GENERIC_REQUEST_ID,
  isNewMonitoringPeriodEndDateValid,
  isNewMonitoringPeriodStartDateValid,
  methodologyShapeSchema,
} from "./CreateRequestSchema";

const getIndividualBatchedProjectSchema = (
  batchedProjects: BatchedProjectRequest[],
  currentProjectId: number,
  requestTypeId: number,
  requestTypes: ReferenceType[],
) => {
  const currentProject = batchedProjects.find((project) => project.id == currentProjectId);
  const firstVpa = findFirstVpa(batchedProjects);
  const isFirstVpa = firstVpa != undefined && firstVpa.id === currentProjectId;

  return Yup.object().shape({
    creditingPeriodId: Yup.number().when(["newCreditingPeriodStartDate", "newCreditingPeriodEndDate"], {
      is: (startDate: string | null, endDate: string | null) => !startDate && !endDate,
      then: (creditingPeriodId) => creditingPeriodId.required("Please select a crediting period").min(1).max(10),
      otherwise: (creditingPeriodId) => creditingPeriodId.nullable(),
    }),
    newCreditingPeriodStartDate: Yup.string().nullable(),
    newCreditingPeriodEndDate: Yup.string().nullable(),
    monitoringPeriodId: Yup.number().when(["isMonitoringPeriodEnabled", "useNewMonitoringPeriod"], {
      is: (isMonitoringPeriodEnabled: boolean, useNewMonitoringPeriod: boolean) =>
        isMonitoringPeriodEnabled && !useNewMonitoringPeriod && requestTypeId !== Number(GENERIC_REQUEST_ID),
      then: (monitoringPeriodId) => monitoringPeriodId.required("Please select a monitoring period"),
      otherwise: (monitoringPeriodId) => monitoringPeriodId.nullable(),
    }),
    newMonitoringPeriodStartDate: Yup.date().when(
      ["isMonitoringPeriodEnabled", "canCreateNewMonitoringPeriod", "useNewMonitoringPeriod"],
      {
        is: (
          isMonitoringPeriodEnabled: boolean,
          canCreateNewMonitoringPeriod: boolean,
          useNewMonitoringPeriod: boolean,
        ) =>
          isMonitoringPeriodEnabled &&
          canCreateNewMonitoringPeriod &&
          useNewMonitoringPeriod &&
          requestTypeId !== Number(GENERIC_REQUEST_ID),
        then: (newMonitoringPeriodStartDate) =>
          newMonitoringPeriodStartDate.test(
            "start_date_test",
            "Start date must be within crediting period and not overlap with any other monitoring periods",
            function (value) {
              const { newMonitoringPeriodEndDate, creditingPeriodId } = this.parent;
              const startDate = dayjs.utc(value).startOf("day");
              const endDate = dayjs.utc(newMonitoringPeriodEndDate).startOf("day");
              const isValid = isNewMonitoringPeriodStartDateValid(
                startDate,
                endDate,
                creditingPeriodId,
                currentProject?.creditingPeriods ?? [],
                !isFirstVpa,
              );
              return isValid;
            },
          ),
        otherwise: (newMonitoringPeriodStartDate) => newMonitoringPeriodStartDate.nullable(),
      },
    ),
    newMonitoringPeriodEndDate: Yup.date().when(
      ["isMonitoringPeriodEnabled", "canCreateNewMonitoringPeriod", "useNewMonitoringPeriod"],
      {
        is: (
          isMonitoringPeriodEnabled: boolean,
          canCreateNewMonitoringPeriod: boolean,
          useNewMonitoringPeriod: boolean,
        ) =>
          isMonitoringPeriodEnabled &&
          canCreateNewMonitoringPeriod &&
          useNewMonitoringPeriod &&
          requestTypeId !== Number(GENERIC_REQUEST_ID),
        then: (newMonitoringPeriodEndDate) =>
          newMonitoringPeriodEndDate.test(
            "end_date_test",
            "End date must be within crediting period, after the end date and not overlap with any other monitoring periods",
            function (value) {
              const { newMonitoringPeriodStartDate, creditingPeriodId } = this.parent;
              const startDate = dayjs.utc(newMonitoringPeriodStartDate).startOf("day");
              const endDate = dayjs.utc(value).startOf("day");
              if (!isFirstVpa) {
                return true;
              }
              const isValid = isNewMonitoringPeriodEndDateValid(
                startDate,
                endDate,
                creditingPeriodId,
                currentProject?.creditingPeriods ?? [],
              );
              return isValid;
            },
          ),
        otherwise: (newMonitoringPeriodEndDate) => newMonitoringPeriodEndDate.nullable(),
      },
    ),
    emissionReductionAmount: Yup.number().required("Please enter an emission reduction amount"),
    methodologies: toProjectMethodologiesSchema(requestTypeId, requestTypes),
  });
};

export const getStepBatchedSchema = (
  batchedProjects: BatchedProjectRequest[],
  currentProjectId: number,
  requestTypeId: number,
  requestTypes: ReferenceType[],
) => {
  const projectIds = batchedProjects.map((project) => project.id);

  // GS need to be able to go back and forth between batch projects so we only validate all projects on the last step
  const isLastProjectInBatch = projectIds.at(-1) == currentProjectId;
  if (!isLastProjectInBatch) {
    return null;
  }

  return Yup.object().shape({
    batchStepProjectValues: Yup.object().shape({
      batch: Yup.object(
        Object.fromEntries(
          projectIds.map((id) => [
            String(id),
            getIndividualBatchedProjectSchema(batchedProjects, id, requestTypeId, requestTypes),
          ]),
        ),
      ),
    }),
  });
};

const toProjectMethodologiesSchema = (requestTypeId: number, requestTypes: ReferenceType[]) => {
  if (isMethodologyUpdateRequestType(requestTypeId, requestTypes)) {
    return Yup.array()
      .of(methodologyShapeSchema)
      .min(1, "Add at least one methodology")
      .test("has-methodology-version", "Add at least one methodology with a version", (arr) => {
        if (!Array.isArray(arr) || arr.length === 0) return false;
        interface MethodologyEntry {
          methodologyId?: unknown;
          methodologyVersion?: unknown;
        }
        return arr.some((m: MethodologyEntry | null | undefined) => {
          if (!m) return false;
          const idValid = typeof m.methodologyId === "number" && m.methodologyId >= 0;
          const versionValid = typeof m.methodologyVersion === "number" && m.methodologyVersion > 0;
          return idValid && versionValid;
        });
      });
  }
  return Yup.array().of(methodologyShapeSchema).notRequired();
};
