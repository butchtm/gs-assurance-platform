import EditIcon from "@mui/icons-material/Edit";
import { useContext } from "react";

import { AuthContext } from "../../Auth/Login/AuthProvider";
import InformationCard from "../../Reusable/Card/InformationCard";
import { PlatformAccount } from "../PlatformAccountTypes";

import { AccountChip } from "./AccountChip";

export const AccountUsersCard = ({
  handleOpenEditModal,
  account,
}: {
  handleOpenEditModal: () => void;
  account?: PlatformAccount;
}) => {
  const authContext = useContext(AuthContext);
  return (
    <InformationCard
      title="Account Users"
      action={
        authContext.isAdminUser()
          ? { ariaLabel: "Edit account users", icon: <EditIcon />, onClick: handleOpenEditModal }
          : undefined
      }
    >
      {account?.users.map((user, index) => (
        <AccountChip key={user.id || index} label={`${user.userName} - ${user.emailAddress}`} />
      ))}
    </InformationCard>
  );
};
