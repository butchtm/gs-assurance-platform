import { Box, FormHelperText, TextField } from "@mui/material";
import { useField, useFormikContext } from "formik";

import { computeDeferredBatchError } from "../../../common/form/deferredBatchErrors";
import { CreateRequestForm } from "../../requests/create-request/Wizard/Request";
import { isForIssuanceRequest } from "../../../common/request/GSRequest";

import CustomHelperText, { CustomTooltip } from "./CustomTooltipText";

export const EmissionsReductionInput = ({
  fieldValue,
  helpText,
  onChange,
  name = "emissionReductionAmount",
  deferUntilFinalSubmit = false,
  required = false,
}: {
  fieldValue: number | null | undefined;
  helpText: string | JSX.Element;
  onChange: (value: number) => void;
  name?: string;
  deferUntilFinalSubmit?: boolean;
  required?: boolean;
}) => {
  const { submitCount, values } = useFormikContext<CreateRequestForm>();
  const [, meta] = useField(name);

  const MAX_EMISSIONS_AMOUNT = 10_000_000;

  const handleChange = (event: { target: { value: string } }) => {
    const newValue = event.target.value;
    let number = parseInt(newValue.replace(/\D/g, ""), 10);
    if (isNaN(number)) {
      number = 0;
    }
    onChange(Math.min(number, MAX_EMISSIONS_AMOUNT));
  };

  const formattedValue = fieldValue ? fieldValue.toLocaleString() : "";

  const isMissing = fieldValue === null || fieldValue === undefined; // 0 is valid
  const { showError, errorMessage } = computeDeferredBatchError({
    meta: { touched: meta.touched, error: meta.error },
    formValues: values,
    submitCount,
    required: true,
    isMissing,
    requiredMessage: "Emission reduction amount is required",
    deferUntilFinal: deferUntilFinalSubmit,
  });
  const errorText = errorMessage;

  const toEmissionsLabel = (createRequestForm: CreateRequestForm) => {
    if (isForIssuanceRequest(createRequestForm)) {
      return "Verified Total Emission Reduction/Removal";
    }
    return "Estimated Annual Emission Reduction/Removal";
  };

  const label = toEmissionsLabel(values);

  return (
    <>
      <Box display="flex" alignItems="center">
        <TextField
          fullWidth
          label={
            required || deferUntilFinalSubmit ? (
              <>
                {label}
                <span aria-hidden="true">*</span>
              </>
            ) : (
              label
            )
          }
          data-testid="emission-reduction-input"
          error={showError}
          value={formattedValue}
          onChange={handleChange}
          inputProps={{
            min: 0,
            max: MAX_EMISSIONS_AMOUNT,
            "aria-required": required || deferUntilFinalSubmit || false,
            "aria-describedby": meta.touched && meta.error ? `${name}-error` : undefined,
          }}
          InputProps={{
            endAdornment: <CustomHelperText text={helpText} type={CustomTooltip.HELP} />,
          }}
        />
      </Box>
      {showError ? (
        <FormHelperText error role="alert" sx={{ ml: "15px" }}>
          {errorText}
        </FormHelperText>
      ) : null}
    </>
  );
};
