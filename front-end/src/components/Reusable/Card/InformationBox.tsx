import { Box } from "@mui/material";
import React from "react";

interface InformationBoxProps {
  children?: string | JSX.Element | (JSX.Element | null)[];
  onClick?: () => void;
}

const InformationBox: React.FC<InformationBoxProps> = ({ children, onClick }) => {
  return (
    <Box
      sx={{
        fontSize: "small",
        color: "var(--color-text-muted)",
        marginBottom: "10px",
        cursor: onClick ? "pointer" : "auto",
        wordBreak: "break-word",
        overflowWrap: "break-word",
      }}
      onClick={onClick}
    >
      {children}
    </Box>
  );
};

export default InformationBox;
