import { MRT_Cell, MRT_RowData } from "material-react-table";

import { UnreadMessagesPill } from "../../../components/requests/reusable/UnreadMessagesPill";

export const QaQcAccountUnread = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const row = cell.row.original;
  const unreadMessages = row.qaqcAccountUnreadMessages;
  const renderedCellValue = cell.getValue<string>();

  return (
    <>
      {renderedCellValue}
      {unreadMessages > 0 && <UnreadMessagesPill unreadMessages={row.qaqcAccountUnreadMessages} />}
    </>
  );
};
