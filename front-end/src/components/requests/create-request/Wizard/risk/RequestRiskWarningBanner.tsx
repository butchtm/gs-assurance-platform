import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { Accordion, AccordionDetails, AccordionSummary, Container, Typography } from "@mui/material";
import { useState } from "react";

import { CurrentRiskSummary } from "../../../../../common/project/Project";

export const RequestRiskWarningBanner = ({ risk, gsid }: { risk: CurrentRiskSummary; gsid: string }) => {
  const [expanded, setExpanded] = useState<boolean>(false);

  return (
    <Container
      maxWidth="md"
      sx={{
        top: 58,
        position: "fixed",
        zIndex: 1000,
        margin: "auto",
        left: "50%",
        transform: `translate(-50%, 0)`,
      }}
    >
      <Accordion
        expanded={expanded}
        onChange={() => setExpanded(!expanded)}
        sx={{
          backgroundColor: "var(--color-risk-high-bg)",
          border: "1px solid var(--color-risk-high-border)",
          borderRadius: "5px",
          boxShadow: "0",
        }}
      >
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography variant="body1">{gsid} - High Risk</Typography>
        </AccordionSummary>
        <AccordionDetails
          sx={{
            display: expanded ? "flex" : "none",
            justifyContent: expanded ? "space-between" : null,
          }}
        >
          <Typography variant="body1">{risk.summary}</Typography>
        </AccordionDetails>
      </Accordion>
    </Container>
  );
};
