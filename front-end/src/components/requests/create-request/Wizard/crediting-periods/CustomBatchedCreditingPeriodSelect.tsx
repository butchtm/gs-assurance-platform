import { FormControl, FormHelperText, InputLabel, MenuItem, Select, SelectChangeEvent } from "@mui/material";
import dayjs from "dayjs";
import { useField, useFormikContext } from "formik";
import React from "react";

import { computeDeferredBatchError } from "../../../../../common/form/deferredBatchErrors";
import { CreditingPeriod } from "../../../../../common/project/Project";
import { BatchedProjectRequest } from "../../../Requests";
import { CreateRequestForm } from "../Request";

interface CustomBatchedCreditingPeriodSelectProps {
  project: BatchedProjectRequest;
  areSelectionsFixed: boolean;
  onPeriodChange: (event: SelectChangeEvent<string | number>) => void;
  isCreditingPeriodDisabled: (period: CreditingPeriod) => boolean | undefined;
  incompatibleMethodologiesString: string;
  firstVpaGsid?: string;
}

export const CustomBatchedCreditingPeriodSelect: React.FC<CustomBatchedCreditingPeriodSelectProps> = ({
  project,
  areSelectionsFixed,
  onPeriodChange,
  isCreditingPeriodDisabled,
  incompatibleMethodologiesString,
  firstVpaGsid,
}) => {
  const { values, submitCount, setFieldTouched } = useFormikContext<CreateRequestForm>();

  const fieldName = `batchStepProjectValues.batch.${project.id}.creditingPeriodId`;
  const [field, meta] = useField(fieldName);

  const projectValues = values.batchStepProjectValues?.batch?.[project.id];
  const isUsingExistingPeriod = !projectValues?.useNewCreditingPeriod;
  const isMissing = isUsingExistingPeriod && !projectValues?.creditingPeriodId;

  const { showError: hasError, errorMessage } = computeDeferredBatchError({
    meta: { touched: meta.touched, error: meta.error },
    formValues: values,
    submitCount,
    required: isUsingExistingPeriod,
    isMissing,
    requiredMessage: "Please select a crediting period",
  });

  return (
    <FormControl fullWidth variant="outlined" error={hasError}>
      <InputLabel id="credit-period-id-label">{`Credit Period ID${isUsingExistingPeriod ? "*" : ""}`}</InputLabel>
      <Select
        {...field}
        name={fieldName}
        labelId="credit-period-id-label"
        value={values.batchStepProjectValues?.batch[project.id]?.creditingPeriodId ?? ""}
        onChange={(e) => {
          field.onChange(e);
          onPeriodChange(e);
        }}
        onBlur={(e) => {
          field.onBlur(e);
          setFieldTouched(fieldName, true, true);
        }}
        label={`Credit Period ID${isUsingExistingPeriod ? "*" : ""}`}
        placeholder="Select ID"
        disabled={areSelectionsFixed}
        data-testid="crediting-period-select"
        aria-required={isUsingExistingPeriod}
      >
        {project.creditingPeriods?.map((p) => {
          const periodDisabled = isCreditingPeriodDisabled(p);
          return (
            <MenuItem
              value={p.periodId}
              key={p.periodId}
              disabled={periodDisabled}
              data-testid={`crediting-period-option-${p.periodId}`}
            >
              {`Period ${p.periodId} - ${dayjs.utc(p.startDate).format("DD/MM/YYYY")} to ${dayjs
                .utc(p.endDate)
                .format("DD/MM/YYYY")}`}
              {periodDisabled && incompatibleMethodologiesString.concat(firstVpaGsid || "")}
            </MenuItem>
          );
        })}
      </Select>
      {hasError && <FormHelperText>{errorMessage}</FormHelperText>}
    </FormControl>
  );
};
