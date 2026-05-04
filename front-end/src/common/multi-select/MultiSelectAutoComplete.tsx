import { Badge, Box, Button, DialogContent, Popover, TextField } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import { DropdownOption } from "material-react-table";
import { useEffect, useState } from "react";

import { autoFocusTextInputs } from "./AutoCompleteFunctions";

export const MultiSelectAutocomplete = ({
  options,
  label,
  selectedOptions,
  onSelectionChange,
}: {
  options: DropdownOption[];
  label: string;
  selectedOptions: DropdownOption[];
  onSelectionChange: (selected: DropdownOption[]) => void;
}) => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    setTimeout(() => setMenuOpen(true), 200);
  }, []);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
    setTimeout(doAutoFocusTextInputs, 200);
  };

  const doAutoFocusTextInputs = () => {
    autoFocusTextInputs(handleClose, label);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleChange = (event: React.SyntheticEvent<Element, Event>, newValues: DropdownOption[]): void => {
    onSelectionChange(newValues);
  };

  return (
    <>
      <Badge badgeContent={selectedOptions.length} sx={{ marginRight: "11px" }} color="primary" max={9}>
        <Button variant="outlined" onClick={handleClick} aria-haspopup="true" fullWidth>
          {label}
        </Button>
      </Badge>

      <Popover
        open={!!anchorEl}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
      >
        <DialogContent dividers>
          <Box id={`filter-element-${label}`} marginBottom={2}>
            <Autocomplete
              multiple
              options={options}
              value={selectedOptions}
              onChange={handleChange}
              fullWidth
              open={menuOpen}
              isOptionEqualToValue={(option, value) => {
                if (typeof option !== "string" && typeof value !== "string") {
                  return option.value === value.value;
                }
                return false;
              }}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label={label}
                  sx={{ minWidth: 250, minHeight: 56 }}
                  data-testid={`${label}-multiselect`}
                />
              )}
            />
          </Box>
        </DialogContent>
      </Popover>
    </>
  );
};
