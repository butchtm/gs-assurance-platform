import { MRT_Cell, MRT_RowData } from "material-react-table";
import { Link } from "react-router-dom";

export const RequestNameLinkCell = <TData extends MRT_RowData>({
  cell,
  row,
  getGsid,
  getRequestId,
}: {
  cell: MRT_Cell<TData, unknown>;
  row: MRT_Cell<TData, unknown>["row"];
  getGsid: (rowData: TData) => string;
  getRequestId: (rowData: TData) => number;
}) => {
  const renderedCellValue = cell.getValue<string>();
  const rowData = row.original;

  if (!renderedCellValue) {
    return null;
  }

  const linkTarget = `/projects/${getGsid(rowData)}/requests?expand=${getRequestId(rowData)}`;
  return (
    <Link target="_blank" to={linkTarget}>
      {renderedCellValue}
    </Link>
  );
};
