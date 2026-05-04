import EditIcon from "@mui/icons-material/Edit";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  accordionClasses,
  AccordionDetails,
  AccordionSummary,
  Box,
  IconButton,
  Typography,
} from "@mui/material";
import { useState } from "react";

import { CurrentRiskSummary } from "../../../../common/project/Project";

export const RiskWarningBanner = ({ risk, openRiskModal }: { risk: CurrentRiskSummary; openRiskModal: () => void }) => {
  const [expanded, setExpanded] = useState<boolean>(false);

  return (
    <Box
      sx={{
        position: "fixed",
        top: 70,
        zIndex: 1000,
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
          [`& .${accordionClasses.region}`]: {
            width: expanded ? "600px" : "200px",
          },
        }}
      >
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography variant="h6">High Risk</Typography>
        </AccordionSummary>
        <AccordionDetails
          sx={{
            display: expanded ? "flex" : "none",
            justifyContent: expanded ? "space-between" : null,
            gap: 1,
          }}
        >
          <Box
            sx={{
              flex: 1,
              maxHeight: "200px",
              overflowY: "auto",
              pr: 1,
              "&::-webkit-scrollbar": {
                width: "8px",
              },
              "&::-webkit-scrollbar-track": {
                backgroundColor: "var(--color-scrollbar-track)",
                borderRadius: "4px",
              },
              "&::-webkit-scrollbar-thumb": {
                backgroundColor: "var(--color-risk-high-border)",
                borderRadius: "4px",
                "&:hover": {
                  backgroundColor: "var(--color-risk-high-thumb-hover)",
                },
              },
            }}
          >
            <Typography
              variant="body1"
              sx={{
                wordWrap: "break-word",
                whiteSpace: "pre-wrap",
                overflowWrap: "break-word",
              }}
            >
              {risk.summary}
            </Typography>
          </Box>
          <IconButton sx={{ height: "fit-content" }} onClick={() => openRiskModal()} data-testid="update-risk">
            {<EditIcon />}
          </IconButton>
        </AccordionDetails>
      </Accordion>
    </Box>
  );
};
