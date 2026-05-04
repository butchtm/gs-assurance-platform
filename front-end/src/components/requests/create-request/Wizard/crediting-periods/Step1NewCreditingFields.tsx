import { FormControl, Grid } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs, { Dayjs } from "dayjs";
import { useFormikContext } from "formik";
import React from "react";

import {
  CreditingPeriod,
  toDefaultEndDate,
  toMaxEndDate,
  toMaxStartDate,
  toMinEndDate,
  toMinStartDate,
} from "../../../../../common/project/Project";
import { CreateRequestForm } from "../Request";

interface Step1NewCreditingFieldsProps {
  creditingPeriods: CreditingPeriod[];
}

export const Step1NewCreditingFields: React.FC<Step1NewCreditingFieldsProps> = ({ creditingPeriods }) => {
  const { values, setFieldValue } = useFormikContext<CreateRequestForm>();

  const handleStartChange = (date: Dayjs | null) => {
    setFieldValue("newCreditingPeriodStartDate", date ? date.utc().toISOString() : null);
  };

  const handleEndChange = (date: Dayjs | null) => {
    setFieldValue("newCreditingPeriodEndDate", date ? date.utc().toISOString() : null);
  };

  const toStartDate = (): Dayjs => {
    if (values.newCreditingPeriodStartDate) {
      return dayjs.utc(values.newCreditingPeriodStartDate);
    }
    return dayjs.utc();
  };

  const toEndDate = (startDate: Dayjs): Dayjs => {
    if (values.newCreditingPeriodEndDate) {
      return dayjs.utc(values.newCreditingPeriodEndDate);
    }
    return toDefaultEndDate(startDate);
  };

  const newIndex = creditingPeriods.length;
  const startDate = toStartDate();
  const endDate = toEndDate(startDate);

  return (
    <Grid item xs={12} sx={{ display: "flex" }}>
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
