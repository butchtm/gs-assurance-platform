import {
  Box,
  DialogContent,
  FormControlLabel,
  List,
  ListItem,
  Popover,
  Radio,
  RadioGroup,
  Typography,
} from "@mui/material";
import React, { useState } from "react";

import { ReferenceType } from "../../../common/project/Project";

import { SelectedFilterOptions } from "./ListFilter";

interface ListFilterDialogProps {
  fieldName: string;
  options: ReferenceType[];
  anchorEl: HTMLButtonElement | null;
  onClose: () => void;
  onFilterChange: (selectedOption: SelectedFilterOptions) => void;
}

export const ListFilterDialog: React.FC<ListFilterDialogProps> = ({
  fieldName,
  options,
  anchorEl,
  onClose,
  onFilterChange,
}) => {
  const [selectedOption, setSelectedOption] = useState<ReferenceType>();

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;
  const allOption = { id: 0, code: "ALL", description: "All" };

  const handleRadioChange = (filterName: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const optionId = Number(event.target.value);
    if (optionId == 0) {
      setSelectedOption(allOption);
    } else {
      const option = options.find((x) => x.id == optionId);
      if (option) {
        setSelectedOption(option);
      }
    }
    const updatedFilters = { [filterName]: optionId };
    onFilterChange(updatedFilters);
  };

  const getOptions = () => {
    return [allOption, ...options];
  };

  return (
    <Popover
      id={id}
      open={open}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{
        vertical: "bottom",
        horizontal: "left",
      }}
    >
      <DialogContent dividers>
        <List>
          <Box key={fieldName} marginBottom={2}>
            <RadioGroup
              name={fieldName}
              value={selectedOption?.id.toString() || ""}
              onChange={handleRadioChange(fieldName)}
            >
              {getOptions().map((option) => (
                <ListItem key={option.id} dense>
                  <FormControlLabel
                    value={option.id.toString()}
                    control={<Radio size="small" />}
                    label={<Typography variant="body2">{option.description}</Typography>}
                    data-testid={`list-filter-option-${fieldName}-${option.id}`}
                  />
                </ListItem>
              ))}
            </RadioGroup>
          </Box>
        </List>
      </DialogContent>
    </Popover>
  );
};
