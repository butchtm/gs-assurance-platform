import React from "react";
import { Box } from "@mui/material";

interface RequestHeadlineProps {
  value: string;
  completedApproved?: boolean;
}

export const RequestHeadline: React.FC<RequestHeadlineProps> = ({ value, completedApproved }) => {
  return (
    <Box
      sx={{
        margin: "0.5rem",
        borderRadius: "5px",
        backgroundColor: completedApproved ? "lightgreen" : "lightgray",
        padding: "0 10px",
      }}
    >
      <span>{value}</span>
    </Box>
  );
};
