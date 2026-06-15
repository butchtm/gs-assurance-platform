import { Box } from "@mui/material";
import { MRT_Cell, MRT_RowData } from "material-react-table";

export const IsActiveCell = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const isActive = cell.getValue<boolean>();
  return (
    <Box
      component="span"
      sx={{
        backgroundColor: (theme) => (isActive ? theme.palette.success.light : theme.palette.warning.light),
        borderRadius: "0.3rem",
        color: "var(--color-text-dark-gray)",
        maxWidth: "9ch",
        p: "0.25rem 0.4rem",
        textTransform: "uppercase",
      }}
    >
      {isActive ? "Active" : "Inactive"}
    </Box>
  );
};
