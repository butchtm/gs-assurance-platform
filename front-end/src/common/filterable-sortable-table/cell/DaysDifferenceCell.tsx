import { Box, useTheme } from "@mui/material";
import dayjs from "dayjs";
import { MRT_Cell, MRT_RowData } from "material-react-table";

import { getDaysRemainingFromToday, getDaysRemainingFromTodayString } from "../../DaysDifference";

export const DaysDifferenceCell = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const renderedCellValue = cell.getValue<string>();

  if (!renderedCellValue) return <></>;

  const theme = useTheme();

  const getDeadlineStyling = (): string => {
    const difference = getDaysRemainingFromToday(renderedCellValue);
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
        backgroundColor: getDeadlineStyling(),
        color: "var(--color-text-dark-gray)",
        borderRadius: "5px",
        width: "fit-content",
        fontSize: "13px !important",
        padding: "5px",
        whiteSpace: "nowrap",
      }}
    >
      {getDaysRemainingFromTodayString(renderedCellValue, true)}&nbsp;(
      {dayjs.utc(renderedCellValue).format("DD/MM/YYYY")})
    </Box>
  );
};
