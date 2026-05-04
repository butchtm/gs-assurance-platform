import { FormControl, Grid, Typography } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";

import { CreditingPeriod } from "../../../../../common/project/Project";
import { CreateRequestForm } from "../Request";
import { CustomDatePicker } from "../../../../Reusable/FormFields/CustomDatePicker";

import {
  allMonitoringPeriodDates,
  findFirstGapOrLatestEndDate,
  lastMonitoringPeriodEndDate,
  rangeOverlapsWithExistingPeriods,
  shouldDisableEndDateOption,
  shouldDisableStartDateOption,
} from "./MonitoringPeriods";

interface Step1NewMonitoringFieldsProps {
  creditingPeriod: CreditingPeriod;
}

export const Step1NewMonitoringFields: React.FC<Step1NewMonitoringFieldsProps> = ({ creditingPeriod }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();
  const [gapWarning, setGapWarning] = useState<boolean>(false);
  const [usedDates, setUsedDates] = useState<Dayjs[]>(allMonitoringPeriodDates(creditingPeriod));

  useEffect(() => {
    setUsedDates(allMonitoringPeriodDates(creditingPeriod));
  }, [creditingPeriod.monitoringPeriods, creditingPeriod]);

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
    if (!values.newMonitoringPeriodStartDate && !values.newMonitoringPeriodEndDate) {
      const defaultStartDate = getDefaultStartDate();
      setFieldValue("newMonitoringPeriodStartDate", defaultStartDate);
      setFieldValue("newMonitoringPeriodEndDate", getDefaultEndDate());
      checkForGaps(defaultStartDate);
    } else if (values.newMonitoringPeriodStartDate) {
      checkForGaps(values.newMonitoringPeriodStartDate);
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
    setFieldValue("newMonitoringPeriodStartDate", date);
    if (date && values.newMonitoringPeriodEndDate) {
      const currentEndDate = dayjs.utc(values.newMonitoringPeriodEndDate);
      if (currentEndDate.isBefore(date) || rangeOverlapsWithExistingPeriods(date, currentEndDate, usedDates)) {
        setFieldValue("newMonitoringPeriodEndDate", date);
      }
    }
    checkForGaps(date);
  };

  const handleEndChange = (date: Dayjs | null) => {
    setFieldValue("newMonitoringPeriodEndDate", date);
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
          <CustomDatePicker
            name="newMonitoringPeriodStartDate"
            label="Monitoring Period Start Date"
            required
            onChange={handleStartChange}
            minDate={toMinDate()}
            maxDate={toMaxDate()}
            shouldDisableDate={(date) =>
              shouldDisableStartDateOption(date, values.newMonitoringPeriodEndDate, creditingPeriod?.monitoringPeriods)
            }
          />
        </FormControl>
        <FormControl fullWidth sx={{ paddingLeft: "5px" }}>
          <CustomDatePicker
            name="newMonitoringPeriodEndDate"
            label="Monitoring Period End Date"
            required
            onChange={handleEndChange}
            minDate={dayjs.utc(values.newMonitoringPeriodStartDate)}
            maxDate={toMaxDate()}
            shouldDisableDate={(date) =>
              shouldDisableEndDateOption(
                date,
                values.newMonitoringPeriodStartDate,
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
