import { Autocomplete, Box, FormControl, FormHelperText, TextField } from "@mui/material";
import { useField } from "formik";
import React from "react";

import { Options } from "../../../common/data-types/DataReferenceTypes";

import CustomHelperText, { CustomTooltip } from "./CustomTooltipText";

interface CustomSelectProps {
  name: string;
  label: string;
  options: Options[];
  onChange?: (value: number | null) => void;
  helperText?: string | JSX.Element;
  warningText?: string | JSX.Element;
  required?: boolean;
  disabled?: boolean;
}

export const CustomAutoComplete: React.FC<CustomSelectProps> = ({
  name,
  label,
  options,
  onChange,
  helperText,
  warningText,
  required,
  disabled,
}) => {
  const [field, meta, helpers] = useField(name);

  const handleOnChange = (event: React.SyntheticEvent<Element, Event>, newValue: Options | null) => {
    const newValueId = newValue ? newValue.value : null;

    helpers.setValue(newValueId);

    if (onChange) {
      if (newValueId === null) {
        onChange(newValueId);
      } else {
        onChange(Number(newValueId));
      }
    }
  };

  const valueFromField = options.find((option) => option.value === field.value) || null;

  return (
    <Box display="flex" alignItems="center">
      <FormControl fullWidth error={meta.touched && Boolean(meta.error)}>
        <Autocomplete
          id={name}
          options={options}
          getOptionLabel={(option) => (option ? option.label : "")}
          value={valueFromField}
          onChange={handleOnChange}
          aria-required={!!required}
          disabled={disabled}
          renderInput={(params) => (
            <TextField
              {...params}
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
              data-testid={label + "-select"}
              error={meta.touched && Boolean(meta.error)}
            />
          )}
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
      {warningText && (
        <Box ml={1}>
          <CustomHelperText text={warningText} type={CustomTooltip.WARNING} />
        </Box>
      )}
    </Box>
  );
};
