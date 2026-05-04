import { Box, Button, TextField, Typography, useTheme } from "@mui/material";
import { FormEvent, useRef, useState } from "react";
import { Link } from "react-router-dom";
import "./LoginForm.css";

const LoginForm = ({
  isLoginLoading,
  submitLogin,
  loginError,
  loginErrorTitle,
}: {
  isLoginLoading: boolean;
  submitLogin: (username: string, password: string) => void;
  loginError?: string;
  loginErrorTitle: string;
}) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [usernameError, setUsernameError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [hasBeenSubmitted, setHasBeenSubmitted] = useState(false);

  const usernameRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);

  const validateUsername = (value: string): string => {
    if (!value.trim()) {
      return "Username is required";
    }
    return "";
  };

  const validatePassword = (value: string): string => {
    if (!value.trim()) {
      return "Password is required";
    }
    return "";
  };

  const isFormValid = username.trim() !== "" && password.trim() !== "" && !usernameError && !passwordError;

  const handleUsernameBlur = () => {
    const error = validateUsername(username);
    setUsernameError(error);
  };

  const handlePasswordBlur = () => {
    const error = validatePassword(password);
    setPasswordError(error);
  };

  const getErrorSummary = (): string[] => {
    const errors: string[] = [];
    if (usernameError) errors.push(usernameError);
    if (passwordError) errors.push(passwordError);
    if (loginError) errors.push(loginError);
    return errors;
  };

  const errorSummary = getErrorSummary();
  const hasErrors = errorSummary.length > 0;

  const formSubmitEvent = (event: FormEvent) => {
    event.preventDefault();
    setHasBeenSubmitted(true);

    const usernameValidationError = validateUsername(username);
    const passwordValidationError = validatePassword(password);

    setUsernameError(usernameValidationError);
    setPasswordError(passwordValidationError);

    let hasErrors = false;
    let firstErrorField: HTMLInputElement | null = null;

    if (usernameValidationError) {
      hasErrors = true;
      firstErrorField = usernameRef.current;
    }

    if (passwordValidationError) {
      hasErrors = true;
      if (!firstErrorField) {
        firstErrorField = passwordRef.current;
      }
    }

    if (firstErrorField) {
      firstErrorField.focus();
    }

    if (!hasErrors) {
      submitLogin(username, password);
    }
  };

  const theme = useTheme();

  return (
    <form onSubmit={formSubmitEvent}>
      <Typography variant="h5" gutterBottom>
        Login
      </Typography>

      {(hasErrors && hasBeenSubmitted) || loginError ? (
        <Box
          role="alert"
          aria-live="assertive"
          sx={{
            mb: 2,
            p: 2,
            backgroundColor: "#ffffff",
            border: `2px solid ${theme.palette.error.main}`,
            borderRadius: 1,
          }}
        >
          <Typography variant="body2" sx={{ fontWeight: "bold", mb: 1, color: theme.palette.error.main }}>
            {loginErrorTitle}
          </Typography>
          <ul style={{ margin: "0", paddingLeft: "16px" }}>
            {errorSummary.map((error) => (
              <li key={error}>
                <Typography variant="body2" sx={{ color: theme.palette.error.main }}>
                  {error}
                </Typography>
              </li>
            ))}
          </ul>
        </Box>
      ) : null}

      <TextField
        label="Username"
        type="text"
        value={username}
        onChange={(event) => {
          setUsername(event.target.value);
          if (usernameError) setUsernameError("");
        }}
        onBlur={handleUsernameBlur}
        margin="normal"
        fullWidth
        autoComplete="username"
        data-testid="login-username-input"
        error={!!usernameError}
        helperText={usernameError}
        inputRef={usernameRef}
        aria-invalid={!!usernameError}
        InputProps={{
          "aria-describedby": usernameError ? "username-error" : undefined,
        }}
        FormHelperTextProps={{
          role: usernameError ? "alert" : undefined,
          id: usernameError ? "username-error" : undefined,
        }}
      />
      <TextField
        label="Password"
        type="password"
        value={password}
        onChange={(event) => {
          setPassword(event.target.value);
          if (passwordError) setPasswordError("");
        }}
        onBlur={handlePasswordBlur}
        margin="normal"
        fullWidth
        autoComplete="current-password"
        data-testid="login-password-input"
        error={!!passwordError}
        helperText={passwordError}
        inputRef={passwordRef}
        aria-invalid={!!passwordError}
        InputProps={{
          "aria-describedby": passwordError ? "password-error" : undefined,
        }}
        FormHelperTextProps={{
          role: passwordError ? "alert" : undefined,
          id: passwordError ? "password-error" : undefined,
        }}
      />
      <Button
        type="submit"
        disabled={isLoginLoading || !isFormValid}
        variant="contained"
        color="primary"
        aria-disabled={isLoginLoading || !isFormValid}
        sx={{
          ...(isLoginLoading || !isFormValid
            ? {
                backgroundColor: "var(--color-button-disabled-bg)",
                color: "var(--color-button-disabled-text)",
                cursor: "not-allowed",
                "&:hover": {
                  backgroundColor: "var(--color-button-disabled-bg)",
                },
              }
            : {}),
        }}
      >
        {isLoginLoading ? "Logging in..." : "Login"}
      </Button>
      <Box sx={{ textAlign: "right" }}>
        <Link to="/forgot-password" className="no-decoration-link">
          Forgotten password?
        </Link>
      </Box>
    </form>
  );
};

export default LoginForm;
