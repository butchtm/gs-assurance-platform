import { Box } from "@mui/material";
import React from "react";

import GS_ICON from "../media/gs-gated-logo-black.svg";
import InformationCard from "../../../Reusable/Card/InformationCard";
import SDG_ICON from "../media/sdg-icon-transparent.png";

interface ResourcesProps {
  registryUrl?: string;
}

const Resources: React.FC<ResourcesProps> = ({ registryUrl }): JSX.Element => {
  return (
    <InformationCard title="Resources">
      <>
        <a
          href="https://sdg-tool.goldstandard.org/"
          rel="noreferrer"
          target="_blank"
          style={{ textDecoration: "none" }}
        >
          <Box style={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
            <img src={SDG_ICON} alt="sdg tool icon" style={{ width: "50px", height: "50px", marginRight: "10px" }} />
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
              Sustainable Development Goal Tool
            </Box>
          </Box>
        </a>
        {registryUrl && (
          <a href={registryUrl} rel="noreferrer" target="_blank" style={{ textDecoration: "none" }}>
            <Box style={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
              <img src={GS_ICON} alt="sdg tool icon" style={{ width: "50px", height: "50px", marginRight: "10px" }} />
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
                Impact Registry
              </Box>
            </Box>
          </a>
        )}
      </>
    </InformationCard>
  );
};

export default Resources;
