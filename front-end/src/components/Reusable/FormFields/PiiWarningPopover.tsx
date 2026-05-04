import { Warning } from "@mui/icons-material";
import { Alert, Box, IconButton, List, ListItem, ListItemText, Popover, Typography, useTheme } from "@mui/material";
import React, { useState } from "react";

import { ProbablePii } from "../../../common/piiAnalysis/PiiAnalysis";

interface PiiWarningPopoverProps {
  probablePii: ProbablePii[];
  field: string;
}

export const PiiWarningPopover: React.FC<PiiWarningPopoverProps> = ({ probablePii, field }) => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const theme = useTheme();

  const open = Boolean(anchorEl);
  const id = open ? `pii-popover-${field}` : undefined;

  const fieldPii = probablePii.filter((pii) => pii.field === field);

  if (fieldPii.length === 0) {
    return null;
  }

  return (
    <>
      <IconButton
        aria-describedby={id}
        onClick={handleClick}
        size="small"
        sx={{
          color: theme.palette.warning.main,
          ml: 1,
          "&:hover": {
            backgroundColor: theme.palette.warning.light,
          },
        }}
      >
        <Warning />
      </IconButton>
      <Popover
        id={id}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "left",
        }}
      >
        <Box sx={{ p: 2, maxWidth: 300 }}>
          <Alert severity="warning" sx={{ mb: 2 }}>
            <Typography variant="subtitle2" gutterBottom>
              PII Detected
            </Typography>
            <Typography variant="body2">
              The following potentially personally identifiable information was detected in this field:
            </Typography>
          </Alert>
          <List dense>
            {fieldPii.map((pii) => (
              <ListItem key={`${pii.identifiedString}-${pii.entity}`} sx={{ py: 0.5 }}>
                <ListItemText
                  primary={pii.identifiedString}
                  secondary={`${pii.entity} (${pii.probability} probability)`}
                  primaryTypographyProps={{ fontWeight: "bold", fontSize: "0.875rem" }}
                  secondaryTypographyProps={{ fontSize: "0.75rem" }}
                />
              </ListItem>
            ))}
          </List>
        </Box>
      </Popover>
    </>
  );
};
