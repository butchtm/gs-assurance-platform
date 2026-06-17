import { MRT_Cell, MRT_RowData } from "material-react-table";

import { Tag } from "@/common/tags";

export const PAACell = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const isPAA = cell.getValue<boolean>();

  if (isPAA) {
    return <Tag variant="success" label="PAA" data-testid="paa-tag" />;
  }

  return null;
};
