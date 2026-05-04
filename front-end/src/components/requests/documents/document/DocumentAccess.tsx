import LockIcon from "@mui/icons-material/Lock";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import { Box } from "@mui/material";
import React from "react";

export const DocumentAccess: React.FC<{ access: boolean }> = ({ access }) => {
  return (
    <Box
      sx={{
        display: "inline-flex",
        borderRadius: "5px",
        padding: "1px 6px",
        alignItems: "center",
        marginLeft: "11px",
        fontSize: "13px",
        color: "white",
        backgroundColor: access ? "var(--color-access-public-bg)" : "var(--color-access-private-bg)",
      }}
    >
      {access ? "PUBLIC" : "PRIVATE"}&nbsp;
      {access ? <LockOpenIcon fontSize="small" /> : <LockIcon fontSize="small" />}
    </Box>
  );
};
