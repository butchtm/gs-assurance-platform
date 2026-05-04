import { MRT_Cell, MRT_RowData } from "material-react-table";
import { useContext } from "react";

import { getReferenceValueDescriptionById, ReferenceTypeContext } from "../../data-types/DataReferenceTypes";

export const PlatformAccountTypeCell = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const typeId = cell.getValue<number>();
  const { activeReferenceTypes } = useContext(ReferenceTypeContext);

  return <>{getReferenceValueDescriptionById(typeId, activeReferenceTypes.AccountType)}</>;
};
