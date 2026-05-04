import { Chip } from "@mui/material";
import React from "react";

interface AccountChipProps {
  label: string;
}

export const AccountChip: React.FC<AccountChipProps> = ({ label }) => {
  return (
    <Chip
      label={label}
      sx={{
        margin: "0.5rem",
        backgroundColor: "var(--color-chip-bg-neutral)",
        color: "var(--color-text-black)",
        fontWeight: "bold",
        height: "auto",
        "& .MuiChip-label": {
          display: "block",
          whiteSpace: "normal",
        },
      }}
    />
  );
};
