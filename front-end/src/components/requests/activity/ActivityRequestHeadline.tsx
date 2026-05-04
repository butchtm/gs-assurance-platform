import { Box, useTheme } from "@mui/material";
import dayjs from "dayjs";
import React from "react";

import { getDaysRemainingFromTodayString } from "../../../common/DaysDifference";

export const ActivityRequestHeadline: React.FC<{ date: string; activityName: string }> = ({ date, activityName }) => {
  const theme = useTheme();

  const getDeadlineStyling = (date: string) => {
    const difference = dayjs.utc(date).startOf("day").businessDiff(dayjs.utc().startOf("day"));
    if (difference == 0 || difference == 1) {
      return theme.palette.warning.light;
    }
    if (difference < 0) {
      return theme.palette.error.light;
    }
    return "lightgray";
  };
  return (
    <Box
      sx={{
        margin: "0.5rem",
        borderRadius: "5px",
        backgroundColor: getDeadlineStyling(date),
        padding: "0 10px",
      }}
    >
      <span>
        {activityName} {getDaysRemainingFromTodayString(date)}
      </span>
    </Box>
  );
};
