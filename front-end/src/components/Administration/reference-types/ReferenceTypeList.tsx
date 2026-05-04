import { Collapse, List, ListItemButton, ListItemText } from "@mui/material";
import ExpandLess from "@mui/icons-material/ExpandLess";
import ExpandMore from "@mui/icons-material/ExpandMore";
import React, { useState } from "react";

import { PageOption } from "../AdministrationLandingPage";

export const ReferenceTypeList: React.FC<{
  onSelect: (referenceType: PageOption) => void;
  currentPage: PageOption | undefined;
}> = ({ onSelect, currentPage }) => {
  const [open, setOpen] = useState<boolean>(true);
  return (
    <>
      <ListItemButton onClick={() => setOpen(!open)}>
        <ListItemText primary="Reference Types" />
        {open ? <ExpandLess /> : <ExpandMore />}
      </ListItemButton>
      <Collapse in={open} unmountOnExit>
        <List component="div" disablePadding>
          <ListItemButton
            sx={{ pl: 4 }}
            onClick={() => onSelect(PageOption.METHODOLOGY)}
            selected={currentPage === PageOption.METHODOLOGY}
          >
            <ListItemText primary="Methodologies" />
          </ListItemButton>
        </List>
      </Collapse>
    </>
  );
};
