import React from "react";
import { useField, useFormikContext } from "formik";
import { FormHelperText, TextField } from "@mui/material";

import { CreateProjectForm } from "../../../../common/project/Project";

export const CreateMethodologyVersion: React.FC<{
  creditingPeriodIndex: number;
  methodologyIndex: number;
}> = ({ creditingPeriodIndex, methodologyIndex }) => {
  const { values, setFieldValue } = useFormikContext<CreateProjectForm>();
  const name = `creditingPeriods[${creditingPeriodIndex}].methodologies[${methodologyIndex}].version`;
  const meta = useField(name)[1];

  return (
    <>
      <TextField
        type="number"
        label={
          <>
            Version<span aria-hidden="true">*</span>
          </>
        }
        value={values.creditingPeriods[creditingPeriodIndex].methodologies[methodologyIndex].version ?? ""}
        onChange={(e) => {
          const value = e.target.value;
          setFieldValue(name, value === "" ? null : Number(value));
        }}
        inputProps={{
          "data-testid": `version-input-${methodologyIndex}`,
        }}
        error={meta.touched && Boolean(meta.error)}
        aria-required
      />
      {meta.touched && Boolean(meta.error) ? (
        <FormHelperText error role="alert">
          {meta.error}
        </FormHelperText>
      ) : null}
    </>
  );
};
