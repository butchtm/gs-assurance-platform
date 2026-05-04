import { Box, FormControl, FormHelperText } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import React from "react";
import { useField } from "formik";
import dayjs, { Dayjs } from "dayjs";

import CustomHelperText, { CustomTooltip } from "./CustomTooltipText";

interface CustomDatePickerProps {
  name: string;
  label: string;
  helperText?: string | JSX.Element;
  required?: boolean;
  defaultValue?: Dayjs;
  minDate?: Dayjs;
  maxDate?: Dayjs;
  onChange?: (date: Dayjs | null) => void;
  shouldDisableDate?: (date: Dayjs) => boolean;
}

export const CustomDatePicker: React.FC<CustomDatePickerProps> = ({
  name,
  label,
  helperText,
  required,
  defaultValue,
  minDate,
  maxDate,
  onChange,
  shouldDisableDate,
}) => {
  const [field, meta, helpers] = useField(name);
  const value = (defaultValue ?? field.value) ? dayjs.utc(field.value) : null;

  return (
    <Box display="flex" alignItems="center">
      <FormControl fullWidth error={meta.touched && Boolean(meta.error)}>
        <DatePicker
          label={
            required ? (
              <>
                {label}
                <span aria-hidden="true">*</span>
              </>
            ) : (
              label
            )
          }
          value={value}
          onChange={(date) => {
            if (onChange) {
              onChange(date);
            } else {
              helpers.setValue(date?.toISOString());
            }
          }}
          slotProps={{ textField: { fullWidth: true, error: meta.touched && Boolean(meta.error) } }}
          format="DD/MM/YYYY"
          timezone="UTC"
          minDate={minDate}
          maxDate={maxDate}
          aria-required={required}
          shouldDisableDate={shouldDisableDate}
        />
        {meta.touched && meta.error ? (
          <FormHelperText error role="alert">
            {meta.error}
          </FormHelperText>
        ) : null}
      </FormControl>
      {helperText && (
        <Box ml={2}>
          <CustomHelperText text={helperText} type={CustomTooltip.HELP} />
        </Box>
      )}
    </Box>
  );
};
