import { Box, useTheme } from "@mui/material";

export const DeadlineDaysWarning = ({
  daysRemaining,
  isCompleted,
}: {
  daysRemaining: number;
  isCompleted: boolean;
}) => {
  const theme = useTheme();

  const getDeadlineStyling = (): string => {
    if (daysRemaining == 0 || daysRemaining == 1) {
      return theme.palette.warning.light;
    }
    if (daysRemaining < 0) {
      return theme.palette.error.light;
    }
    return "var(--color-surface-deadline-default)";
  };

  const getDaysString = (): string => {
    if (isCompleted) {
      if (daysRemaining == -1) {
        return `${Math.abs(daysRemaining)} day late`;
      }
      if (daysRemaining < 0) {
        return `${Math.abs(daysRemaining)} days late`;
      }
      return "On time";
    }

    if (daysRemaining == -1) {
      return `${Math.abs(daysRemaining)} day overdue`;
    }
    if (daysRemaining < -1) {
      return `${Math.abs(daysRemaining)} days overdue`;
    }
    if (daysRemaining == 0) {
      return "Due today";
    }

    if (daysRemaining == 1) {
      return `1 day remaining`;
    }
    return `${Math.abs(daysRemaining)} days remaining`;
  };

  return (
    <Box
      sx={{
        backgroundColor: getDeadlineStyling(),
        color: "var(--color-text-dark-gray)",
        borderRadius: "5px",
        width: "fit-content",
        fontSize: "13px !important",
        padding: "5px",
        whiteSpace: "nowrap",
      }}
    >
      {getDaysString()}
    </Box>
  );
};
