import { MRT_Cell, MRT_RowData } from "material-react-table";
import { Link } from "react-router-dom";
import { useContext } from "react";

import { PlatformAccount } from "../../../components/PlatformAccounts/PlatformAccountTypes";
import { AuthContext } from "../../../components/Auth/Login/AuthProvider";

export const PlatformAccountCell = <TData extends MRT_RowData>({ cell }: { cell: MRT_Cell<TData, unknown> }) => {
  const account = cell.getValue<PlatformAccount>();
  const authContext = useContext(AuthContext);
  const isArmOrAdmin = authContext.isAdminUser() || authContext.isArmUser();
  if (!account) return <></>;
  if (!account.id || !isArmOrAdmin) return account.name;

  return <Link to={`/account/${account.id}`}>{account.name}</Link>;
};
