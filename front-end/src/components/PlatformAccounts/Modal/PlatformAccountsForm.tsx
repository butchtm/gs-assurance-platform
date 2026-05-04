import { Autocomplete, Box, Chip, FormControl, FormControlLabel, Switch, TextField } from "@mui/material";
import React, { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { ValidEmailTextField } from "../../Reusable/FormFields/ValidEmailTextField";
import { User, getAllActiveUsers } from "../../User/User";
import { PlatformAccount } from "../PlatformAccountTypes";

interface PlatformAccountsFormProps {
  defaultAccountValues?: PlatformAccount;
  onChange: (updatedPlatformAccount: PlatformAccount) => void;
  onChangeSelectedUserIds: (selectedUserIds: number[]) => void;
  onValidationChange: (isPlatformAccountChangeValid: boolean) => void;
}

export const PlatformAccountsForm: React.FC<PlatformAccountsFormProps> = ({
  defaultAccountValues,
  onChange,
  onChangeSelectedUserIds,
  onValidationChange,
}) => {
  const [account, setAccount] = useState<PlatformAccount>({
    id: 0,
    name: "",
    active: true,
    emailAddress: "",
    address: "",
    accountType: {
      id: undefined,
      code: "",
      description: "",
    },
    users: [],
  });
  const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);
  const [allActiveUsers, setAllActiveUsers] = useState<User[]>([]);
  const [isEmailAddressValid, setIsEmailAddressValid] = useState(!!defaultAccountValues?.emailAddress);
  const { activeReferenceTypes } = useReferenceTypes();

  useEffect(() => {
    if (defaultAccountValues) {
      setAccount(defaultAccountValues);
      setSelectedUserIds(defaultAccountValues.users.map((user) => Number(user.id)));
    }

    const fetchUsersFromApi = async () => {
      try {
        setAllActiveUsers(await getAllActiveUsers());
      } catch (error) {
        console.error("Error fetching users from API:", error);
      }
    };
    fetchUsersFromApi();
  }, []);

  useEffect(() => {
    if (account) onChange(account);
    onValidationChange(isFormValid());
  }, [account, isEmailAddressValid]);

  useEffect(() => {
    onChangeSelectedUserIds(selectedUserIds);
  }, [selectedUserIds]);

  const isFormValid = (): boolean => {
    if (!isEmailAddressValid) return false;
    if (!account?.address) return false;
    return true;
  };

  const getAccountType = () => {
    const matchedAccountType = activeReferenceTypes?.AccountType.find(
      (accountType) => account && accountType.id == account?.accountType.id,
    );
    if (!matchedAccountType) return null;
    return matchedAccountType;
  };

  return (
    <form>
      <FormControl fullWidth margin="normal">
        <Autocomplete
          options={activeReferenceTypes?.AccountType || []}
          getOptionLabel={(option) => option.description}
          value={getAccountType()}
          onChange={(_, accountType) => {
            if (account) {
              setAccount({
                ...account,
                accountType: {
                  ...account.accountType,
                  id: Number(accountType?.id),
                },
              });
            }
          }}
          renderInput={(params) => (
            <TextField
              {...params}
              label={"Account Type"}
              variant="outlined"
              required={true}
              data-testid="account-type-auto-complete"
            />
          )}
        />
      </FormControl>

      <FormControlLabel
        control={
          <Switch
            checked={account?.active || false}
            onChange={() => {
              if (account) {
                setAccount({ ...account, active: !account.active });
              }
            }}
            name="active"
            color="primary"
          />
        }
        label="Active"
        data-testid="account-active-switch"
      />

      <TextField
        label="Account Name"
        value={account?.name || ""}
        onChange={(e) => {
          if (account) {
            setAccount({ ...account, name: e.target.value });
          }
        }}
        fullWidth
        margin="normal"
        data-testid="account-name-input"
      />

      <ValidEmailTextField
        onEmailAddressChange={(emailAddress, isValid) => {
          setAccount({ ...account, emailAddress: emailAddress });
          setIsEmailAddressValid(isValid);
        }}
        defaultValue={defaultAccountValues?.emailAddress}
        label="Main Contact Email"
        fullWidth
        margin="normal"
      />

      <TextField
        label="Main Contact Address"
        value={account?.address || ""}
        onChange={(e) => {
          if (account) {
            setAccount({ ...account, address: e.target.value });
          }
        }}
        fullWidth
        margin="normal"
        error={!account?.address}
        helperText={!account?.address && "You must provide an address"}
        data-testid="account-address-input"
      />

      <Autocomplete
        multiple
        id="assigned-users"
        options={allActiveUsers}
        value={allActiveUsers.filter((user) => selectedUserIds.includes(user.id))}
        getOptionLabel={(option) => `${option.userName} (${option.emailAddress || "No Email"})`}
        onChange={(_, values) => {
          setSelectedUserIds(values.map((value) => value.id));
        }}
        renderTags={(tagValue, getTagProps) => (
          <Box sx={{ maxHeight: 120, overflowY: "auto" }}>
            {tagValue.map((option, index) => {
              const { key, ...tagProps } = getTagProps({ index });
              return <Chip key={key} label={option.userName} {...tagProps} />;
            })}
          </Box>
        )}
        ListboxProps={{ style: { maxHeight: "200px", overflowY: "auto" } }}
        renderInput={(params) => (
          <TextField {...params} label="Assigned Users" margin="normal" data-testid="assigned-users-input" />
        )}
      />
    </form>
  );
};
