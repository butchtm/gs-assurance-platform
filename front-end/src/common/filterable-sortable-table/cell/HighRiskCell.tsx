import { Box } from "@mui/material";
import { MRT_Cell } from "material-react-table";

import { DetailedRiskSummary } from "../../project/Project";

export const HighRiskCell = ({ cell }: { cell: MRT_Cell<DetailedRiskSummary, unknown> }) => {
  const row = cell.row.original;
  const isHighRisk = row.risk;
  return isHighRisk ? (
    <Box
      sx={{
        backgroundColor: "var(--color-risk-high-bg)",
        borderRadius: 1,
        border: "1px solid var(--color-risk-high-border)",
        width: "fit-content",
        padding: "4px",
      }}
    >
      High Risk
    </Box>
  ) : (
    "No Risk"
  );
};
