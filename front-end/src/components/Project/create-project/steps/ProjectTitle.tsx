import { Box, FormHelperText, TextField, Typography, useTheme } from "@mui/material";
import { useField } from "formik";
import { useEffect, useState } from "react";

import CustomHelperText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";
import { PiiWarningPopover } from "../../../Reusable/FormFields/PiiWarningPopover";
import { helperTexts } from "../HelperText";
import { ProbablePii } from "../../../../common/piiAnalysis/PiiAnalysis";
import { Project } from "../../../../common/project/Project";

type ProjectTitleParent = Pick<Project, "gsid">;

const getProjectTitlePrefix = (parentPoa?: ProjectTitleParent, parentRealCase?: ProjectTitleParent) => {
  let titlePrefix = "";
  if (parentPoa) {
    titlePrefix = parentPoa.gsid + "-";
  }
  if (parentRealCase) {
    titlePrefix += parentRealCase.gsid + "-";
  }
  return titlePrefix;
};

export const ProjectTitle = ({
  probablePii = [],
  parentPoa,
  parentRealCase,
  isUpdate,
  disabled,
}: {
  probablePii?: ProbablePii[];
  parentPoa?: ProjectTitleParent;
  parentRealCase?: ProjectTitleParent;
  isUpdate?: boolean;
  disabled?: boolean;
}) => {
  const [field, meta, helpers] = useField("name");
  const theme = useTheme();
  const [count, setCount] = useState<number>(field.value?.length ?? 0);
  const [prefix, setPrefix] = useState<string>(getProjectTitlePrefix(parentPoa, parentRealCase));

  const setTitleField = (newValue: string) => {
    helpers.setValue(newValue);
    setCount(newValue.length);
  };

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    let newValue = e.target.value;
    if (!isUpdate && prefix && !newValue.startsWith(prefix)) {
      newValue = prefix;
    }
    setTitleField(newValue);
  };

  useEffect(() => {
    if (isUpdate) {
      return;
    }
    const newPrefix = getProjectTitlePrefix(parentPoa, parentRealCase);
    setPrefix(newPrefix);
    setTitleField(newPrefix);
  }, [parentPoa, parentRealCase]);

  return (
    <>
      <Box display="flex" alignItems="center">
        <TextField
          fullWidth
          label={
            <>
              Project Title<span aria-hidden="true">*</span>
            </>
          }
          data-testid="project-title-input"
          error={meta.touched && Boolean(meta.error)}
          value={field.value}
          onChange={handleTitleChange}
          disabled={disabled}
          aria-required
          InputProps={{
            endAdornment: <CustomHelperText text={helperTexts.projectTitle()} type={CustomTooltip.HELP} />,
          }}
        />
        <PiiWarningPopover probablePii={probablePii} field="title" />
      </Box>
      <Box sx={{ display: "flex", justifyContent: "space-between" }}>
        <Box>
          {meta.touched && Boolean(meta.error) ? (
            <FormHelperText error role="alert">
              {meta.error}
            </FormHelperText>
          ) : null}
        </Box>
        <Typography
          variant="caption"
          sx={{
            color: meta.touched && Boolean(meta.error) ? theme.palette.error.main : "black",
          }}
        >
          {count}/255
        </Typography>
      </Box>
    </>
  );
};
