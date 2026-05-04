package org.goldstandard.assurance.api.service.mfa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.mfa.MfaSettingsDTO;
import org.goldstandard.assurance.api.dto.mfa.SoftwareTokenSetupResponseDTO;
import org.goldstandard.assurance.api.dto.mfa.UpdateMfaSettingsResponseDTO;
import org.goldstandard.assurance.api.dto.mfa.VerifySoftwareTokenRequestDTO;
import org.goldstandard.assurance.api.dto.mfa.VerifySoftwareTokenResponseDTO;
import org.goldstandard.assurance.api.exception.MfaException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.auth.CognitoClient;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class MfaService {

  private final CognitoClient cognitoClient;
  private final PermissionsService permissionsService;
  private final AuthenticatedUserSupplier userSupplier;

  @Value("${cognito.user-pool.id}")
  private String userPoolId;

  public MfaSettingsDTO getMfaSettings(String targetUsername) {
    if (permissionsService.isAdmin() && targetUsername != null) {
      return getMfaSettingsAsAdmin(targetUsername);
    } else {
      return getMfaSettingsAsUser();
    }
  }

  private MfaSettingsDTO getMfaSettingsAsAdmin(String targetUsername) {
    GetUserResponse userResponse = cognitoClient.adminGetUser(userPoolId, targetUsername);

    return MfaSettingsDTO.builder()
        .username(targetUsername)
        .softwareTokenMfaEnabled(isSoftwareTokenMfaEnabled(userResponse))
        .emailMfaEnabled(isEmailMfaEnabled(userResponse))
        .build();
  }

  private MfaSettingsDTO getMfaSettingsAsUser() {
    String username =
        userSupplier
            .getAuthenticatedUsername()
            .orElseThrow(() -> new IllegalStateException("User not authenticated"));

    String accessToken = getCurrentUserAccessToken();
    GetUserResponse userResponse = cognitoClient.getUser(accessToken);

    return MfaSettingsDTO.builder()
        .username(username)
        .softwareTokenMfaEnabled(isSoftwareTokenMfaEnabled(userResponse))
        .emailMfaEnabled(isEmailMfaEnabled(userResponse))
        .build();
  }

  public UpdateMfaSettingsResponseDTO updateMfaSettings(MfaSettingsDTO request) {
    try {
      if (permissionsService.isAdmin() && request.getUsername() != null) {
        return updateMfaSettingsAsAdmin(request);
      } else {
        return updateMfaSettingsAsUser(request);
      }
    } catch (Exception e) {
      throw new MfaException("Failed to update MFA settings", e);
    }
  }

  private UpdateMfaSettingsResponseDTO updateMfaSettingsAsAdmin(MfaSettingsDTO request) {
    String targetUsername = getTargetUsername(request);

    cognitoClient.setUserMfaPreferenceAsAdmin(userPoolId, targetUsername, request);

    return UpdateMfaSettingsResponseDTO.builder()
        .success(true)
        .softwareTokenMfaEnabled(request.isSoftwareTokenMfaEnabled())
        .emailMfaEnabled(request.isEmailMfaEnabled())
        .build();
  }

  private UpdateMfaSettingsResponseDTO updateMfaSettingsAsUser(MfaSettingsDTO request) {
    String accessToken = getCurrentUserAccessToken();

    cognitoClient.setUserMfaPreference(accessToken, request);

    return UpdateMfaSettingsResponseDTO.builder()
        .success(true)
        .softwareTokenMfaEnabled(request.isSoftwareTokenMfaEnabled())
        .emailMfaEnabled(request.isEmailMfaEnabled())
        .build();
  }

  public SoftwareTokenSetupResponseDTO setupSoftwareToken() {
    try {
      var accessToken = getCurrentUserAccessToken();
      var response = cognitoClient.associateSoftwareToken(accessToken);
      var secretCode = response.secretCode();
      return SoftwareTokenSetupResponseDTO.builder()
          .secretCode(secretCode)
          .otpAuthUrl(toOtpAuthUrl(secretCode))
          .build();
    } catch (Exception e) {
      throw new MfaException("Failed to setup software token", e);
    }
  }

  private String toOtpAuthUrl(String secretCode) {
    var username = userSupplier.forceGetAuthenticatedUsername();
    return String.format(
        "otpauth://totp/GoldStandardAssurance:%s?secret=%s&issuer=GoldStandardAssurance",
        username, secretCode);
  }

  public VerifySoftwareTokenResponseDTO verifySoftwareToken(VerifySoftwareTokenRequestDTO request) {
    try {
      String accessToken = getCurrentUserAccessToken();
      var response = cognitoClient.verifySoftwareToken(accessToken, request.getCode(), true);

      boolean verified = "SUCCESS".equals(response.status().toString());

      MfaSettingsDTO mfaSettings =
          MfaSettingsDTO.builder()
              .username(null)
              .softwareTokenMfaEnabled(true)
              .emailMfaEnabled(request.getEmailMfaEnabled())
              .build();

      if (verified) {
        cognitoClient.setUserMfaPreference(accessToken, mfaSettings);
      }

      return VerifySoftwareTokenResponseDTO.builder().verified(verified).build();
    } catch (Exception e) {
      log.error("Failed to verify software token", e);
      return VerifySoftwareTokenResponseDTO.builder().verified(false).build();
    }
  }

  private String getCurrentUserAccessToken() {
    return userSupplier.forceGetAuthenticatedAccessToken();
  }

  private String getTargetUsername(MfaSettingsDTO request) {
    if (permissionsService.isAdmin() && request.getUsername() != null) {
      return request.getUsername();
    }
    return userSupplier.forceGetAuthenticatedUsername();
  }

  private boolean isSoftwareTokenMfaEnabled(GetUserResponse userResponse) {
    return userResponse.userMFASettingList().contains("SOFTWARE_TOKEN_MFA");
  }

  private boolean isEmailMfaEnabled(GetUserResponse userResponse) {
    return userResponse.userMFASettingList().contains("EMAIL_OTP");
  }
}
