import { Badge, Box, Button, DialogContent, List, Popover } from "@mui/material";
import {
  MRT_Header,
  MRT_TableHeadCellFilterContainer,
  MRT_TableInstance,
  type MRT_RowData,
} from "material-react-table";
import React, { useState } from "react";

import { autoFocusTextInputs } from "../multi-select/AutoCompleteFunctions";

export const FilterButtonMenu = <T extends MRT_RowData>({
  header,
  tableConfig,
}: {
  header: MRT_Header<T>;
  tableConfig: MRT_TableInstance<T>;
}) => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
    setTimeout(doAutoFocusTextInputs, 150);
  };

  const doAutoFocusTextInputs = (handleClose: () => void) => {
    autoFocusTextInputs(handleClose, header.id);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const getFilterCount = (filterValue: unknown): number => {
    if (Array.isArray(filterValue)) {
      const removedUndefined = filterValue.filter((val) => val !== undefined && val !== null);
      return removedUndefined.length;
    }
    if (filterValue != undefined) {
      return 1;
    }
    return 0;
  };

  return (
    <>
      <Badge
        badgeContent={getFilterCount(header.column.getFilterValue())}
        sx={{ marginRight: "11px" }}
        color="primary"
        max={9}
      >
        <Button variant="outlined" onClick={handleClick} aria-haspopup="true">
          {header.column.columnDef.header}
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
          <List>
            <Box id={`filter-element-${header.id}`} marginBottom={2}>
              <MRT_TableHeadCellFilterContainer header={header} table={tableConfig} />
            </Box>
          </List>
        </DialogContent>
      </Popover>
    </>
  );
};
