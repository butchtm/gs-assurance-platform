import { Card, CardContent, Typography, Box } from "@mui/material";

interface StatCardProps {
  description: string;
  value: number;
}

export const StatCard = (props: StatCardProps) => {
  return (
    <Card sx={{ width: 170, m: 1, height: 160, display: "flex", flexDirection: "column" }}>
      <CardContent sx={{ flexGrow: 1, display: "flex", flexDirection: "column", justifyContent: "space-between" }}>
        <Box sx={{ flexGrow: 1, display: "flex", alignItems: "center" }}>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ wordWrap: "break-word", textAlign: "center", width: "100%" }}
          >
            {props.description}
          </Typography>
        </Box>
        <Typography variant="h4" sx={{ mt: 1 }}>
          {props.value}
        </Typography>
      </CardContent>
    </Card>
  );
};
