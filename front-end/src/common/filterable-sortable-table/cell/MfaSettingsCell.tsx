import SettingsIcon from "@mui/icons-material/Settings";
import { IconButton } from "@mui/material";
import { MRT_Cell, MRT_RowData } from "material-react-table";
import { useState } from "react";

import UserMultiFactorSettingsModal from "../../../components/Auth/Settings/UserMultiFactorSettingsModal";

export const MfaSettingsCell = <TData extends MRT_RowData & { userName: string }>({
  cell,
}: {
  cell: MRT_Cell<TData, unknown>;
}): JSX.Element => {
  const [open, setOpen] = useState(false);
  const username = cell.row.original.userName;

  return (
    <>
      <IconButton onClick={() => setOpen(true)} size="small" title="MFA Settings">
        <SettingsIcon />
      </IconButton>

      <UserMultiFactorSettingsModal
        isOpen={open}
        onClose={() => setOpen(false)}
        isAdminMode={true}
        username={username}
      />
    </>
  );
};
