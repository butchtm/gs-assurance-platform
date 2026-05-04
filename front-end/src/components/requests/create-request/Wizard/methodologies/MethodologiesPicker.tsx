import { Autocomplete, Button, Grid, IconButton, TextField, Typography } from "@mui/material";
import { ClearIcon } from "@mui/x-date-pickers";
import { FieldArray, useFormikContext } from "formik";
import React from "react";

import { Options } from "../../../../../common/data-types/DataReferenceTypes";
import { FormValues } from "../../../Requests";
import { MethodologyConfiguration } from "../Request";

export const MethodologiesPicker: React.FC<{
  methodologies?: MethodologyConfiguration[];
  methodologyOptions: Options[];
  allDisabled: boolean;
  creditingPeriodId: number | null;
  fieldName: string;
}> = ({ methodologies, methodologyOptions, allDisabled, creditingPeriodId, fieldName }) => {
  const { setFieldValue, errors, touched, submitCount, values } = useFormikContext<FormValues>();

  const isAddDisabled = () => {
    if (!methodologies || methodologies.length === 0) return false;
    const lastMethodologyId = methodologies[methodologies.length - 1].methodologyId;
    return lastMethodologyId === undefined || lastMethodologyId === null || lastMethodologyId === "";
  };

  const shouldShowError = ({
    touched,
    submitCount,
    hasValue,
    error,
  }: {
    touched: boolean;
    submitCount: number;
    hasValue: boolean;
    error: string | undefined;
  }) => {
    return !!(touched || (submitCount > 0 && hasValue)) && !!error;
  };

  const handleMethodologyChange = (methodologyId: number | "", methodologyDescription: string, index: number) => {
    setFieldValue(`${fieldName}[${index}].methodologyId`, methodologyId);
    setFieldValue(`${fieldName}[${index}].methodologyDescription`, methodologyDescription);
  };

  const sanitizeVersionInput = (value: string | number): number | string => {
    const versionStr = typeof value === "number" ? String(value) : value;
    if (versionStr === "0") {
      return "0";
    }
    const versionStrWithoutLeadingZeros = versionStr.replace(/^0+(?!$)/, "");

    if (versionStrWithoutLeadingZeros === "") {
      return "";
    }
    const num = Number(versionStrWithoutLeadingZeros);

    if (Number.isNaN(num) || num < 0) {
      return "";
    }
    return num;
  };

  const handleVersionChange = (value: string | number, index: number) => {
    const sanitized = sanitizeVersionInput(value);
    setFieldValue(`${fieldName}[${index}].methodologyVersion`, sanitized);
  };

  const hasMethodologyOrVersionErrors = (
    config: MethodologyConfiguration,
    index: number,
    fieldArrayErrors: { methodologyId?: string; methodologyVersion?: string }[],
  ): boolean => {
    const hasEmptyMethodology =
      config.methodologyId === null || config.methodologyId === undefined || config.methodologyId === "";
    const hasEmptyVersion =
      config.methodologyVersion === null ||
      config.methodologyVersion === undefined ||
      String(config.methodologyVersion) === "";
    const hasMethodologyError = !!fieldArrayErrors?.[index]?.methodologyId;
    const hasVersionError = !!fieldArrayErrors?.[index]?.methodologyVersion;
    return (hasEmptyMethodology && hasMethodologyError) || (hasEmptyVersion && hasVersionError);
  };

  const getValidationState = (
    config: MethodologyConfiguration,
    index: number,
    fieldArrayTouched: { methodologyId?: boolean; methodologyVersion?: boolean }[],
    fieldArrayErrors: { methodologyId?: string; methodologyVersion?: string }[],
  ) => {
    const methodologyTouched = fieldArrayTouched?.[index]?.methodologyId;
    const versionTouched = fieldArrayTouched?.[index]?.methodologyVersion;

    const hasMethodologyValue =
      config.methodologyId !== undefined && config.methodologyId !== null && config.methodologyId !== "";

    const hasVersionValue =
      config.methodologyVersion !== undefined &&
      config.methodologyVersion !== null &&
      String(config.methodologyVersion) !== "";

    const isVersionZero = config.methodologyVersion === 0 || String(config.methodologyVersion) === "0";

    let showMethodologyError = shouldShowError({
      touched: methodologyTouched ?? false,
      submitCount,
      hasValue: hasMethodologyValue,
      error: fieldArrayErrors?.[index]?.methodologyId,
    });

    let showVersionError = shouldShowError({
      touched: versionTouched ?? false,
      submitCount,
      hasValue: hasVersionValue && !isVersionZero,
      error: fieldArrayErrors?.[index]?.methodologyVersion,
    });

    const batchProjectValues = (values as { batchStepProjectValues?: { finalProjectInitialSubmitCount?: number } })
      .batchStepProjectValues;
    const finalProjectBaselineSubmit = batchProjectValues?.finalProjectInitialSubmitCount;
    const isDeferredPhase = typeof finalProjectBaselineSubmit === "number" && submitCount > finalProjectBaselineSubmit;
    if (isDeferredPhase) {
      if (!hasMethodologyValue) showMethodologyError = true;
      if (!hasVersionValue) showVersionError = true;
    }

    return {
      showMethodologyError,
      showVersionError,
      isVersionZero,
    };
  };

  return (
    <FieldArray
      name={fieldName}
      render={({ remove, push }) => {
        type MethodologyError = {
          methodologyId?: string;
          methodologyVersion?: string;
        };
        const fieldArrayErrors = (errors?.[fieldName as keyof FormValues] as MethodologyError[] | undefined) || [];
        return (
          <>
            {methodologies &&
              methodologies.length > 0 &&
              methodologies.map((methodologyConfiguration: MethodologyConfiguration, index: number) => {
                type MethodologyTouched = {
                  methodologyId?: boolean;
                  methodologyVersion?: boolean;
                };
                const fieldArrayTouched =
                  (touched && (touched[fieldName as keyof FormValues] as MethodologyTouched[] | undefined)) || [];

                const validation = getValidationState(
                  methodologyConfiguration,
                  index,
                  fieldArrayTouched,
                  fieldArrayErrors,
                );

                return (
                  <Grid
                    key={methodologyConfiguration.methodologyId}
                    container
                    sx={{ marginBottom: "20px" }}
                    item
                    xs={12}
                    spacing={2}
                    alignItems="center"
                  >
                    <Grid item xs={9}>
                      <Autocomplete
                        disableClearable
                        options={methodologyOptions}
                        value={methodologyOptions.find(
                          (opt) => Number(opt.value) === methodologyConfiguration.methodologyId,
                        )}
                        isOptionEqualToValue={(option, value) => option.value === value.value}
                        getOptionDisabled={(option) =>
                          methodologies.some(
                            (config, idx) => idx !== index && Number(config.methodologyId) === Number(option.value),
                          )
                        }
                        renderInput={(params) => (
                          <TextField
                            {...params}
                            label={
                              <>
                                Project Methodology <span aria-hidden="true">*</span>
                              </>
                            }
                            inputProps={{
                              ...params.inputProps,
                              "data-testid": `methodology-input-${index}`,
                              "aria-required": "true",
                              "aria-label": `Project Methodology ${index + 1}`,
                            }}
                            error={!!validation.showMethodologyError}
                          />
                        )}
                        onChange={(_, selectedOption) =>
                          handleMethodologyChange(
                            selectedOption ? Number(selectedOption.value) : "",
                            selectedOption ? selectedOption.label : "",
                            index,
                          )
                        }
                        disabled={allDisabled}
                      />
                    </Grid>
                    <Grid item xs={2}>
                      <TextField
                        label={
                          <>
                            Version <span aria-hidden="true">*</span>
                          </>
                        }
                        type="number"
                        value={
                          methodologyConfiguration.methodologyVersion === undefined
                            ? ""
                            : String(methodologyConfiguration.methodologyVersion)
                        }
                        onChange={(e) => handleVersionChange(e.target.value, index)}
                        inputProps={{
                          "data-testid": `version-input-${index}`,
                          "aria-required": "true",
                          "aria-label": `Methodology Version ${
                            index + 1
                          }. Enter a number or 0 for unapproved methodologies`,
                        }}
                        error={!!validation.showVersionError}
                        disabled={allDisabled}
                      />
                    </Grid>
                    {!allDisabled && (
                      <Grid item xs={1}>
                        <IconButton
                          onClick={() => remove(index)}
                          data-testid={`remove-${index}`}
                          aria-label={`Remove methodology ${index + 1}`}
                          title={`Remove methodology ${index + 1}`}
                        >
                          <ClearIcon color="error" />
                        </IconButton>
                      </Grid>
                    )}
                  </Grid>
                );
              })}
            {submitCount > 1 && (
              <>
                {methodologies?.some((config, index) =>
                  hasMethodologyOrVersionErrors(config, index, fieldArrayErrors),
                ) && (
                  <Typography
                    sx={{
                      color: (theme) => theme.palette.error.main,
                      fontSize: "12px",
                      marginBottom: "8px",
                    }}
                    role="alert"
                    aria-live="assertive"
                  >
                    Methodology and version number required
                  </Typography>
                )}
              </>
            )}
            {submitCount > 0 && (
              <>
                {methodologies?.some(
                  (config) => config.methodologyVersion === 0 || String(config.methodologyVersion) === "0",
                ) && (
                  <Typography
                    sx={{
                      color: "var(--color-warning-soft)",
                      fontSize: "14px",
                      marginTop: "8px",
                      marginBottom: "8px",
                    }}
                    aria-live="polite"
                  >
                    Methodology Version 0 should only be used for unapproved methodologies
                  </Typography>
                )}
              </>
            )}

            <Button
              variant="outlined"
              disabled={isAddDisabled() || allDisabled}
              onClick={() =>
                push({
                  methodologyId: "",
                  methodologyVersion: "",
                  creditingPeriodId: creditingPeriodId,
                })
              }
            >
              Add Methodology
            </Button>
          </>
        );
      }}
    />
  );
};
