import React from "react";
import { useField } from "formik";
import { Autocomplete, FormHelperText, TextField } from "@mui/material";

import { CreateMethodology } from "../../../../common/project/Project";
import { Options } from "../../../../common/data-types/DataReferenceTypes";

export const CreateMethodologySelect: React.FC<{
  currentMethodologies: CreateMethodology[];
  creditingPeriodIndex: number;
  methodologyOptions: Options[];
  methodologyIndex: number;
}> = ({ currentMethodologies, creditingPeriodIndex, methodologyOptions, methodologyIndex }) => {
  const name = `creditingPeriods[${creditingPeriodIndex}].methodologies[${methodologyIndex}].id`;
  const [field, meta, helpers] = useField(name);

  function isOptionDisabled(
    methodologyIndex: number,
    currentMethodologies: CreateMethodology[],
    option: Options,
  ): boolean {
    return currentMethodologies.some((m, idx) => idx !== methodologyIndex && Number(m.id) === Number(option.value));
  }

  return (
    <>
      {methodologyOptions.length > 0 && (
        <Autocomplete
          disableClearable
          options={methodologyOptions}
          getOptionLabel={(option) => option.label}
          getOptionDisabled={(option) => isOptionDisabled(methodologyIndex, currentMethodologies, option)}
          value={
            field.value == null
              ? undefined
              : methodologyOptions.find((opt) => Number(opt.value) === Number(field.value))
          }
          onChange={(_, selectedOption) => {
            helpers.setValue(selectedOption ? Number(selectedOption.value) : "");
          }}
          renderInput={(params) => (
            <TextField
              {...params}
              label={
                <>
                  Methodology<span aria-hidden="true">*</span>
                </>
              }
              inputProps={{
                ...params.inputProps,
                "data-testid": `methodology-input-${methodologyIndex}`,
              }}
              error={meta.touched && Boolean(meta.error)}
            />
          )}
          data-testid="methodology-autocomplete"
          aria-required
        />
      )}
      {meta.touched && Boolean(meta.error) ? (
        <FormHelperText error role="alert">
          {meta.error}
        </FormHelperText>
      ) : null}
    </>
  );
};
