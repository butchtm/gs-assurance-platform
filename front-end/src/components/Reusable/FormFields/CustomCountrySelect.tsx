import { Autocomplete, Box, Chip, TextField } from "@mui/material";
import { useField } from "formik";
import React from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";

import CustomHelperText, { CustomTooltip } from "./CustomTooltipText";

interface ReferenceType {
  id: string | number;
  description: string;
  code: string;
}

interface CustomCountrySelectProps {
  name: string;
  label: string;
  helperText?: string | JSX.Element;
  warningText?: string | JSX.Element;
}

export const CustomCountrySelect: React.FC<CustomCountrySelectProps> = ({ name, label, helperText, warningText }) => {
  const [field, , helpers] = useField(name);
  const { activeReferenceTypes } = useReferenceTypes();
  const locations = activeReferenceTypes.ProjectLocation;

  let selectedValue: ReferenceType[] = [];

  if (locations) {
    selectedValue = field.value !== undefined ? locations.filter((option) => field.value.includes(option.id)) : [];
  }

  return (
    <Box display="flex" alignItems="center" sx={{ width: "100%" }}>
      <Autocomplete
        multiple
        fullWidth
        autoHighlight
        options={locations || []}
        getOptionLabel={(option) => option.description}
        value={selectedValue}
        onChange={(event, newValue) => {
          helpers.setValue(newValue.map((item: ReferenceType) => item.id));
        }}
        renderTags={(value, getTagProps) =>
          value.map((option, index) => {
            const { key, ...tagProps } = getTagProps({ index });
            return (
              <Chip
                key={key}
                label={option.description}
                {...tagProps}
                sx={{
                  maxWidth: "100%",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  whiteSpace: "nowrap",
                }}
              />
            );
          })
        }
        renderOption={(props, option) => (
          <Box component="li" sx={{ "& > img": { mr: 2, flexShrink: 0 } }} {...props}>
            <img
              loading="lazy"
              width="20"
              srcSet={`https://flagcdn.com/w40/${option.code.toLowerCase()}.png 2x`}
              src={`https://flagcdn.com/w20/${option.code.toLowerCase()}.png`}
              alt=""
            />
            {option.description} ({option.code})
          </Box>
        )}
        renderInput={(params) => (
          <TextField
            {...params}
            label={label}
            fullWidth
            inputProps={{
              ...params.inputProps,
              autoComplete: "off",
              "data-testid": `${name}-input`,
            }}
          />
        )}
        sx={{
          flex: 1,
          maxWidth: "100%",
          "& .MuiAutocomplete-tag": {
            maxWidth: "200px",
          },
        }}
      />
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
