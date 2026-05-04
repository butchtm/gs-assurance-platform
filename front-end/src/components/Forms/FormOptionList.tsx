import { Collapse, List, ListItemButton, ListItemText } from "@mui/material";
import ExpandLess from "@mui/icons-material/ExpandLess";
import ExpandMore from "@mui/icons-material/ExpandMore";
import React, { useState } from "react";

import { PageOption } from "../Administration/AdministrationLandingPage";

export const FormOptionList: React.FC<{
  onSelect: (formOption: PageOption) => void;
  currentPage: PageOption | undefined;
}> = ({ onSelect, currentPage }) => {
  const [open, setOpen] = useState<boolean>(true);
  return (
    <>
      <ListItemButton onClick={() => setOpen(!open)}>
        <ListItemText primary="Forms" />
        {open ? <ExpandLess /> : <ExpandMore />}
      </ListItemButton>
      <Collapse in={open} unmountOnExit>
        <List component="div" disablePadding>
          <ListItemButton
            sx={{ pl: 4 }}
            onClick={() => onSelect(PageOption.SIMPLE_FORM)}
            selected={currentPage === PageOption.SIMPLE_FORM}
          >
            <ListItemText primary="Simple Form" />
          </ListItemButton>
          <ListItemButton
            sx={{ pl: 4 }}
            onClick={() => onSelect(PageOption.STEPPER_FORM)}
            selected={currentPage === PageOption.STEPPER_FORM}
          >
            <ListItemText primary="Stepper Form" />
          </ListItemButton>
        </List>
      </Collapse>
    </>
  );
};
