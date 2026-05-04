import dayjs from "dayjs";
import { DropdownOption, MRT_Cell, MRT_RowData, MRT_SortingState } from "material-react-table";

import { ReferenceTypeContextType } from "../../../common/data-types/DataReferenceTypes";
import { OpenRequestAssignmentsCell } from "../../../common/filterable-sortable-table/cell/OpenRequestAssignmentsCell";
import { RequestNameLinkCell } from "../../../common/filterable-sortable-table/cell/RequestNameLinkCell";

export const buildDropdownOptionsWithTypes = (
  referenceTypes: ReferenceTypeContextType,
  typeName: string,
): DropdownOption[] => {
  return referenceTypes.activeReferenceTypes[typeName]
    .filter((type) => type.active)
    .map((type) => ({
      value: type.id,
      label: type.description,
    }));
};

export const buildInvoiceStatusDropdownOptions = (referenceTypes: ReferenceTypeContextType): DropdownOption[] => {
  const options = buildDropdownOptionsWithTypes(referenceTypes, "InvoiceStatus");
  return [...options, { label: "No Invoice", value: -1 }];
};

export const formatDate = (date: string | undefined) => {
  if (date) {
    return dayjs.utc(date).format("DD/MM/YYYY");
  }
  return "";
};

export const convertMrtSortToSearchParam = (sortingState: MRT_SortingState) => {
  if (sortingState.length < 1) {
    return "";
  }
  const direction = sortingState[0].desc ? "desc" : "asc";
  return `${sortingState[0].id},${direction}`;
};

export function createRequestNameLinkCell<TData extends MRT_RowData>(
  getGsid: (rowData: TData) => string,
  getRequestId: (rowData: TData) => number,
) {
  return function RequestNameLinkRenderer({
    cell,
    row,
  }: {
    cell: MRT_Cell<TData, unknown>;
    row: MRT_Cell<TData, unknown>["row"];
  }) {
    return <RequestNameLinkCell cell={cell} row={row} getGsid={getGsid} getRequestId={getRequestId} />;
  };
}

export function createOpenRequestAssignmentsCell<TData extends MRT_RowData>(handleOpenModal: (rowData: TData) => void) {
  return function OpenRequestAssignmentsRenderer({ row }: { row: MRT_Cell<TData, unknown>["row"] }) {
    return <OpenRequestAssignmentsCell row={row} handleOpenModal={handleOpenModal} />;
  };
}
