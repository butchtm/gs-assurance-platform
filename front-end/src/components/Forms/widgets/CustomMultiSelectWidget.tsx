import { Autocomplete, Chip, TextField } from "@mui/material";
import { WidgetProps } from "@rjsf/utils";
import React from "react";

interface Option {
  value: string;
  label: string;
}

const CustomMultiSelectWidget: React.FC<WidgetProps> = ({
  schema,
  id,
  value = [],
  required,
  disabled,
  readonly,
  label,
  onChange,
  onBlur,
  onFocus,
  rawErrors,
}) => {
  // Convert schema enum values to options format
  const enumOptions: Option[] =
    (schema.items as { enum?: string[] })?.enum?.map((item: string) => ({
      value: item,
      label: item,
    })) || [];

  const selectedValues = Array.isArray(value) ? value : [];
  const selectedOptions = selectedValues.map(
    (val) => enumOptions.find((opt) => opt.value === val) || { value: val, label: val },
  );

  const handleChange = (_event: React.SyntheticEvent, newValues: Option[]) => {
    const newValueArray = newValues.map((option) => option.value);
    onChange(newValueArray);
  };

  const hasError = rawErrors && rawErrors.length > 0;

  return (
    <Autocomplete
      multiple
      id={id}
      options={enumOptions}
      value={selectedOptions}
      onChange={handleChange}
      onBlur={onBlur && (() => onBlur(id, selectedValues))}
      onFocus={onFocus && (() => onFocus(id, selectedValues))}
      disabled={disabled || readonly}
      getOptionLabel={(option) => option.label}
      isOptionEqualToValue={(option, value) => option.value === value.value}
      renderTags={(tagValue, getTagProps) =>
        tagValue.map((option, index) => {
          const { key, ...tagProps } = getTagProps({ index });
          return (
            <Chip
              key={key}
              label={option.label}
              {...tagProps}
              sx={{
                backgroundColor: "rgb(0, 186, 190, 0.1)",
                borderRadius: "16px",
                border: "1px solid rgb(0, 186, 190, 0.3)",
                maxWidth: "100%",
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
                "& .MuiChip-label": {
                  color: "rgb(0, 186, 190)",
                  fontSize: "0.875rem",
                  fontWeight: 500,
                },
                "& .MuiChip-deleteIcon": {
                  color: "rgb(0, 186, 190, 0.7)",
                  "&:hover": {
                    color: "rgb(0, 186, 190)",
                  },
                },
                "&:hover": {
                  backgroundColor: "rgb(0, 186, 190, 0.15)",
                },
              }}
            />
          );
        })
      }
      renderInput={(params) => (
        <TextField
          {...params}
          label={
            required ? (
              <>
                {label} <span aria-hidden="true">*</span>
              </>
            ) : (
              label
            )
          }
          error={hasError}
          helperText={hasError ? rawErrors?.[0] : undefined}
          fullWidth
          data-testid={`${id}-multi-select`}
        />
      )}
    />
  );
};

export { CustomMultiSelectWidget };
