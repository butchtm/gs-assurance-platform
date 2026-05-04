import { FormControl, Grid, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs, { Dayjs } from "dayjs";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { CreditingPeriod } from "../../../../../common/project/Project";
import { BatchedProjectRequest } from "../../../Requests";
import { CreateRequestForm } from "../Request";

import {
  allMonitoringPeriodDates,
  findFirstGapOrLatestEndDate,
  lastMonitoringPeriodEndDate,
  rangeOverlapsWithExistingPeriods,
  shouldDisableEndDateOption,
  shouldDisableStartDateOption,
} from "./MonitoringPeriods";

export const StepBatchedNewMonitoringFields: React.FC<{
  creditingPeriod: CreditingPeriod;
  project: BatchedProjectRequest;
  isFixedEndDate: boolean;
}> = ({ creditingPeriod, project, isFixedEndDate }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();
  const [gapWarning, setGapWarning] = useState<boolean>(false);
  const [usedDates, setUsedDates] = useState<Dayjs[]>(allMonitoringPeriodDates(creditingPeriod));

  useEffect(() => {
    setUsedDates(allMonitoringPeriodDates(creditingPeriod));
  }, [creditingPeriod.monitoringPeriods, creditingPeriod]);

  const currentProjectSelector = `batchStepProjectValues.batch.[${project.id}]`;

  const getDefaultStartDate = () => {
    const firstGapOrLastEndDate = findFirstGapOrLatestEndDate(creditingPeriod, usedDates);
    if (firstGapOrLastEndDate) {
      return dayjs.utc(firstGapOrLastEndDate);
    }
    return dayjs.utc(creditingPeriod.startDate);
  };

  const getDefaultEndDate = () => {
    const startDate = getDefaultStartDate();
    return startDate.add(1, "day");
  };

  useEffect(() => {
    const batchValues = values.batchStepProjectValues?.batch[project.id];
    if (batchValues) {
      const defaultStartDate = getDefaultStartDate();
      if (!batchValues.newMonitoringPeriodStartDate) {
        setFieldValue(`${currentProjectSelector}.newMonitoringPeriodStartDate`, defaultStartDate);
        checkForGaps(defaultStartDate);
      } else {
        checkForGaps(batchValues.newMonitoringPeriodStartDate);
      }
      if (!batchValues.newMonitoringPeriodEndDate && !isFixedEndDate) {
        setFieldValue(`${currentProjectSelector}.newMonitoringPeriodEndDate`, getDefaultEndDate());
      }
    }
  }, [creditingPeriod]);

  const checkForGaps = (startDate: Dayjs | null) => {
    if (!startDate?.isValid()) {
      setGapWarning(false);
      return;
    }
    if (creditingPeriod.monitoringPeriods?.length === 0) {
      setGapWarning(!dayjs.utc(creditingPeriod.startDate).startOf("day").isSame(startDate?.startOf("day"), "day"));
    } else {
      setGapWarning(
        !dayjs
          .utc(lastMonitoringPeriodEndDate(creditingPeriod))
          .startOf("day")
          .add(1, "day")
          .isSame(startDate?.startOf("day"), "day"),
      );
    }
  };

  const handleStartChange = (date: Dayjs | null) => {
    setFieldValue(`${currentProjectSelector}.newMonitoringPeriodStartDate`, date);
    if (isFixedEndDate) {
      return;
    }

    const batchValues = values.batchStepProjectValues?.batch[project.id];
    if (date && batchValues?.newMonitoringPeriodEndDate) {
      const currentEndDate = dayjs.utc(batchValues?.newMonitoringPeriodEndDate);
      if (currentEndDate.isBefore(date) || rangeOverlapsWithExistingPeriods(currentEndDate, date, usedDates)) {
        setFieldValue(`${currentProjectSelector}.newMonitoringPeriodEndDate`, date);
      }
    }
    checkForGaps(date);
  };

  const handleEndChange = (date: Dayjs | null) => {
    setFieldValue(`${currentProjectSelector}.newMonitoringPeriodEndDate`, date);
  };

  const toMinDate = () => {
    return findFirstGapOrLatestEndDate(creditingPeriod, usedDates);
  };

  const toMaxDate = () => {
    return dayjs.utc(creditingPeriod.endDate);
  };

  return (
    <Grid item xs={12}>
      <Grid sx={{ display: "flex" }}>
        <FormControl fullWidth>
          <DatePicker
            label="Monitoring Period Start Date"
            value={values.batchStepProjectValues?.batch[project.id].newMonitoringPeriodStartDate}
            onChange={handleStartChange}
            minDate={toMinDate()}
            maxDate={toMaxDate()}
            format="DD/MM/YYYY"
            shouldDisableDate={(date) =>
              shouldDisableStartDateOption(
                date,
                values.batchStepProjectValues?.batch[project.id].newMonitoringPeriodEndDate,
                creditingPeriod?.monitoringPeriods,
                isFixedEndDate,
              )
            }
          />
        </FormControl>
        <FormControl fullWidth sx={{ paddingLeft: "5px" }}>
          <DatePicker
            label="Monitoring Period End Date"
            value={values.batchStepProjectValues?.batch[project.id].newMonitoringPeriodEndDate}
            onChange={handleEndChange}
            minDate={dayjs.utc(values.batchStepProjectValues?.batch[project.id].newMonitoringPeriodStartDate)}
            maxDate={toMaxDate()}
            format="DD/MM/YYYY"
            disabled={isFixedEndDate}
            shouldDisableDate={(date) =>
              shouldDisableEndDateOption(
                date,
                values.batchStepProjectValues?.batch[project.id].newMonitoringPeriodStartDate,
                usedDates,
                creditingPeriod?.monitoringPeriods,
              )
            }
          />
        </FormControl>
      </Grid>
      {gapWarning && (
        <Typography variant="body2" color="warning.main" sx={{ marginTop: "5px" }}>
          Warning: There is a gap between the previous monitoring period or the start of the crediting period and the
          new start date. This does not prevent the request from being submitted.
        </Typography>
      )}
    </Grid>
  );
};
