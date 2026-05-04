import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import { Box } from "@mui/material";

export const HighRiskPill = () => {
  return (
    <Box
      sx={{
        backgroundColor: "var(--color-risk-high-bg)",
        color: "var(--color-text-black)",
        borderRadius: "5px",
        display: "flex",
        alignItems: "center",
        gap: "4px",
        fontSize: "11px",
        padding: "2px 4px",
        whiteSpace: "nowrap",
      }}
      aria-live="polite"
      aria-label="High risk project"
    >
      <WarningAmberIcon sx={{ fontSize: 14, color: "var(--color-text-dark-gray)" }} />
      High Risk
    </Box>
  );
};
