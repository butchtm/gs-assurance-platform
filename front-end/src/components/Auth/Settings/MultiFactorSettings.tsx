import { apiClient } from "../../APIClient/Client";

interface BaseMfaSettings {
  softwareTokenMfaEnabled: boolean;
  emailMfaEnabled: boolean;
}

export type MfaSettings = BaseMfaSettings;

export type UpdateMfaSettingsRequest = Partial<BaseMfaSettings>;

export interface AdminUpdateMfaSettingsRequest extends UpdateMfaSettingsRequest {
  username: string;
}

export interface UpdateMfaSettingsResponse extends BaseMfaSettings {
  success: boolean;
}

export interface SoftwareTokenSetupResponse {
  secretCode: string;
  otpAuthUrl: string;
}

interface VerifySoftwareTokenRequest {
  code: string;
  emailMfaEnabled: boolean;
}

interface VerifySoftwareTokenResponse {
  verified: boolean;
}

export const getMfaSettings = (username?: string): Promise<MfaSettings> => {
  const url = username ? `/mfa/settings?username=${encodeURIComponent(username)}` : "/mfa/settings";
  return apiClient.makeRequest(url, "GET");
};

export const updateMfaSettings = (request: UpdateMfaSettingsRequest): Promise<UpdateMfaSettingsResponse> => {
  return apiClient.makeRequest("/mfa/settings", "PUT", JSON.stringify(request));
};

export const adminUpdateMfaSettings = (request: AdminUpdateMfaSettingsRequest): Promise<UpdateMfaSettingsResponse> => {
  return apiClient.makeRequest("/mfa/settings", "PUT", JSON.stringify(request));
};

export const setupSoftwareToken = (): Promise<SoftwareTokenSetupResponse> => {
  return apiClient.makeRequest("/mfa/software-token/setup", "POST");
};

export const verifySoftwareToken = (request: VerifySoftwareTokenRequest): Promise<VerifySoftwareTokenResponse> => {
  return apiClient.makeRequest("/mfa/software-token/verify", "POST", JSON.stringify(request));
};
