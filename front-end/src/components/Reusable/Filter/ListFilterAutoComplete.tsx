import { Badge, Box, Button, Popover, TextField } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import React, { useState } from "react";

import { FilterOptions } from "../../requests/CommonRequestFilters";

interface ListFilterAutoCompleteProps {
  id: string;
  label: string;
  options: FilterOptions[];
  value: FilterOptions[];
  onChange: (event: React.SyntheticEvent<Element, Event>, newValue: FilterOptions[]) => void;
}

const ListFilterAutoComplete: React.FC<ListFilterAutoCompleteProps> = ({ id, label, options, value, onChange }) => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const popoverId = open ? `${id}-popover` : undefined;

  return (
    <Box>
      <Badge
        badgeContent={value.length}
        color="primary"
        anchorOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
        showZero={false}
      >
        <Button variant="outlined" aria-describedby={popoverId} onClick={handleClick} sx={{ minWidth: "auto" }}>
          {label}
        </Button>
      </Badge>
      <Popover
        id={popoverId}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
      >
        <Box p={2}>
          <Autocomplete
            multiple
            id={id}
            options={options}
            getOptionLabel={(option) => (option ? option.label : "")}
            value={value}
            onChange={onChange}
            renderInput={(params) => (
              <TextField
                {...params}
                label={label}
                sx={{ minWidth: 250, minHeight: 56 }}
                data-testid={label + "-select"}
              />
            )}
          />
        </Box>
      </Popover>
    </Box>
  );
};

export default ListFilterAutoComplete;
