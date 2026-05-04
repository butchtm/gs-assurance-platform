import AddIcon from "@mui/icons-material/Add";
import { Button, FormControl, Grid, IconButton, InputLabel, MenuItem, Select, Typography } from "@mui/material";
import { ClearIcon, DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { FieldArray, useFormikContext } from "formik";
import { useEffect, useState } from "react";

import { CreateCreditingPeriod, CreateProjectForm, MonitoringPeriod } from "../../../../common/project/Project";
import {
  allMonitoringPeriodDates,
  fallsWithinMonitoringPeriod,
} from "../../../requests/create-request/Wizard/monitoring-periods/MonitoringPeriods";
import { Options, useReferenceTypes } from "../../../../common/data-types/DataReferenceTypes";
import { isNullOrUndefined } from "../../../../common/request/GSRequest";

interface CreateMonitoringPeriodProps {
  currentMonitoringPeriods: MonitoringPeriod[] | undefined;
  creditingPeriodIndex: number;
}

const CreateMonitoringPeriod: React.FC<CreateMonitoringPeriodProps> = ({
  currentMonitoringPeriods,
  creditingPeriodIndex,
}: CreateMonitoringPeriodProps) => {
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const [monitoringPeriods, setMonitoringPeriods] = useState<MonitoringPeriod[]>(currentMonitoringPeriods ?? []);
  const [creditingPeriod, setCreditingPeriod] = useState<CreateCreditingPeriod>(
    values.creditingPeriods[creditingPeriodIndex],
  );
  const [usedDates, setUsedDates] = useState<Dayjs[]>([]);
  const { getCustomReferenceOptions } = useReferenceTypes();

  useEffect(() => {
    setCreditingPeriod(values.creditingPeriods[creditingPeriodIndex]);
    setMonitoringPeriods(currentMonitoringPeriods ?? []);
  }, [currentMonitoringPeriods, creditingPeriodIndex, values.creditingPeriods[creditingPeriodIndex]]);

  useEffect(() => {
    setUsedDates(allMonitoringPeriodDates(creditingPeriod));
  }, [monitoringPeriods, creditingPeriod]);

  const handleAddPeriod = (push: (obj: MonitoringPeriod) => void) => {
    const nextPeriodId = monitoringPeriods.length ? monitoringPeriods[monitoringPeriods?.length - 1].id + 1 : 1;
    const newStartDate = toNewStartDate();
    push({
      id: nextPeriodId,
      startDate: newStartDate.toISOString(),
      endDate: newStartDate.toISOString(),
      feeSchedule: null,
    });
  };

  const toNewStartDate = (): Dayjs => {
    let date = dayjs.utc(creditingPeriod.startDate).startOf("day");
    const endDate = dayjs.utc(creditingPeriod.endDate).startOf("day");
    if (monitoringPeriods) {
      while (date.isBefore(endDate, "day") || date.isSame(endDate, "day")) {
        if (!usedDates.some((usedDate) => usedDate.isSame(date, "day"))) {
          return date;
        }
        date = date.add(1, "day");
      }
    }
    return dayjs.utc(creditingPeriod.startDate).startOf("day");
  };

  const isAddDisabled = () => {
    if (!monitoringPeriods || monitoringPeriods.length === 0) return false;

    const periodStartDate = dayjs.utc(creditingPeriod.startDate).startOf("day");
    const periodEndDate = dayjs.utc(creditingPeriod.endDate).startOf("day");
    const periodLength = Math.abs(dayjs.utc(periodStartDate).diff(periodEndDate, "day")) + 1;
    if (allMonitoringPeriodDates(creditingPeriod).length == periodLength) {
      return true;
    }
    return false;
  };

  const handleStartDateChange = (index: number, date: Dayjs | null) => {
    setFieldValue(`creditingPeriods[${creditingPeriodIndex}].monitoringPeriods[${index}].startDate`, date);
    const thisPeriod = monitoringPeriods[index];
    if (date && thisPeriod.endDate) {
      const currentEndDate = dayjs.utc(thisPeriod.endDate);
      if (currentEndDate.isBefore(date) || endDateWouldCauseOverlap(currentEndDate, date, index + 1)) {
        setFieldValue(`creditingPeriods[${creditingPeriodIndex}].monitoringPeriods[${index}].endDate`, date);
      }
    }
  };

  const handleEndDateChange = (index: number, date: Dayjs | null) => {
    setFieldValue(`creditingPeriods[${creditingPeriodIndex}].monitoringPeriods[${index}].endDate`, date);
  };

  const handleFeeScheduleChange = (index: number, feeSchedule: number) => {
    setFieldValue(
      `creditingPeriods[${creditingPeriodIndex}].monitoringPeriods[${index}].feeSchedule`,
      feeSchedule >= 0 ? feeSchedule : null,
    );
  };

  const getFeeScheduleOptions = (feeSchedule: number | null) => {
    const blankOption: Options = { value: -1, label: "None" };
    const options = getCustomReferenceOptions("FeeSchedule", feeSchedule);
    return [blankOption].concat(options);
  };

  const otherMonitoringPeriods = (thisPeriodId: number): MonitoringPeriod[] => {
    if (monitoringPeriods.length <= 1) {
      return [];
    }
    const otherPeriods: MonitoringPeriod[] = [];
    monitoringPeriods.forEach((period) => {
      if (period.id != thisPeriodId) {
        otherPeriods.push(period);
      }
    });
    return otherPeriods;
  };

  const dateIsUsedThisPeriod = (date: Dayjs, thisPeriod: MonitoringPeriod) => {
    if (!thisPeriod.startDate && !thisPeriod.endDate) return false;

    if (!thisPeriod.endDate) {
      return dayjs.utc(thisPeriod.startDate).startOf("day").isSame(date, "day");
    }
    if (!thisPeriod.startDate) {
      return dayjs.utc(thisPeriod.endDate).startOf("day").isSame(date, "day");
    }
    return fallsWithinMonitoringPeriod(date, thisPeriod);
  };

  const dateIsUsedAnotherPeriod = (date: Dayjs, thisPeriod: MonitoringPeriod) => {
    const dateDay = date.startOf("day");
    if (usedDates.some((usedDate) => usedDate.isSame(dateDay, "day") && !dateIsUsedThisPeriod(usedDate, thisPeriod))) {
      return true;
    }
    return false;
  };

  const endDateWouldCauseOverlap = (date: Dayjs, thisStartDate: Dayjs, thisPeriodId: number) => {
    const dateDay = date.startOf("day");
    const thisStartDateDay = thisStartDate.startOf("day");
    const otherPeriods = otherMonitoringPeriods(thisPeriodId);
    if (
      otherPeriods.some((period) => {
        const periodStartDate = dayjs.utc(period.startDate);
        const periodEndDate = dayjs.utc(period.endDate);
        return (
          (thisStartDateDay.isBefore(periodStartDate, "day") && !dateDay.isBefore(periodStartDate, "day")) ||
          (thisStartDateDay.isAfter(periodEndDate, "day") && !dateDay.isAfter(periodEndDate, "day"))
        );
      })
    ) {
      return true;
    }
    return false;
  };

  const isStartDateDisabled = (date: Dayjs, thisPeriod: MonitoringPeriod) => {
    return dateIsUsedAnotherPeriod(date, thisPeriod);
  };

  const isEndDateDisabled = (date: Dayjs, thisPeriod: MonitoringPeriod) => {
    if (thisPeriod.id && thisPeriod.startDate) {
      return (
        dateIsUsedAnotherPeriod(date, thisPeriod) ||
        endDateWouldCauseOverlap(date, dayjs.utc(thisPeriod.startDate), thisPeriod.id)
      );
    }
    return dateIsUsedAnotherPeriod(date, thisPeriod);
  };

  return (
    <Grid container item xs={12} sx={{ ml: 3 }}>
      <Grid item xs={12} sx={{ mt: 2, mb: 2 }}>
        <Typography variant="h6" display="flex">
          Monitoring Periods
        </Typography>
      </Grid>
      <FieldArray name={`creditingPeriods[${creditingPeriodIndex}].monitoringPeriods`}>
        {({ push, remove }) => {
          return (
            <Grid container spacing={2} alignItems="top">
              {currentMonitoringPeriods?.map((period: MonitoringPeriod, periodIndex: number) => (
                <Grid
                  container
                  alignItems="center"
                  key={period.id}
                  sx={{ mb: 2, mt: 2, ml: 2 }}
                  justifyContent="space-between"
                >
                  <Typography variant="body1" align="center" sx={{ fontWeight: "bold" }}>
                    {period.id}
                  </Typography>
                  <DatePicker
                    label="Start Date"
                    value={dayjs.utc(period.startDate)}
                    onChange={(date) => handleStartDateChange(periodIndex, date)}
                    format="DD/MM/YYYY"
                    minDate={dayjs.utc(creditingPeriod.startDate)}
                    maxDate={dayjs.utc(creditingPeriod.endDate)}
                    shouldDisableDate={(date) => isStartDateDisabled(date, period)}
                    timezone="UTC"
                    sx={{ width: "175px" }}
                  />
                  <DatePicker
                    label="End Date"
                    value={dayjs.utc(period.endDate)}
                    onChange={(date) => handleEndDateChange(periodIndex, date)}
                    format="DD/MM/YYYY"
                    minDate={dayjs.utc(period.startDate)}
                    maxDate={dayjs.utc(creditingPeriod.endDate)}
                    shouldDisableDate={(date) => isEndDateDisabled(date, period)}
                    timezone="UTC"
                    sx={{ width: "175px" }}
                  />
                  <FormControl sx={{ width: "200px" }}>
                    <InputLabel id="fee-schedule-label">Fee Schedule</InputLabel>
                    <Select
                      labelId="fee-schedule-label"
                      label="Fee Schedule"
                      value={!isNullOrUndefined(period.feeSchedule) ? period.feeSchedule : -1}
                      onChange={(event) => handleFeeScheduleChange(periodIndex, Number(event.target.value))}
                      data-testid="fee-schedule-select"
                    >
                      {getFeeScheduleOptions(period.feeSchedule).map((option: Options) => {
                        return (
                          <MenuItem
                            value={option.value}
                            key={option.value}
                            data-testid={`fee-schedule-option-${option.value}`}
                          >
                            {option.label}
                          </MenuItem>
                        );
                      })}
                    </Select>
                  </FormControl>
                  <IconButton aria-label="Remove Monitoring Period" onClick={() => remove(periodIndex)}>
                    <ClearIcon color="error" />
                  </IconButton>
                </Grid>
              ))}
              <Grid item xs={12}>
                <Button disabled={isAddDisabled()} onClick={() => handleAddPeriod(push)}>
                  <AddIcon />
                  Add Monitoring Period
                </Button>
              </Grid>
            </Grid>
          );
        }}
      </FieldArray>
    </Grid>
  );
};

export default CreateMonitoringPeriod;
