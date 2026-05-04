import OpenInNewIcon from "@mui/icons-material/OpenInNew";
import { IconButton } from "@mui/material";
import { MRT_Cell, MRT_RowData } from "material-react-table";

export const OpenRequestAssignmentsCell = <TData extends MRT_RowData>({
  row,
  handleOpenModal,
}: {
  row: MRT_Cell<TData, unknown>["row"];
  handleOpenModal: (rowData: TData) => void;
}) => {
  const rowData = row.original;

  return (
    <IconButton title="View Assignments" data-testid="open-assignments-button" onClick={() => handleOpenModal(rowData)}>
      <OpenInNewIcon />
    </IconButton>
  );
};
