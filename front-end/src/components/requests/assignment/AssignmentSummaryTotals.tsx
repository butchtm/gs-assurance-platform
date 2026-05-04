import { Box, Typography } from "@mui/material";

import { AssignmentSummaryTotalCard } from "./AssignmentSummaryTotalCard";

interface Item {
  description: string;
  value: number;
}

export const AssignmentSummaryTotals = ({ items }: { items: Item[] }) => {
  if (!items) {
    return <></>;
  }

  const toKey = (description: string) => {
    return `summary-totals-${description.replace(/\s+/g, "-").toLowerCase()}`;
  };

  return (
    <Box sx={{ flexDirection: "column", flex: 1, minWidth: 0 }} data-testid="summary-totals">
      <Typography sx={{ textAlign: "start" }}>Totals</Typography>
      <Box sx={{ display: "flex", flexWrap: "wrap", gap: "8px", justifyContent: "center" }}>
        {items.map((item) => (
          <AssignmentSummaryTotalCard
            key={toKey(item.description)}
            description={item.description}
            value={item.value || 0}
          />
        ))}
      </Box>
    </Box>
  );
};
