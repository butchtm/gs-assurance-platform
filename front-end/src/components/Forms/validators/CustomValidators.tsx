import { FormValidation } from "@rjsf/utils";
import dayjs, { Dayjs } from "dayjs";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const customValidate = (formData: any, errors: FormValidation<any>) => {
  const monitoringPeriodError = getMonitoringPeriodErrors(formData.monitoringPeriod, formData.reportCompletionDate);
  if (monitoringPeriodError) {
    errors.monitoringPeriod?.addError(monitoringPeriodError);
  }
  return errors;
};

const getMonitoringPeriodErrors = (
  monitoringPeriod?: { startDate?: string; endDate?: string },
  completionDate?: string,
) => {
  if (!monitoringPeriod) {
    return;
  }
  if (!monitoringPeriod.startDate || !monitoringPeriod.endDate) {
    return;
  }
  const start = dayjs.utc(monitoringPeriod.startDate);
  const end = dayjs.utc(monitoringPeriod.endDate);
  if (!isStartBeforeEnd(start, end)) {
    return "Start date is after end date";
  }
  if (!completionDate && !areBothDatesInPast(start, end)) {
    return "Start or end date are not in the past";
  }
  if (completionDate && !areBothDatesPastDate(start, end, dayjs.utc(completionDate))) {
    return "Start or end date are after completion date";
  }
};

const isStartBeforeEnd = (startDate: Dayjs, endDate: Dayjs) => {
  return startDate.isBefore(endDate);
};

const areBothDatesInPast = (startDate: Dayjs, endDate: Dayjs) => {
  const now = dayjs.utc();
  return areBothDatesPastDate(startDate, endDate, now);
};

const areBothDatesPastDate = (startDate: Dayjs, endDate: Dayjs, comparisonDate: Dayjs) => {
  return startDate.isBefore(comparisonDate) && endDate.isBefore(comparisonDate);
};
