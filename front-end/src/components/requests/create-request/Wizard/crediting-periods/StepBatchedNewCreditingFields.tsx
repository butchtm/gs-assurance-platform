import { FormControl, Grid } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs, { Dayjs } from "dayjs";
import { useFormikContext } from "formik";
import React from "react";

import {
  toDefaultEndDate,
  toMaxEndDate,
  toMaxStartDate,
  toMinEndDate,
  toMinStartDate,
} from "../../../../../common/project/Project";
import { BatchedProjectRequest } from "../../../Requests";
import { CreateRequestForm } from "../Request";

export const StepBatchedNewCreditingFields: React.FC<{
  batchedProject: BatchedProjectRequest;
}> = ({ batchedProject }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();

  const currentProjectSelector = `batchStepProjectValues.batch.[${batchedProject.id}]`;

  const handleStartChange = (date: Dayjs | null) => {
    setFieldValue(`${currentProjectSelector}.newCreditingPeriodStartDate`, date ? date.utc().toISOString() : null);
  };

  const handleEndChange = (date: Dayjs | null) => {
    setFieldValue(`${currentProjectSelector}.newCreditingPeriodEndDate`, date ? date.utc().toISOString() : null);
  };

  const currentBatchProject = values.batchStepProjectValues?.batch?.[batchedProject.id];

  const toStartDate = (): Dayjs => {
    if (currentBatchProject?.newCreditingPeriodStartDate) {
      return dayjs.utc(currentBatchProject.newCreditingPeriodStartDate);
    }
    return dayjs.utc();
  };

  const toEndDate = (startDate: Dayjs): Dayjs => {
    if (currentBatchProject?.newCreditingPeriodEndDate) {
      return dayjs.utc(currentBatchProject.newCreditingPeriodEndDate);
    }
    return toDefaultEndDate(startDate);
  };

  const creditingPeriods = batchedProject?.creditingPeriods ?? [];
  const newIndex = creditingPeriods.length;
  const startDate = toStartDate();
  const endDate = toEndDate(startDate);

  return (
    <Grid item xs={12} sx={{ display: "flex", mb: 2 }}>
      <FormControl fullWidth required>
        <DatePicker
          label="Crediting Period Start Date"
          value={startDate}
          onChange={handleStartChange}
          data-testid="new-crediting-start-date"
          minDate={toMinStartDate(creditingPeriods, newIndex)}
          maxDate={toMaxStartDate(creditingPeriods, newIndex)}
          format="DD/MM/YYYY"
          slotProps={{
            textField: {
              required: true,
              inputProps: { "aria-required": true },
            },
          }}
        />
      </FormControl>
      <FormControl fullWidth required sx={{ paddingLeft: "5px" }}>
        <DatePicker
          label="Crediting Period End Date"
          value={endDate}
          onChange={handleEndChange}
          data-testid="new-crediting-end-date"
          minDate={toMinEndDate(startDate.toISOString())}
          maxDate={toMaxEndDate(creditingPeriods, newIndex)}
          format="DD/MM/YYYY"
          slotProps={{
            textField: {
              required: true,
              inputProps: { "aria-required": true },
            },
          }}
        />
      </FormControl>
    </Grid>
  );
};
