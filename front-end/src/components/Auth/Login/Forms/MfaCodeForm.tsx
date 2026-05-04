import { Box, Button, Link, TextField, Typography } from "@mui/material";
import { FormEvent, useRef, useState } from "react";

import MfaTypeSelectionForm from "./MfaTypeSelectionForm";

export enum MfaType {
  SOFTWARE_TOKEN_MFA = "SOFTWARE_TOKEN_MFA",
  EMAIL_OTP = "EMAIL_OTP",
  SELECT_MFA_TYPE = "SELECT_MFA_TYPE",
}

const MfaCodeForm = ({
  challengeType,
  username,
  isLoading,
  submitMfaCode,
  mfaError,
  onMfaTypeSelection,
  availableMfaMethods,
  resetLogin,
}: {
  challengeType: string;
  username: string;
  isLoading: boolean;
  submitMfaCode: (username: string, code: string) => void;
  mfaError?: string;
  onMfaTypeSelection: (selectedType: string) => void;
  availableMfaMethods: string[];
  resetLogin: () => void;
}) => {
  const [code, setCode] = useState("");
  const [codeError, setCodeError] = useState("");
  const [hasBeenSubmitted, setHasBeenSubmitted] = useState(false);
  const codeRef = useRef<HTMLInputElement>(null);

  const validateCode = (value: string): string => {
    if (!value.trim()) {
      return "Verification code is required";
    }
    if (value.length < 6) {
      return "Verification code must be at least 6 characters";
    }
    return "";
  };

  const isFormValid = code.trim() !== "" && !codeError;

  const handleCodeBlur = () => {
    const error = validateCode(code);
    setCodeError(error);
  };

  const getErrorSummary = (): string[] => {
    const errors: string[] = [];
    if (codeError) errors.push(codeError);
    if (mfaError) errors.push("Verification code is incorrect");
    return errors;
  };

  const errorSummary = getErrorSummary();
  const hasErrors = errorSummary.length > 0;

  const getTitle = (): string => {
    if (challengeType === MfaType.SOFTWARE_TOKEN_MFA) {
      return "Enter Authenticator Code";
    }
    return "Enter Email Verification Code";
  };

  const getDescription = (): string => {
    if (challengeType === MfaType.SOFTWARE_TOKEN_MFA) {
      return "Please enter the 6-digit code from your authenticator app.";
    }
    return "Please enter the verification code sent to your email address.";
  };

  const formSubmitEvent = (event: FormEvent) => {
    event.preventDefault();
    setHasBeenSubmitted(true);

    const codeValidationError = validateCode(code);
    setCodeError(codeValidationError);

    if (codeValidationError) {
      codeRef.current?.focus();
      return;
    }

    submitMfaCode(username, code);
  };

  const handleMfaTypeSelection = (selectedType: string) => {
    setCodeError("");
    onMfaTypeSelection(selectedType);
  };

  if (challengeType == MfaType.SELECT_MFA_TYPE) {
    return <MfaTypeSelectionForm onMfaTypeSelection={handleMfaTypeSelection} />;
  }

  return (
    <form onSubmit={formSubmitEvent}>
      <Typography variant="h5" gutterBottom>
        {getTitle()}
      </Typography>

      <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
        {getDescription()}
      </Typography>

      {(hasErrors && hasBeenSubmitted) || mfaError ? (
        <Box
          role="alert"
          aria-live="assertive"
          sx={{
            mb: 2,
            p: 2,
            backgroundColor: "#ffffff",
            border: (theme) => `2px solid ${theme.palette.error.main}`,
            borderRadius: 1,
          }}
        >
          <Typography variant="body2" sx={{ fontWeight: "bold", mb: 1, color: (theme) => theme.palette.error.main }}>
            Verification Failed
          </Typography>
          <ul style={{ margin: "0", paddingLeft: "16px" }}>
            {errorSummary.map((error) => (
              <li key={error}>
                <Typography variant="body2" sx={{ color: (theme) => theme.palette.error.main }}>
                  {error}
                </Typography>
              </li>
            ))}
          </ul>
        </Box>
      ) : null}

      <TextField
        label="Verification Code"
        type="text"
        value={code}
        onChange={(event) => {
          setCode(event.target.value);
          if (codeError) setCodeError("");
        }}
        onBlur={handleCodeBlur}
        margin="normal"
        fullWidth
        autoComplete="one-time-code"
        data-testid="mfa-code-input"
        error={!!codeError}
        helperText={codeError}
        inputRef={codeRef}
        aria-invalid={!!codeError}
        inputProps={{
          inputMode: "numeric",
          pattern: "[0-9]*",
          maxLength: 8,
        }}
        InputProps={{
          "aria-describedby": codeError ? "code-error" : undefined,
        }}
        FormHelperTextProps={{
          role: codeError ? "alert" : undefined,
          id: codeError ? "code-error" : undefined,
        }}
      />
      {availableMfaMethods.length > 1 && (
        <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
          Having trouble?&nbsp;
          <Link component="span" sx={{ textDecoration: "none", cursor: "pointer" }} onClick={resetLogin}>
            Repeat login process to sign in another way
          </Link>
        </Typography>
      )}

      <Button
        type="submit"
        disabled={isLoading || !isFormValid}
        variant="contained"
        color="primary"
        fullWidth
        sx={{ mt: 2 }}
        aria-disabled={isLoading || !isFormValid}
      >
        {isLoading ? "Verifying..." : "Verify Code"}
      </Button>
    </form>
  );
};

export default MfaCodeForm;
