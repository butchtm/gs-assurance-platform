import { Box, Card, CardContent, Typography } from "@mui/material";

interface AssignmentSummaryTotalCardProps {
  description: string;
  value: number;
}

export const AssignmentSummaryTotalCard = (props: AssignmentSummaryTotalCardProps) => {
  const toTestId = (description: string) => {
    return `summary-totals-${description.replace(/\s+/g, "-").toLowerCase()}`;
  };

  return (
    <Card sx={{ width: 80, height: 70, display: "flex", flexDirection: "column" }}>
      <CardContent
        sx={{ flexGrow: 1, display: "flex", flexDirection: "column", justifyContent: "space-between", padding: "8px" }}
      >
        <Box sx={{ flexGrow: 1, display: "flex", alignItems: "center" }}>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              textAlign: "center",
              width: "100%",
              fontSize: "0.75rem",
              whiteSpace: "nowrap",
              overflow: "hidden",
              textOverflow: "ellipsis",
            }}
          >
            {props.description}
          </Typography>
        </Box>
        <Typography variant="h6" data-testid={toTestId(props.description)}>
          {props.value}
        </Typography>
      </CardContent>
    </Card>
  );
};
