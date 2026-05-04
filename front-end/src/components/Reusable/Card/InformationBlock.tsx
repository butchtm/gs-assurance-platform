import { Box } from "@mui/material";
import React, { ReactNode } from "react";

import InformationBox from "./InformationBox";

interface InformationBlockProps {
  title: string;
  icon?: ReactNode;
  onClick?: () => void;
  children?: string | JSX.Element | (JSX.Element | null)[];
  tooltip?: ReactNode;
}

const InformationBlock: React.FC<InformationBlockProps> = ({ icon, title, children, onClick, tooltip }) => {
  return (
    <>
      <Box
        sx={{
          fontSize: "13px",
          display: "block",
          color: "var(--color-accent-teal-dark)",
          textTransform: "uppercase",
          fontWeight: "bold",
          marginBottom: "0.5rem",
        }}
      >
        {!!icon && icon} {title} {!!tooltip && tooltip}
      </Box>
      <InformationBox onClick={onClick}>{children}</InformationBox>
    </>
  );
};

export default InformationBlock;
