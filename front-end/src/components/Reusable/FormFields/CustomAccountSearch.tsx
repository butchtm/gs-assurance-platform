import { Autocomplete, CircularProgress, FormHelperText, TextField } from "@mui/material";
import { useField } from "formik";
import React, { useEffect, useState } from "react";

import { Options } from "../../../common/data-types/DataReferenceTypes";
import { getAccounts } from "../../PlatformAccounts/PlatformAccount";

interface CustomAccountSearchProps {
  name: string;
  label: string;
  onChange?: (account: Options | null) => void;
  accountTypeId: number;
  required?: boolean;
  disabled?: boolean;
}

export const CustomAccountSearchAutoComplete: React.FC<CustomAccountSearchProps> = ({
  name,
  label,
  onChange,
  accountTypeId,
  required,
  disabled,
}) => {
  const [field, meta, helpers] = useField(name);
  const [accountSearch, setAccountSearch] = useState("");
  const [accountOptions, setAccountOptions] = useState<Options[]>([]); // Use PlatformAccount directly
  const [loading, setLoading] = useState(true);
  const [selectedAccount, setSelectedAccount] = useState<Options | null>(null);

  useEffect(() => {
    setAccountOptions([]);
    const fetchAccounts = async () => {
      setLoading(true);
      const filters = {
        name: accountSearch,
        accountTypeIds: [accountTypeId],
        active: true,
      };
      try {
        const response = await getAccounts(filters);
        if (field.value) {
          const initialAccount = response.content.find((acc) => acc.id === field.value);
          if (initialAccount) {
            setSelectedAccount({ value: initialAccount?.id, label: initialAccount?.name });
          }
        }

        const optionsWithCount: { [label: string]: number } = {};

        const simplifiedOptions: Options[] = response.content.map((account) => {
          const accountName = account.name;
          const value = account.id;

          optionsWithCount[accountName] = (optionsWithCount[accountName] || 0) + 1;

          return { value, label: accountName };
        });

        const uniqueOptionLabels: Options[] = simplifiedOptions.map((option) => {
          if (optionsWithCount[option.label] > 1) {
            return { ...option, label: `${option.label} (${option.value})` }; // Ensuring uniqueness
          }
          return option;
        });

        setAccountOptions(uniqueOptionLabels);
      } catch (error) {
        console.error("Error fetching accounts:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchAccounts();
  }, [accountSearch, field.value]);

  const handleOnChange = (event: React.SyntheticEvent<Element, Event>, newValue: Options | null) => {
    setSelectedAccount(newValue);
    helpers.setValue(newValue ? newValue.value : null);
    if (onChange) {
      onChange(newValue || null);
    }
  };

  return (
    <>
      <Autocomplete
        options={accountOptions}
        loading={loading}
        filterOptions={(options) => options}
        onOpen={() => setAccountSearch("")}
        openOnFocus
        onInputChange={(event, newInputValue) => {
          setAccountSearch(newInputValue);
        }}
        value={selectedAccount}
        onChange={handleOnChange}
        disabled={disabled}
        getOptionLabel={(option) => option.label}
        isOptionEqualToValue={(option, value) => option.value === value.value}
        aria-required={!!required}
        renderInput={(params) => (
          <TextField
            {...params}
            label={
              required ? (
                <>
                  {label}
                  <span aria-hidden="true">*</span>
                </>
              ) : (
                label
              )
            }
            error={meta.touched && Boolean(meta.error)}
            InputProps={{
              ...params.InputProps,
              endAdornment: (
                <React.Fragment>
                  {loading ? <CircularProgress color="inherit" size={20} /> : null}
                  {params.InputProps.endAdornment}
                </React.Fragment>
              ),
            }}
            inputProps={{
              ...params.inputProps,
              "data-testid": "account-search-input",
            }}
          />
        )}
      />
      {meta.touched && Boolean(meta.error) && (
        <FormHelperText error role="alert">
          {meta.error}
        </FormHelperText>
      )}
    </>
  );
};
