import { TextField } from "@mui/material";
import { debounce } from "lodash";
import React, { useMemo } from "react";

import { DashboardFilters } from "../../requests/CommonRequestFilters";

interface ProjectSearchTextBoxProps {
  defaultValue?: string;
  onChange: (name: keyof DashboardFilters, value: string) => void;
}

const ProjectSearchTextBox: React.FC<ProjectSearchTextBoxProps> = ({ defaultValue, onChange }) => {
  const debouncedOnChange = useMemo(() => {
    return debounce((event) => onChange && onChange("projectSearch", event.target.value), 300);
  }, []);

  return (
    <TextField
      label="Name or exact GSID"
      type="search"
      defaultValue={defaultValue}
      variant="outlined"
      size="small"
      onChange={debouncedOnChange}
      data-testid="project-search-textbox"
    />
  );
};

export default ProjectSearchTextBox;
