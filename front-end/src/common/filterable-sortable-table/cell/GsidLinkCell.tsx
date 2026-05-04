import { MRT_Cell, MRT_RowData } from "material-react-table";
import { Link } from "react-router-dom";

export const GsidLinkCell = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const renderedCellValue = cell.getValue<string>();

  return (
    <Link target="_blank" to={`/projects/${renderedCellValue}`}>
      {renderedCellValue}
    </Link>
  );
};
