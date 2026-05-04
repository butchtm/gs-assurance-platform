import { MRT_Cell, MRT_RowData } from "material-react-table";

import { ReferenceType } from "../../../common/data-types/DataReferenceTypes";

export const MultilineReferenceTypesCell = <TData extends MRT_RowData>({
  cell,
}: {
  cell: MRT_Cell<TData, unknown>;
}) => {
  const referenceTypes = cell.getValue<ReferenceType[]>();

  return (
    <div>
      {referenceTypes.map((referenceType) => (
        <div key={referenceType.id}>{referenceType.description}</div>
      ))}
    </div>
  );
};
