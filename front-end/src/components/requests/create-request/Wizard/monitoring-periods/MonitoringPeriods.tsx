import dayjs, { Dayjs } from "dayjs";

import { CreateCreditingPeriod, CreditingPeriod, MonitoringPeriod } from "../../../../../common/project/Project";

export const lastMonitoringPeriodEndDate = (creditingPeriod: CreditingPeriod) => {
  if (creditingPeriod?.monitoringPeriods && creditingPeriod.monitoringPeriods.length > 0) {
    const latestPeriod = creditingPeriod.monitoringPeriods.reduce((latest, current) => {
      return dayjs.utc(current.endDate).isAfter(dayjs.utc(latest.endDate)) ? current : latest;
    });
    return latestPeriod.endDate;
  }
  return undefined;
};

const dateIsUsedInAnyExistingPeriod = (date: Dayjs, monitoringPeriods: MonitoringPeriod[] | undefined) => {
  const dateDay = date.startOf("day");
  if (monitoringPeriods?.some((period) => fallsWithinMonitoringPeriod(dateDay, period))) {
    return true;
  }
  return false;
};

export const findFirstGapOrLatestEndDate = (creditingPeriod: CreditingPeriod, usedDates: Dayjs[]): Dayjs => {
  if (!usedDates || usedDates.length == 0) return dayjs.utc(creditingPeriod.startDate);

  // Find the first gap i.e. the earliest date covered by a monitoring period where +1 day is not equal to the next date in the array
  for (let i = 0; i < usedDates.length - 1; i++) {
    const current = usedDates[i];
    const next = usedDates[i + 1];

    if (!current.add(1, "day").isSame(next, "day")) {
      return current.add(1, "day");
    }
  }
  // No gaps found, return the latest end date
  return usedDates[usedDates.length - 1].add(1, "day");
};

export const rangeOverlapsWithExistingPeriods = (startDate: Dayjs, endDate: Dayjs, usedDates: Dayjs[]): boolean => {
  return usedDates.some(
    (usedDate) =>
      (usedDate.isAfter(startDate, "day") || usedDate.isSame(startDate, "day")) &&
      (usedDate.isBefore(endDate, "day") || usedDate.isSame(endDate, "day")),
  );
};

const endDateAlreadyUsedOrWouldCauseOverlap = (
  endDateToCheck: Dayjs,
  startDate: Dayjs,
  usedDates: Dayjs[],
  monitoringPeriods: MonitoringPeriod[] | undefined,
): boolean => {
  return (
    dateIsUsedInAnyExistingPeriod(endDateToCheck, monitoringPeriods) ||
    rangeOverlapsWithExistingPeriods(startDate, endDateToCheck, usedDates)
  );
};

export const fallsWithinMonitoringPeriod = (date: Dayjs, monitoringPeriod: MonitoringPeriod) => {
  return fallsWithinPeriod(date, dayjs.utc(monitoringPeriod.startDate), dayjs.utc(monitoringPeriod.endDate));
};

const fallsWithinPeriod = (date: Dayjs, periodStartDate: Dayjs, periodEndDate: Dayjs) => {
  const dateDay = date.startOf("day");
  const startDateDay = periodStartDate.startOf("day");
  const endDateDay = periodEndDate.startOf("day");
  return (
    startDateDay.isSame(dateDay, "day") ||
    (startDateDay.isBefore(dateDay) && endDateDay.isAfter(dateDay)) ||
    endDateDay.isSame(dateDay, "day")
  );
};

export const allMonitoringPeriodDates = (creditingPeriod: CreditingPeriod | CreateCreditingPeriod): Dayjs[] => {
  const dates: Dayjs[] = [];
  const monitoringPeriods = creditingPeriod.monitoringPeriods;
  if (monitoringPeriods) {
    monitoringPeriods.forEach((period) => {
      let date = dayjs.utc(period.startDate).startOf("day");
      const endDate = dayjs.utc(period.endDate).startOf("day");
      while (date.isBefore(endDate, "day") || date.isSame(endDate, "day")) {
        dates.push(date);
        date = date.add(1, "day");
      }
    });
  }
  return dates;
};

export const isCreditingPeriodFull = (creditingPeriod: CreditingPeriod) => {
  let current = dayjs.utc(creditingPeriod.startDate).startOf("day");
  const creditingEndDate = dayjs.utc(creditingPeriod.endDate).startOf("day");

  while (current.isBefore(creditingEndDate, "day") || current.isSame(creditingEndDate, "day")) {
    if (!dateIsUsedInAnyExistingPeriod(current, creditingPeriod?.monitoringPeriods)) {
      return false;
    }
    current = current.add(1, "day");
  }

  return true;
};

export const getMonitoringPeriodOptions = (creditingPeriods: CreditingPeriod[], creditingPeriodId: number | null) => {
  if (creditingPeriodId) {
    return (creditingPeriods[creditingPeriodId - 1].monitoringPeriods || []).map((period) => ({
      value: period.id,
      label:
        period.id === 0
          ? "New Period"
          : `Period ${period.id} - ${dayjs.utc(period.startDate).format("DD/MM/YYYY")} to ${dayjs
              .utc(period.endDate)
              .format("DD/MM/YYYY")}`,
    }));
  }
  return [];
};

export const shouldDisableStartDateOption = (
  date: Dayjs,
  endDate: Dayjs | null | undefined,
  monitoringPeriods: MonitoringPeriod[] | undefined,
  isFixedEndDate?: boolean,
) => {
  if (isFixedEndDate && date.isAfter(endDate)) {
    return true;
  }
  return dateIsUsedInAnyExistingPeriod(date, monitoringPeriods);
};

export const shouldDisableEndDateOption = (
  date: Dayjs,
  startDate: Dayjs | null | undefined,
  usedDates: Dayjs[],
  monitoringPeriods: MonitoringPeriod[] | undefined,
) => {
  return endDateAlreadyUsedOrWouldCauseOverlap(date, dayjs.utc(startDate), usedDates, monitoringPeriods);
};
