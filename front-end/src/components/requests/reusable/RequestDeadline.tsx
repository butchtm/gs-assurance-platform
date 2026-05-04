import { Box, useTheme } from "@mui/material";
import dayjs from "dayjs";
import React from "react";

import { getDaysRemainingFromTodayString } from "../../../common/DaysDifference";

export const RequestDeadline: React.FC<{ date: string }> = ({ date }) => {
  const theme = useTheme();

  const getDeadlineStyling = (date: string) => {
    const difference = dayjs.utc(date).startOf("day").businessDiff(dayjs.utc().startOf("day"));
    if (difference == 0 || difference == 1) {
      return theme.palette.warning.light;
    }
    if (difference < 0) {
      return theme.palette.error.light;
    }
    return "var(--color-surface-deadline-default)";
  };

  return (
    <Box
      sx={{
        color: "var(--color-text-dark-gray)",
        borderRadius: "5px",
        width: "fit-content",
        fontSize: "13px !important",
        padding: "5px",
        textWrapMode: "nowrap",
        backgroundColor: getDeadlineStyling(date),
      }}
    >
      {`${dayjs.utc(date).format("DD/MM/YYYY")} (${getDaysRemainingFromTodayString(date, true)})`}
    </Box>
  );
};
