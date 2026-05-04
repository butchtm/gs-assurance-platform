import { Button } from "@mui/material";
import React, { useState } from "react";

import { ReferenceType } from "../../../common/project/Project";

import { ListFilterDialog } from "./ListFilterDialog";

export interface SelectedFilterOptions {
  [key: string]: string | number;
}

interface ListFilterProps {
  fieldName: string;
  label: string;
  options: ReferenceType[];
  onFilterChange: (selectedOptions: SelectedFilterOptions) => void;
}

export const ListFilter: React.FC<ListFilterProps> = ({ fieldName, label, options, onFilterChange }) => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return options?.length ? (
    <>
      <Button variant="outlined" onClick={handleClick} sx={{ minWidth: "auto" }}>
        {label}
      </Button>
      <ListFilterDialog
        fieldName={fieldName}
        options={options}
        anchorEl={anchorEl}
        onClose={handleClose}
        onFilterChange={onFilterChange}
      />
    </>
  ) : null;
};
