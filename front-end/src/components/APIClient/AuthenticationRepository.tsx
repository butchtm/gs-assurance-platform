import axios, { AxiosResponse } from "axios";

import { User } from "../User/User";

interface VerifyTokenResponse {
  authenticated: boolean;
  user: User;
}

export interface LoginResponse {
  newPasswordRequired: boolean;
  mfaChallengeRequired: boolean;
  challengeType?: string;
  user: User;
  availableChallengeTypes?: string[];
}

class AuthenticationRepository {
  private static readonly BASE_LOGIN_PATH = "/api/public";

  public login = (username: string, password: string): Promise<AxiosResponse<LoginResponse>> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/login`, {
      username: username,
      password: password,
    });
  };

  public verifyToken = (): Promise<AxiosResponse<VerifyTokenResponse>> => {
    return axios.get(`${AuthenticationRepository.BASE_LOGIN_PATH}/verify-token`);
  };

  public logout = (): Promise<AxiosResponse> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/logout`);
  };

  public forgotPassword = (username: string): Promise<AxiosResponse> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/forgot-password`, {
      username: username,
    });
  };

  public confirmForgotPassword = (
    username: string,
    newPassword: string,
    confirmationCode: string,
  ): Promise<AxiosResponse> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/confirm-forgot-password`, {
      username: username,
      newPassword: newPassword,
      confirmationCode: confirmationCode,
    });
  };

  public setNewPassword = (
    username: string,
    newPassword: string,
    givenName: string,
    familyName: string,
  ): Promise<AxiosResponse> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/set-new-password`, {
      username: username,
      newPassword: newPassword,
      givenName: givenName,
      familyName: familyName,
    });
  };

  public selectMfaType = (username: string, challengeType: string): Promise<AxiosResponse<LoginResponse>> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/select-mfa-type`, {
      username: username,
      challengeType: challengeType,
    });
  };

  public respondToSoftwareTokenMfa = (username: string, code: string): Promise<AxiosResponse<LoginResponse>> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/respond-to-software-token-mfa`, {
      username: username,
      code: code,
    });
  };

  public respondToEmailOtp = (username: string, code: string): Promise<AxiosResponse<LoginResponse>> => {
    return axios.post(`${AuthenticationRepository.BASE_LOGIN_PATH}/respond-to-email-otp`, {
      username: username,
      code: code,
    });
  };
}

export default new AuthenticationRepository();
