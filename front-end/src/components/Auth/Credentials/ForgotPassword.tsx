import { Box, Button, Container, TextField, Typography } from "@mui/material";
import { useEffect, useRef, useState } from "react";
import PasswordChecklist from "react-password-checklist";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import AuthenticationRepository from "../../APIClient/AuthenticationRepository";

import ValidatedTextField from "./ValidatedTextField";

const ForgotPassword = () => {
  const [formErrorSummary, setFormErrorSummary] = useState<string | null>(null);
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState<string | null>(null);
  const emailInputRef = useRef<HTMLInputElement>(null);
  const [verificationCode, setVerificationCode] = useState("");
  const [verificationCodeError, setVerificationCodeError] = useState<string | null>(null);
  const [verificationCodeTouched, setVerificationCodeTouched] = useState(false);
  const [emailTouched, setEmailTouched] = useState(false);
  const [newPasswordTouched, setNewPasswordTouched] = useState(false);
  const [confirmNewPasswordTouched, setConfirmNewPasswordTouched] = useState(false);
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [newPasswordError, setNewPasswordError] = useState<string | null>(null);
  const [confirmNewPasswordError, setConfirmNewPasswordError] = useState<string | null>(null);
  const [stage, setStage] = useState("emailInput");
  const [infoMessage, setInfoMessage] = useState<string | null>(null);
  const [isNewPasswordValid, setIsNewPasswordValid] = useState(false);
  const navigate = useNavigate();

  const calculateFormErrors = (): string[] => {
    const errors: string[] = [];

    if (stage !== "codeInput") return errors;

    if (verificationCodeTouched && (!verificationCode || verificationCodeError)) {
      errors.push("Verification code required");
    }

    if (newPasswordTouched && (!newPassword || newPasswordError)) {
      errors.push("New password required");
    }

    if (confirmNewPasswordTouched && (!confirmNewPassword || confirmNewPasswordError)) {
      errors.push("Passwords do not match");
    }

    return errors;
  };

  useEffect(() => {
    const errors = calculateFormErrors();
    if (errors.length > 0) {
      setFormErrorSummary(`${errors.length} error${errors.length > 1 ? "s" : ""} found: ${errors.join(", ")}`);
    } else {
      setFormErrorSummary(null);
    }
  }, [
    stage,
    verificationCode,
    verificationCodeError,
    verificationCodeTouched,
    newPassword,
    isNewPasswordValid,
    newPassword,
    newPasswordError,
    newPasswordTouched,
    confirmNewPassword,
    confirmNewPasswordError,
    confirmNewPasswordTouched,
  ]);

  const validateEmail = (value: string): string | null => {
    if (!value) return "Email is required";

    if (value.length > 254) return "Enter a valid email address";

    const atIndex = value.indexOf("@");
    if (atIndex === -1 || atIndex === 0 || atIndex === value.length - 1) {
      return "Enter a valid email address";
    }

    const localPart = value.substring(0, atIndex);
    const domainPart = value.substring(atIndex + 1);

    if (
      localPart.length === 0 ||
      localPart.length > 64 ||
      domainPart.length === 0 ||
      domainPart.length > 253 ||
      value.includes("..") ||
      value.includes(" ") ||
      !domainPart.includes(".") ||
      domainPart.startsWith(".") ||
      domainPart.endsWith(".")
    ) {
      return "Enter a valid email address";
    }

    return null;
  };

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setEmail(value);
    setEmailError(validateEmail(value));
  };

  const handleEmailBlur = () => {
    setEmailTouched(true);
    setEmailError(validateEmail(email));
  };

  const handleVerificationCodeChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const value = e.target.value;
    setVerificationCode(value);
    updateVerificationCodeError(value);
  };

  const handleVerificationCodeBlur = () => {
    setVerificationCodeTouched(true);
    updateVerificationCodeError(verificationCode);
  };

  const updateVerificationCodeError = (value: string) => {
    const isValid = /^\d{6}$/.test(value);
    setVerificationCodeError(isValid ? null : "Enter the 6-digit verification code");
  };

  const handleNewPasswordChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const value = e.target.value;
    setNewPassword(value);
    updateNewPasswordError(value);
  };

  const handleNewPasswordBlur = () => {
    setNewPasswordTouched(true);
    updateNewPasswordError(newPassword);
  };

  const updateNewPasswordError = (value: string) => {
    setNewPasswordError(value ? null : "Password is required");
  };

  const handleConfirmNewPasswordChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const value = e.target.value;
    setConfirmNewPassword(value);
    updateConfirmNewPasswordError(value);
  };

  const handleConfirmNewPasswordBlur = () => {
    setConfirmNewPasswordTouched(true);
    updateConfirmNewPasswordError(confirmNewPassword);
  };

  const updateConfirmNewPasswordError = (value: string) => {
    const passwordsMatch = value && value !== newPassword;
    setConfirmNewPasswordError(passwordsMatch ? null : "Passwords do not match");
  };

  const handleForgotPassword = async () => {
    const error = validateEmail(email);
    setEmailError(error);
    if (error) {
      emailInputRef.current?.focus();
      return;
    }

    try {
      await AuthenticationRepository.forgotPassword(email);
      setInfoMessage(`A password recovery email has been sent to ${email} if an email address exists in the system.`);
      setStage("codeInput");
    } catch (error) {
      console.error("Failed to send verification code:", error);
      setInfoMessage(`A password recovery email has been sent to ${email} if an email address exists in the system.`);
      setStage("codeInput");
    }
  };

  const handleConfirmForgotPassword = async () => {
    setVerificationCodeTouched(true);
    setConfirmNewPasswordTouched(true);

    const errors = calculateFormErrors();

    const allErrors: string[] = [...errors];

    if (!verificationCode && !allErrors.includes("Verification code required")) {
      allErrors.push("Verification code required");
    }

    if (!newPassword) {
      allErrors.push("Password is required");
    }

    if (!confirmNewPassword && !allErrors.includes("Passwords do not match")) {
      allErrors.push("Password confirmation is required");
    }

    if (allErrors.length > 0) {
      setFormErrorSummary(`${allErrors.length} error${allErrors.length > 1 ? "s" : ""} found: ${allErrors.join(", ")}`);
      return;
    }

    try {
      await AuthenticationRepository.confirmForgotPassword(email, newPassword, verificationCode);
      toast.success("Password reset successfully.");
      navigate("/login");
    } catch (error) {
      console.error("An error occurred while sending the verification code: ", error);
      setVerificationCodeError("Verification code is incorrect");
      toast.error("Failed to reset password.");
    }
  };

  const passwordValidationChangeHandler = (isValid: boolean): void => {
    setIsNewPasswordValid(isValid);
    if (!confirmNewPassword) {
      setConfirmNewPasswordError("Passwords do not match");
    } else if (newPassword !== confirmNewPassword) {
      setConfirmNewPasswordError("Passwords do not match");
    } else {
      setConfirmNewPasswordError(null);
    }
  };

  return (
    <Container maxWidth="xs">
      <Box className={"fullscreen-form-container"}>
        {stage === "emailInput" ? (
          <>
            <Typography variant="h5">Forgot Password</Typography>
            <TextField
              label="Email"
              value={email}
              onChange={handleEmailChange}
              onBlur={handleEmailBlur}
              variant="outlined"
              margin="normal"
              fullWidth
              inputRef={emailInputRef}
              error={emailTouched && !!emailError}
              aria-invalid={emailTouched && !!emailError}
              aria-describedby={emailTouched && emailError ? "email-error-text" : undefined}
              data-testid="forgot-password-email-input"
            />

            {emailTouched && emailError && (
              <Typography id="email-error-text" color="error" variant="body2" role="alert" sx={{ mt: -1, mb: 1 }}>
                {emailError}
              </Typography>
            )}

            <Button
              variant="contained"
              color="primary"
              onClick={handleForgotPassword}
              disabled={!!validateEmail(email)}
              aria-disabled={!!validateEmail(email)}
              sx={validateEmail(email) ? { opacity: 0.5, pointerEvents: "none" } : {}}
            >
              Submit
            </Button>
          </>
        ) : (
          <>
            <Typography variant="h5">Enter Verification Code</Typography>
            {infoMessage && (
              <Typography
                id="info-message-text"
                variant="body2"
                role="output"
                sx={{
                  mt: 1,
                  mb: 1,
                  fontSize: "14px",
                  fontWeight: "bold",
                  color: "#000000",
                }}
              >
                {infoMessage}
              </Typography>
            )}
            <TextField
              label="Verification Code"
              name="verification-code"
              value={verificationCode}
              onChange={handleVerificationCodeChange}
              onBlur={handleVerificationCodeBlur}
              variant="outlined"
              margin="normal"
              fullWidth
              autoComplete="one-time-code"
              inputProps={{
                inputMode: "numeric",
                pattern: "[0-9]*",
                autoComplete: "one-time-code",
              }}
              error={verificationCodeTouched && !!verificationCodeError}
              aria-invalid={verificationCodeTouched && !!verificationCodeError}
              aria-describedby={
                verificationCodeTouched && verificationCodeError ? "verification-code-error-text" : undefined
              }
              data-testid="verification-code-input"
            />

            {verificationCodeError && (
              <Typography
                id="verification-code-error-text"
                color="error"
                variant="body2"
                role="alert"
                sx={{ mt: -1, mb: 1 }}
              >
                {verificationCodeError}
              </Typography>
            )}
            <ValidatedTextField
              label="New Password"
              type="password"
              autoComplete="new-password"
              value={newPassword}
              onChange={handleNewPasswordChange}
              onBlur={handleNewPasswordBlur}
              error={newPasswordTouched && !!newPasswordError}
              errorText={newPasswordError}
              testId="new-password-input"
            />
            <ValidatedTextField
              label="Confirm New Password"
              type="password"
              autoComplete="new-password"
              value={confirmNewPassword}
              onChange={handleConfirmNewPasswordChange}
              onBlur={handleConfirmNewPasswordBlur}
              error={confirmNewPasswordTouched && !!confirmNewPasswordError}
              errorText={confirmNewPasswordError}
              testId="confirm-new-password-input"
            />
            <PasswordChecklist
              rules={["minLength", "specialChar", "number", "capital", "lowercase", "match"]}
              minLength={8}
              value={newPassword}
              valueAgain={confirmNewPassword}
              onChange={passwordValidationChangeHandler}
            />
            {formErrorSummary && (
              <Typography color="error" variant="body2" role="alert" sx={{ mt: 1, mb: 1 }}>
                {formErrorSummary}
              </Typography>
            )}
            {(() => {
              const isSubmitDisabled =
                !verificationCode ||
                !!verificationCodeError ||
                !newPassword ||
                !confirmNewPassword ||
                !!confirmNewPasswordError ||
                !isNewPasswordValid;
              return (
                <Button
                  variant="contained"
                  color="primary"
                  onClick={handleConfirmForgotPassword}
                  disabled={isSubmitDisabled}
                  aria-disabled={isSubmitDisabled}
                  sx={isSubmitDisabled ? { opacity: 0.5, pointerEvents: "none" } : {}}
                >
                  Submit
                </Button>
              );
            })()}
          </>
        )}
      </Box>
    </Container>
  );
};

export default ForgotPassword;
