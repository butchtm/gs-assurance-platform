import EditIcon from "@mui/icons-material/Edit";
import { Grid } from "@mui/material";
import { useContext } from "react";

import { AuthContext } from "../../Auth/Login/AuthProvider";
import InformationBlock from "../../Reusable/Card/InformationBlock";
import InformationCard from "../../Reusable/Card/InformationCard";
import { PlatformAccount } from "../PlatformAccountTypes";

const AccountDetailsCard = ({ account, onEditClick }: { account: PlatformAccount; onEditClick: () => void }) => {
  const authContext = useContext(AuthContext);
  return (
    <InformationCard
      title="Account Details"
      action={
        authContext.isAdminUser()
          ? { ariaLabel: "Edit account details", icon: <EditIcon />, onClick: onEditClick }
          : undefined
      }
    >
      <Grid container spacing={2}>
        <Grid item xs={3}>
          <InformationBlock title="Account Type">{account.accountType.description}</InformationBlock>
        </Grid>
        <Grid item xs={3}>
          <InformationBlock title="Active">{account.active ? "Active" : "Inactive"}</InformationBlock>
        </Grid>
        <Grid item xs={3}>
          <InformationBlock title="Account Name">{account.name}</InformationBlock>
        </Grid>
        <Grid item xs={3}>
          <InformationBlock title="Main Contact Email">{account.emailAddress}</InformationBlock>
        </Grid>
      </Grid>
    </InformationCard>
  );
};

export default AccountDetailsCard;
