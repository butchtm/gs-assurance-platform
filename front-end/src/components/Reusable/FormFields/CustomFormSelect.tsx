import { Box, FormControl, FormHelperText, InputBaseComponentProps, InputLabel, MenuItem, Select } from "@mui/material";
import { useField, useFormikContext } from "formik";
import React from "react";

import { Options } from "../../../common/data-types/DataReferenceTypes";

import CustomHelperText, { CustomTooltip } from "./CustomTooltipText";

interface FormSelectProps {
  name: string;
  label: string;
  options: Options[];
  helperText?: string | JSX.Element;
  warningText?: string | JSX.Element;
  disabled?: boolean;
  required?: boolean;
  inputProps?: InputBaseComponentProps;
}

export const CustomFormSelect: React.FC<FormSelectProps> = ({
  name,
  label,
  options,
  helperText,
  warningText,
  disabled,
  required,
  inputProps,
}) => {
  const { setFieldValue } = useFormikContext();
  const [field, meta, helpers] = useField(name);

  if (!options) {
    return <></>;
  }

  return (
    <Box display="flex" alignItems="center">
      <FormControl fullWidth disabled={disabled} error={meta.touched && Boolean(meta.error)}>
        <InputLabel id={`${name}-label`} htmlFor={`${name}-input`}>
          {required ? (
            <>
              {label}
              <span aria-hidden="true">*</span>
            </>
          ) : (
            label
          )}
        </InputLabel>
        <Select
          labelId={`${name}-label`}
          label={label}
          value={field.value ?? ""}
          onChange={(e) => {
            setFieldValue(name, e.target.value);
          }}
          onBlur={() => helpers.setTouched(true)}
          inputProps={{
            ...inputProps,
            id: `${name}-input`,
            "data-testid": `${name}-select`,
            "aria-required": required || false,
            "aria-describedby": meta.touched && meta.error ? `${name}-error` : undefined,
          }}
        >
          {options.map((option) => (
            <MenuItem key={option.value} value={option.value} data-testid={`${name}-option-${option.value}`}>
              {option.label}
            </MenuItem>
          ))}
        </Select>
        {meta.touched && meta.error ? (
          <FormHelperText error role="alert" aria-live="assertive" id={`${name}-error`}>
            {meta.error}
          </FormHelperText>
        ) : null}
      </FormControl>
      {helperText && (
        <Box ml={2}>
          <CustomHelperText text={helperText} type={CustomTooltip.HELP} />
        </Box>
      )}
      {warningText && (
        <Box ml={1}>
          <CustomHelperText text={warningText} type={CustomTooltip.WARNING} />
        </Box>
      )}
    </Box>
  );
};
