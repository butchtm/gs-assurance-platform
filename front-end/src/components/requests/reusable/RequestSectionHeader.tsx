import { Box } from "@mui/material";
import React from "react";

interface RequestSectionHeaderProps {
  name: string;
}

export const RequestSectionHeader: React.FC<RequestSectionHeaderProps> = ({ name }) => {
  return (
    <Box
      sx={{
        borderBottom: "1px solid lightgray",
        padding: "16px 10px",
        textAlign: "left",
      }}
    >
      {name}
    </Box>
  );
};
