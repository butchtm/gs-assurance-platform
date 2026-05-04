import OpenInNewIcon from "@mui/icons-material/OpenInNew";
import { Autocomplete, Box, FormControl, IconButton, TextField } from "@mui/material";
import { useState } from "react";

import { ReferenceType, useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { PermissionGroup } from "../../../PermissionGroup";
import ProtectedComponent from "../../../ProtectedComponent";
import { PlatformAccount } from "../../PlatformAccounts/PlatformAccountTypes";

import { AssignmentStatus } from "./Assignment";

export const AccountAssignmentRow = ({
  label,
  assignmentTypeId,
  accountOptions,
  onAccountAssignmentChanged,
  onAccountAssignmentStatusChanged,
  defaultAccount,
  defaultStatus,
  required,
}: {
  label: string;
  assignmentTypeId: number;
  accountOptions: PlatformAccount[];
  onAccountAssignmentChanged: (newAccount: PlatformAccount | null, newStatus: ReferenceType | null) => void;
  onAccountAssignmentStatusChanged: (newStatus: ReferenceType | null) => void;
  defaultAccount?: PlatformAccount;
  defaultStatus?: AssignmentStatus;
  required?: boolean;
}) => {
  const [account, setAccount] = useState(defaultAccount || null);
  const [status, setStatus] = useState(defaultStatus || null);
  const { activeReferenceTypes } = useReferenceTypes();

  const handleAccountChange = (newAccount: PlatformAccount | null) => {
    const pendingStatus = activeReferenceTypes.AssignmentStatus.find((s) => s.code === "PENDING") as ReferenceType;
    const assignedStatus = activeReferenceTypes.AssignmentStatus.find((s) => s.code === "ACCEPTED") as ReferenceType;

    const newStatus = assignmentTypeId === 3 ? assignedStatus : pendingStatus;
    onAccountAssignmentChanged(newAccount, newStatus);
    setAccount(newAccount);
    setStatus(newStatus);
  };

  return (
    <Box sx={{ display: "flex", alignItems: "center", marginBottom: 2 }}>
      <FormControl sx={{ flex: 1, paddingRight: 0.5 }}>
        <Autocomplete
          id={"acc-assig-type-" + assignmentTypeId}
          options={accountOptions}
          getOptionLabel={(option) => option.name}
          isOptionEqualToValue={(option, value) => option.id == value.id}
          value={account}
          renderInput={(params) => (
            <TextField
              {...params}
              label={label}
              variant="outlined"
              required={required}
              data-testid={"acc-assignment-type-select-" + assignmentTypeId}
            />
          )}
          onChange={(_, newAccount) => handleAccountChange(newAccount)}
        />
      </FormControl>

      <ProtectedComponent roles={[PermissionGroup.ARM_TEAM]}>
        <IconButton
          sx={{
            marginRight: 2.5,
            visibility: account ? "visible" : "hidden",
          }}
          title="Open Account in New Tab"
          onClick={() => window.open(`/account/${account?.id}`, "_blank")}
          data-testid={"open-account-in-new-tab-" + assignmentTypeId}
        >
          <OpenInNewIcon />
        </IconButton>
      </ProtectedComponent>

      <FormControl sx={{ flex: 1 }}>
        <Autocomplete
          id={"assig-status-assig-type-" + assignmentTypeId}
          options={activeReferenceTypes.AssignmentStatus as AssignmentStatus[]}
          getOptionLabel={(option) => option.description}
          isOptionEqualToValue={(option, value) => option.id == value.id}
          value={status}
          renderInput={(params) => (
            <TextField
              {...params}
              label={label}
              variant="outlined"
              required={required || account != null}
              data-testid={"assig-status-assig-type-" + assignmentTypeId}
            />
          )}
          onChange={(_, newStatus) => {
            onAccountAssignmentStatusChanged(newStatus);
            setStatus(newStatus);
          }}
        />
      </FormControl>
    </Box>
  );
};
