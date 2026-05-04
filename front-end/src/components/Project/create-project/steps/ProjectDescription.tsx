import { Box, FormHelperText, TextField, Typography, useTheme } from "@mui/material";
import { useField } from "formik";
import { useState } from "react";

import { ProbablePii } from "../../../../common/piiAnalysis/PiiAnalysis";
import CustomHelperText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { PiiWarningPopover } from "../../../Reusable/FormFields/PiiWarningPopover";
import { helperTexts } from "../HelperText";

export const ProjectDescription = ({ probablePii = [] }: { probablePii?: ProbablePii[] }) => {
  const theme = useTheme();
  const [field, meta, helpers] = useField("description");
  const softLimit = 1500;
  const [count, setCount] = useState<number>(field.value.length ?? 0);
  const shouldShowDescriptionWarning = (description: string): boolean => {
    return description.length > softLimit;
  };
  const [showDescriptionWarning, setShowDescriptionWarning] = useState<boolean>(
    shouldShowDescriptionWarning(field.value),
  );

  const getCounterColour = () => {
    if (meta.touched && Boolean(meta.error)) {
      return theme.palette.error.main;
    }
    if (showDescriptionWarning) {
      return "var(--color-warning-soft)";
    }
    return "black";
  };

  return (
    <>
      <Box display="flex" alignItems="center">
        <TextField
          fullWidth
          label="Project Description"
          data-testid="project-description-input"
          error={meta.touched && Boolean(meta.error)}
          value={field.value}
          multiline
          rows={5}
          onChange={(e) => {
            helpers.setValue(e.target.value);
            setCount(e.target.value.length);
            setShowDescriptionWarning(shouldShowDescriptionWarning(e.target.value));
          }}
          InputProps={{
            endAdornment: <CustomHelperText text={helperTexts.projectDescription()} type={CustomTooltip.HELP} />,
          }}
          inputProps={{
            "aria-describedby": showDescriptionWarning ? "description-warning" : undefined,
          }}
        />
        <PiiWarningPopover probablePii={probablePii} field="description" />
      </Box>
      <Box sx={{ display: "flex", justifyContent: "space-between" }}>
        <Box>
          {meta.touched && Boolean(meta.error) ? (
            <FormHelperText error role="alert">
              {meta.error}
            </FormHelperText>
          ) : null}
          {!(meta.touched && Boolean(meta.error)) && showDescriptionWarning ? (
            <FormHelperText
              sx={{ color: "var(--color-warning-soft)" }}
              role="warning"
              aria-live="assertive"
              id="description-warning"
            >
              Recommended description length is less than {softLimit} characters.
            </FormHelperText>
          ) : null}
        </Box>
        <Typography
          variant="caption"
          sx={{
            color: getCounterColour(),
          }}
        >
          {count}/{softLimit}
        </Typography>
      </Box>
    </>
  );
};
