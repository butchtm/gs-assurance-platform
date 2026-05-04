import dayjs, { Dayjs } from "dayjs";

export const addWorkingDays = (date: Dayjs, days: number): Dayjs => {
  return date.businessDaysAdd(days);
};

export const getDaysRemaining = (dueDate: string, endDate: string): number => {
  return dayjs.utc(dueDate).startOf("day").businessDiff(dayjs.utc(endDate).startOf("day"));
};

export const getDaysRemainingFromToday = (dueDate: string): number => {
  return getDaysRemaining(dueDate, dayjs.utc().startOf("day").toString());
};

export const getDaysRemainingFromTodayString = (date: string, short?: boolean): string => {
  const difference = getDaysRemainingFromToday(date);
  if (difference == -1) {
    return `${Math.abs(difference)} day ${short ? "ago" : "overdue"}`;
  }
  if (difference < 0) {
    return `${Math.abs(difference)} days ${short ? "ago" : "overdue"}`;
  }
  if (difference == 0) {
    return `${short ? "D" : "d"}ue today`;
  }
  if (difference == 1) {
    return `${difference} day${short ? "" : " remaining"}`;
  }
  return `${difference} days${short ? "" : " remaining"}`;
};
