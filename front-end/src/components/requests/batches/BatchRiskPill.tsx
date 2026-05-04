import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import { Box, Tooltip } from "@mui/material";

export const BatchRiskPill = () => {
  return (
    <Tooltip title="This project has been deemed high risk">
      <Box
        sx={{
          width: "fit-content",
          backgroundColor: "var(--color-risk-high-bg)",
          borderRadius: "5px",
          padding: "1px 6px",
          textWrap: "nowrap",
          alignItems: "center",
          display: "flex",
        }}
      >
        <WarningAmberIcon sx={{ fontSize: "20px", mr: "5px" }} />
        HIGH RISK
      </Box>
    </Tooltip>
  );
};
