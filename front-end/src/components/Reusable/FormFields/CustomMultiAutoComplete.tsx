import {
  Autocomplete,
  AutocompleteOwnerState,
  AutocompleteRenderGetTagProps,
  Box,
  FormControl,
  FormHelperText,
  TextField,
} from "@mui/material";
import { useField, useFormikContext } from "formik";
import React, { ReactNode } from "react";

import { Options } from "../../../common/data-types/DataReferenceTypes";

import CustomHelperText, { CustomTooltip } from "./CustomTooltipText";

interface CustomMultiSelectProps {
  name: string;
  label: string;
  options: Options[];
  value?: string[] | number[];
  helperText?: string | JSX.Element;
  warningText?: string | JSX.Element;
  preserveFirstValue?: boolean;
  disabled?: boolean;
  customRenderChips?: (
    value: Options[],
    getTagProps: AutocompleteRenderGetTagProps,
    ownerState: AutocompleteOwnerState<Options, true, false, false, "div">,
  ) => ReactNode;
  required?: boolean;
  showErrorAfterSubmit?: boolean;
}

export const CustomMultiAutoComplete: React.FC<CustomMultiSelectProps> = ({
  name,
  label,
  options,
  helperText,
  warningText,
  preserveFirstValue,
  disabled,
  customRenderChips,
  required,
  showErrorAfterSubmit,
}) => {
  const [field, meta, helpers] = useField(name);

  interface FormikSubmitCountContext {
    submitCount: number;
  }
  const { submitCount } = useFormikContext<FormikSubmitCountContext>();

  const handleOnChange = (event: React.SyntheticEvent<Element, Event>, newValue: Options[] | null) => {
    if (preserveFirstValue) {
      //If this is set the first value of the autocomplete cannot be removed
      const firstOptionValue = field.value[0];
      const firstOption = options.find((option) => option.value === firstOptionValue);

      if (newValue && firstOption) {
        if (newValue.length === 0 || newValue[0].value !== firstOptionValue) {
          newValue = [firstOption, ...newValue.filter((option) => option.value !== firstOptionValue)];
        }
      }
    }

    const selectedValues = newValue?.map((item) => item.value) || [];
    helpers.setValue(selectedValues);
  };

  let valueFromField: Options[] | undefined;

  if (field.value && Array.isArray(field.value)) {
    valueFromField = field.value
      .map((id: number) => options.find((option) => option.value === id))
      .filter(Boolean) as Options[];
  }

  const displayError = (showErrorAfterSubmit ? submitCount > 0 : meta.touched) && Boolean(meta.error);

  return (
    <>
      <Box display="flex" alignItems="center" width="100%">
        <FormControl fullWidth error={displayError}>
          <Box flexGrow={1}>
            <Autocomplete
              multiple
              id={name}
              options={options}
              getOptionLabel={(option) => (option ? option.label : "")}
              value={valueFromField}
              onChange={handleOnChange}
              aria-required={required}
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
                  data-testid={label + "-input"}
                  error={displayError}
                />
              )}
              disabled={disabled}
              renderTags={customRenderChips}
            />
          </Box>
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
      {displayError && (
        <FormHelperText error role="alert">
          {meta.error}
        </FormHelperText>
      )}
    </>
  );
};
