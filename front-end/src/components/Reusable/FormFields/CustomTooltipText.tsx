import { WarningAmberOutlined } from "@mui/icons-material";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import { IconButton, Popover, Typography } from "@mui/material";
import React, { CSSProperties, useState } from "react";

export enum CustomTooltip {
  WARNING,
  HELP,
}

interface FieldHelperTextProps {
  text: string | JSX.Element;
  type: CustomTooltip;
  styleOverrides?: CSSProperties;
  altText?: string;
}

const CustomTooltipText: React.FC<FieldHelperTextProps> = ({ text, type, styleOverrides, altText }) => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;

  return (
    <>
      <IconButton
        aria-describedby={id}
        aria-label={altText ?? "Help"}
        onClick={handleClick}
        size="small"
        sx={{
          ...styleOverrides,
          ...(type === CustomTooltip.WARNING && { color: "warning.main" }),
        }}
        data-testid={type === CustomTooltip.HELP ? "help-icon" : "warning-icon"}
      >
        {type == CustomTooltip.HELP ? <HelpOutlineIcon /> : <WarningAmberOutlined />}
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
        <Typography sx={{ p: 2, maxWidth: "300px", wordWrap: "break-word" }}>{text}</Typography>
      </Popover>
    </>
  );
};

export default CustomTooltipText;
