import dayjs, { Dayjs } from "dayjs";
import * as Yup from "yup";

import { ReferenceType } from "../../../../common/data-types/DataReferenceTypes";
import { CreditingPeriod } from "../../../../common/project/Project";
import { getEmissionReductionRequiredIds, getFeeScheduleRequestTypeIds } from "../../../../common/request/GSRequest";

import {
  allMonitoringPeriodDates,
  findFirstGapOrLatestEndDate,
  shouldDisableEndDateOption,
  shouldDisableStartDateOption,
} from "./monitoring-periods/MonitoringPeriods";

export const GENERIC_REQUEST_ID = "1013";

export const getRequestStep1Schema = (requestTypes: ReferenceType[], creditingPeriods: CreditingPeriod[]) => {
  const feeScheduleRequestTypeIds = getFeeScheduleRequestTypeIds(requestTypes);
  const emissionReductionRequiredIds = getEmissionReductionRequiredIds(requestTypes);
  return Yup.object().shape({
    requestType: Yup.string().required("Request type required"),
    createBatchRequest: Yup.string().required("Select whether this request is a batch"),
    batchRequestTypeId: Yup.string().when("createBatchRequest", {
      is: (val: string) => val === "yes",
      then: (schema) => schema.required("Batch type required"),
      otherwise: (schema) => schema.notRequired(),
    }),
    batchProjectIds: Yup.array().when("createBatchRequest", {
      is: (val: string) => val === "yes",
      then: (schema) =>
        schema.when("batchRequestTypeId", {
          is: (val: number) => Number(val) !== 0,
          then: (sch) => sch.min(2, "At least two batch projects are required to batch together"),
          otherwise: (sch) => sch.required().length(2, "Exactly two batch projects are required to batch together"),
        }),
      otherwise: (schema) => schema.notRequired(),
    }),
    creditingPeriodId: Yup.number().when(
      ["createBatchRequest", "newCreditingPeriodStartDate", "newCreditingPeriodEndDate"],
      {
        is: (createBatchRequest: string, startDate: string | null, endDate: string | null) =>
          createBatchRequest === "no" && startDate === null && endDate === null,
        then: (creditingPeriodId) => creditingPeriodId.required("Crediting period required"),
        otherwise: (creditingPeriodId) => creditingPeriodId.nullable(),
      },
    ),
    newCreditingPeriodStartDate: Yup.string().nullable(),
    newCreditingPeriodEndDate: Yup.string().nullable(),

    monitoringPeriodId: Yup.number().when(
      ["createBatchRequest", "isMonitoringPeriodEnabled", "useNewMonitoringPeriod", "requestType"],
      {
        is: (
          createBatchRequest: string,
          isMonitoringPeriodEnabled: boolean,
          useNewMonitoringPeriod: boolean,
          requestType: string,
        ) =>
          createBatchRequest === "no" &&
          isMonitoringPeriodEnabled &&
          !useNewMonitoringPeriod &&
          requestType !== GENERIC_REQUEST_ID,
        then: (monitoringPeriodId) => monitoringPeriodId.required("Monitoring period required"),
        otherwise: (monitoringPeriodId) => monitoringPeriodId.nullable(),
      },
    ),

    feeSchedule: Yup.number().when(["createBatchRequest", "requestType", "isMonitoringPeriodEnabled"], {
      is: (createBatchRequest: string, requestType: string, isMonitoringPeriodEnabled: boolean) =>
        feeScheduleRequestTypeIds.includes(Number(requestType)) &&
        requestType !== GENERIC_REQUEST_ID &&
        ((createBatchRequest == "no" && isMonitoringPeriodEnabled) || createBatchRequest == "yes"),
      then: (feeSchedule) => feeSchedule.required("Fee schedule required"),
      otherwise: (FeeSchedule) => FeeSchedule.nullable(),
    }),

    newMonitoringPeriodStartDate: Yup.date().when(
      [
        "createBatchRequest",
        "isMonitoringPeriodEnabled",
        "canCreateNewMonitoringPeriod",
        "useNewMonitoringPeriod",
        "requestType",
      ],
      {
        is: (
          createBatchRequest: string,
          isMonitoringPeriodEnabled: boolean,
          canCreateNewMonitoringPeriod: boolean,
          useNewMonitoringPeriod: boolean,
          requestType: string,
        ) =>
          createBatchRequest === "no" &&
          isMonitoringPeriodEnabled &&
          canCreateNewMonitoringPeriod &&
          useNewMonitoringPeriod &&
          requestType !== GENERIC_REQUEST_ID,
        then: (newMonitoringPeriodStartDate) =>
          newMonitoringPeriodStartDate
            .required("Monitoring period start date is required")
            .test(
              "start_date_test",
              "Start date must be within crediting period and not overlap with any other monitoring periods",
              function (value) {
                const { newMonitoringPeriodEndDate, creditingPeriodId } = this.parent;
                const startDate = dayjs.utc(value).startOf("day");
                const endDate = dayjs.utc(newMonitoringPeriodEndDate).startOf("day");
                return isNewMonitoringPeriodStartDateValid(startDate, endDate, creditingPeriodId, creditingPeriods);
              },
            ),
        otherwise: (newMonitoringPeriodStartDate) => newMonitoringPeriodStartDate.nullable(),
      },
    ),

    newMonitoringPeriodEndDate: Yup.date().when(
      [
        "createBatchRequest",
        "isMonitoringPeriodEnabled",
        "canCreateNewMonitoringPeriod",
        "useNewMonitoringPeriod",
        "requestType",
      ],
      {
        is: (
          createBatchRequest: string,
          isMonitoringPeriodEnabled: boolean,
          canCreateNewMonitoringPeriod: boolean,
          useNewMonitoringPeriod: boolean,
          requestType: string,
        ) =>
          createBatchRequest === "no" &&
          isMonitoringPeriodEnabled &&
          canCreateNewMonitoringPeriod &&
          useNewMonitoringPeriod &&
          requestType !== GENERIC_REQUEST_ID,
        then: (newMonitoringPeriodEndDate) =>
          newMonitoringPeriodEndDate
            .required("Monitoring period end date is required")
            .test(
              "end_date_test",
              "End date must be within crediting period, after the end date and not overlap with any other monitoring periods",
              function (value) {
                const { newMonitoringPeriodStartDate, creditingPeriodId } = this.parent;
                const startDate = dayjs.utc(newMonitoringPeriodStartDate).startOf("day");
                const endDate = dayjs.utc(value).startOf("day");
                return isNewMonitoringPeriodEndDateValid(startDate, endDate, creditingPeriodId, creditingPeriods);
              },
            ),
        otherwise: (newMonitoringPeriodEndDate) => newMonitoringPeriodEndDate.nullable(),
      },
    ),
    emissionReductionAmount: Yup.number().when(["requestType", "createBatchRequest"], {
      is: (requestType: string, createBatchRequest: string) =>
        createBatchRequest === "no" && emissionReductionRequiredIds.includes(Number(requestType)),
      then: (schema) => schema.required("Emission reduction amount is required"),
      otherwise: (schema) => schema.notRequired(),
    }),
  });
};

export const methodologyShapeSchema = Yup.object({
  methodologyVersion: Yup.number().required(),
  methodologyId: Yup.number().required(),
});

export const methodologiesSchema = Yup.object().shape({
  methodologyConfigurations: Yup.array().of(methodologyShapeSchema).min(1, "Add at least one methodology"),
});

export const isNewMonitoringPeriodStartDateValid = (
  startDate: Dayjs,
  endDate: Dayjs,
  creditingPeriodId: number,
  creditingPeriods: CreditingPeriod[],
  isFixedEndDate?: boolean,
) => {
  const creditingPeriod = creditingPeriods.find((period) => period.periodId == creditingPeriodId);
  if (!creditingPeriod) {
    return true;
  }
  const usedDates = allMonitoringPeriodDates(creditingPeriod);
  const minDate = findFirstGapOrLatestEndDate(creditingPeriod, usedDates).startOf("day");
  const maxDate = dayjs.utc(creditingPeriod.endDate).startOf("day");
  if (startDate.isBefore(minDate) || startDate.isAfter(maxDate)) {
    return false;
  }
  return !shouldDisableStartDateOption(startDate, endDate, creditingPeriod?.monitoringPeriods, isFixedEndDate);
};

export const isNewMonitoringPeriodEndDateValid = (
  startDate: Dayjs,
  endDate: Dayjs,
  creditingPeriodId: number,
  creditingPeriods: CreditingPeriod[],
) => {
  const creditingPeriod = creditingPeriods.find((period) => period.periodId == creditingPeriodId);
  if (!creditingPeriod) {
    return true;
  }
  const usedDates = allMonitoringPeriodDates(creditingPeriod);
  if (endDate.isBefore(startDate) || endDate.isAfter(dayjs.utc(creditingPeriod.endDate))) {
    return false;
  }
  return !shouldDisableEndDateOption(endDate, startDate, usedDates, creditingPeriod?.monitoringPeriods);
};
