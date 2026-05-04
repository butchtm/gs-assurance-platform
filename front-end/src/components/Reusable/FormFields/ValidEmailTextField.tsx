import { TextField, TextFieldProps } from "@mui/material";
import { useState } from "react";

export const ValidEmailTextField = ({
  onEmailAddressChange,
  ...textFieldProps
}: {
  onEmailAddressChange: (emailAddress: string, isValid: boolean) => void;
} & TextFieldProps) => {
  const [isValid, setIsValid] = useState(true);

  const isEmailAddressValid = (emailAddress: string) => {
    const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,254}$/;
    return regex.test(emailAddress);
  };

  return (
    <TextField
      type="email"
      onChange={(e) => {
        const emailAddress = e.target.value;
        const isChangeValid = isEmailAddressValid(emailAddress);
        setIsValid(isChangeValid);
        onEmailAddressChange(emailAddress, isChangeValid);
      }}
      error={!isValid}
      helperText={!isValid ? "Invalid email address" : ""}
      {...textFieldProps}
      data-testid="valid-email-text-field"
    />
  );
};
