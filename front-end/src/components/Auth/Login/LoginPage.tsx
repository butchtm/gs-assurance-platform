import { Box } from "@mui/material";
import { isAxiosError } from "axios";
import { useContext, useState } from "react";
import { toast } from "react-toastify";

import { AuthContext } from "./AuthProvider";
import FinishAccountSetupForm from "./Forms/FinishAccountSetupForm";
import LoginForm from "./Forms/LoginForm";
import MfaCodeForm, { MfaType } from "./Forms/MfaCodeForm";

const LoginPage = () => {
  const [username, setUsername] = useState("");
  const [isNewPasswordRequired, setIsNewPasswordRequired] = useState(false);
  const [isMfaChallengeRequired, setIsMfaChallengeRequired] = useState(false);
  const [challengeType, setChallengeType] = useState<string>("");
  const [isLoginLoading, setIsLoginLoading] = useState(false);
  const [loginError, setLoginError] = useState("");
  const [loginErrorTitle, setLoginErrorTitle] = useState("Login Failed");
  const [mfaError, setMfaError] = useState("");
  const authContext = useContext(AuthContext);
  const [availableMfaMethods, setAvailableMfaMethods] = useState<string[]>([]);

  const onNewPasswordRequired = (username: string) => {
    setUsername(username);
    setIsNewPasswordRequired(true);
  };

  const onMfaChallengeRequired = (username: string, challengeType: string) => {
    setUsername(username);
    setChallengeType(challengeType);
    setIsMfaChallengeRequired(true);
  };

  const submitAccountSetup = async (newPassword: string, givenName: string, familyName: string) => {
    setIsLoginLoading(true);
    try {
      await authContext.setNewPassword(username, newPassword, givenName, familyName);
      toast.success("Password changed successfully");
    } catch (error) {
      console.error("Set new password error: ", error);
      toast.error("An error occurred when setting a new password");
    }
    setIsLoginLoading(false);
  };

  const onMfaTypeSelection = async (selectedType: string) => {
    setIsLoginLoading(true);
    try {
      await authContext.selectMfaType(username, selectedType);
      setMfaError("");
      setChallengeType(selectedType);
    } catch (error) {
      console.error("MFA type selection error: ", error);
      toast.error("Error selecting MFA method.");
    } finally {
      setIsLoginLoading(false);
    }
  };

  const submitMfaCode = async (username: string, code: string) => {
    setIsLoginLoading(true);
    setMfaError("");

    try {
      let mfaResponse;
      if (challengeType === MfaType.SOFTWARE_TOKEN_MFA) {
        mfaResponse = await authContext.respondToSoftwareTokenMfa(username, code);
      } else if (challengeType === MfaType.EMAIL_OTP) {
        mfaResponse = await authContext.respondToEmailOtp(username, code);
      } else {
        throw new Error("Unknown challenge type");
      }

      if (mfaResponse.data.user) {
        toast.success("Authentication successful");
      }
    } catch (error) {
      console.error("MFA verification error: ", error);
      if (isAxiosError(error)) {
        if (error?.response?.data?.detail) {
          setMfaError(error?.response?.data?.detail);
          return;
        }
      }
      setMfaError("Verification code is incorrect");
    } finally {
      setIsLoginLoading(false);
    }
  };

  const submitLogin = async (username: string, password: string) => {
    setIsLoginLoading(true);
    setLoginError("");

    try {
      const loginResponse = await authContext.login(username, password);
      if (loginResponse.data.newPasswordRequired) {
        onNewPasswordRequired(username);
        return;
      }
      if (loginResponse.data.mfaChallengeRequired && loginResponse.data.challengeType) {
        onMfaChallengeRequired(username, loginResponse.data.challengeType);
        setAvailableMfaMethods(loginResponse.data.availableChallengeTypes ?? []);
        return;
      }
    } catch (error) {
      displayLoginError(error);
    } finally {
      setIsLoginLoading(false);
    }
  };

  const displayLoginError = (error: unknown) => {
    if (isAxiosError(error)) {
      if (error.response && error.response.status === 400) {
        console.error("Login error: ", error);
        setLoginError("Username or password is incorrect");
        setLoginErrorTitle("Login Failed");
        return;
      }
      if (error?.response && error.response.status === 409) {
        console.error(error);
        setLoginError(error?.response.data.detail);
        setLoginErrorTitle("User Configured Incorrectly");
        return;
      }
      if (error?.response?.data?.detail) {
        console.error(error);
        setLoginError(error?.response?.data?.detail);
        setLoginErrorTitle("Login Failed");
        return;
      }
    }

    console.error("Login error: ", error);
    setLoginError("An error is preventing login");
    setLoginErrorTitle("Login Failed");
  };

  const resetLogin = () => {
    setIsMfaChallengeRequired(false);
  };

  const renderForm = () => {
    if (isNewPasswordRequired) {
      return <FinishAccountSetupForm submitAccountSetup={submitAccountSetup}></FinishAccountSetupForm>;
    }

    if (isMfaChallengeRequired) {
      return (
        <MfaCodeForm
          challengeType={challengeType}
          username={username}
          isLoading={isLoginLoading}
          submitMfaCode={submitMfaCode}
          mfaError={mfaError}
          onMfaTypeSelection={onMfaTypeSelection}
          availableMfaMethods={availableMfaMethods}
          resetLogin={resetLogin}
        />
      );
    }

    return (
      <LoginForm
        isLoginLoading={isLoginLoading}
        submitLogin={submitLogin}
        loginError={loginError}
        loginErrorTitle={loginErrorTitle}
      />
    );
  };

  return <Box className={"fullscreen-form-container"}>{renderForm()}</Box>;
};

export default LoginPage;
