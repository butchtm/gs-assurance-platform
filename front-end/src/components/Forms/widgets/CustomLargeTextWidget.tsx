import React from "react";
import { TextField, Typography, useTheme } from "@mui/material";
import { WidgetProps } from "@rjsf/utils";

export const CustomLargeTextWidget: React.FC<WidgetProps> = ({
  id,
  options,
  value,
  required,
  disabled,
  readonly,
  label,
  onChange,
  onBlur,
  onFocus,
  rawErrors,
}) => {
  const hasError = rawErrors && rawErrors.length > 0;
  const theme = useTheme();
  return (
    <>
      <Typography sx={{ mb: "5px", color: hasError ? theme.palette.error.main : "var(--color-text-bold)" }}>
        {required ? (
          <>
            {label} <span aria-hidden="true">*</span>
          </>
        ) : (
          label
        )}
      </Typography>
      <TextField
        id={id}
        value={value}
        onChange={(event) => onChange(event.target.value == "" ? null : event.target.value)}
        onBlur={onBlur && (() => onBlur(id, value))}
        onFocus={onFocus && (() => onFocus(id, value))}
        disabled={disabled || readonly}
        error={hasError}
        multiline
        rows={options.rows}
        fullWidth
        data-testid={`${id}-large-text-area`}
      />
    </>
  );
};
