import { Box } from "@mui/material";
import { MRT_Cell, MRT_RowData } from "material-react-table";

export const BoldTextCell = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const renderedCellValue = cell.getValue<string>();

  return (
    <Box
      component="span"
      sx={{
        color: "var(--color-text-bold)",
        fontWeight: "700",
      }}
    >
      {renderedCellValue}
    </Box>
  );
};
