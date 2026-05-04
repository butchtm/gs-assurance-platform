package org.goldstandard.assurance.api.service.auth;

import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.EMAIL_OTP;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.SELECT_MFA_TYPE;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.SOFTWARE_TOKEN_MFA;

import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.CognitoClientConfiguration;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.goldstandard.assurance.api.dto.auth.ConfirmForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.ForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.LoginRequestDTO;
import org.goldstandard.assurance.api.dto.auth.MfaChallengeResponseDTO;
import org.goldstandard.assurance.api.dto.auth.SetNewPasswordDTO;
import org.goldstandard.assurance.api.dto.mfa.MfaSettingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AssociateSoftwareTokenRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AssociateSoftwareTokenResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RevokeTokenRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenResponse;

@Component
@Slf4j
public class CognitoClient {

  private final String clientId;
  private final CognitoIdentityProviderClient awsClient;
  private final CognitoAuthFactory authFactory;

  @Autowired
  public CognitoClient(
      CognitoClientConfiguration cognitoConfig,
      CognitoIdentityProviderClient awsClient,
      CognitoAuthFactory authFactory) {
    this.clientId = cognitoConfig.getClientId();
    this.awsClient = awsClient;
    this.authFactory = authFactory;
  }

  public AuthResultDTO login(LoginRequestDTO dto) {
    var request = authFactory.createLoginInitiateAuthRequest(clientId, dto);
    var response = awsClient.initiateAuth(request);
    return authFactory.createAuthResultFromInitiateAuth(response);
  }

  public void logout(String refreshToken) {
    awsClient.revokeToken(
        RevokeTokenRequest.builder().clientId(clientId).token(refreshToken).build());
  }

  public AuthResultDTO refreshToken(String refreshToken) {
    var request = authFactory.createRefreshInitiateAuthRequest(clientId, refreshToken);
    var response = awsClient.initiateAuth(request);
    return authFactory.createAuthResultFromInitiateAuth(response);
  }

  public void forgotPassword(ForgotPasswordDTO dto) {
    var request = authFactory.createForgotPasswordRequest(clientId, dto);
    var response = awsClient.forgotPassword(request);
    var deliveryDetails = response.codeDeliveryDetails();
    var httpResponse = response.sdkHttpResponse();
    log.debug("delivery details {} successful {}", deliveryDetails, httpResponse.isSuccessful());
  }

  public void confirmForgotPassword(ConfirmForgotPasswordDTO dto) {
    var request = authFactory.createConfirmForgotPasswordRequest(clientId, dto);
    awsClient.confirmForgotPassword(request);
  }

  public void setNewPassword(SetNewPasswordDTO dto) {
    var request = authFactory.createNewPasswordChallengeRequest(clientId, dto);
    awsClient.respondToAuthChallenge(request);
  }

  public AuthResultDTO respondToMfaChallenge(MfaChallengeResponseDTO dto) {
    var request = authFactory.createMfaChallengeRequest(clientId, dto);
    var response = awsClient.respondToAuthChallenge(request);
    return authFactory.createAuthResultFromChallengeResponse(response);
  }

  public AuthResultDTO respondToSelectMfaType(
      String session, String username, String challengeType) {
    var dto =
        buildResponseDto(session, username, SELECT_MFA_TYPE)
            .challengeSelection(challengeType)
            .build();
    return respondToMfaChallenge(dto);
  }

  public AuthResultDTO respondToSoftwareTokenMfa(String session, String username, String code) {
    var dto = buildResponseDto(session, username, SOFTWARE_TOKEN_MFA).code(code).build();
    return respondToMfaChallenge(dto);
  }

  public AuthResultDTO respondToEmailOtp(String session, String username, String code) {
    var dto = buildResponseDto(session, username, EMAIL_OTP).code(code).build();
    return respondToMfaChallenge(dto);
  }

  private static MfaChallengeResponseDTO.MfaChallengeResponseDTOBuilder buildResponseDto(
      String session, String username, ChallengeNameType challengeType) {
    return MfaChallengeResponseDTO.builder()
        .session(session)
        .username(username)
        .challengeType(challengeType.name());
  }

  public GetUserResponse getUser(String accessToken) {
    var request = GetUserRequest.builder().accessToken(accessToken).build();
    return awsClient.getUser(request);
  }

  public void setUserMfaPreferenceAsAdmin(
      String userPoolId, String username, MfaSettingsDTO requestDto) {
    awsClient.adminSetUserMFAPreference(
        request ->
            request
                .userPoolId(userPoolId)
                .username(username)
                .softwareTokenMfaSettings(
                    settings -> settings.enabled(requestDto.isSoftwareTokenMfaEnabled()))
                .emailMfaSettings(settings -> settings.enabled(requestDto.isEmailMfaEnabled())));
  }

  public void setUserMfaPreference(String accessToken, MfaSettingsDTO requestDto) {
    awsClient.setUserMFAPreference(
        request ->
            request
                .accessToken(accessToken)
                .softwareTokenMfaSettings(
                    settings -> settings.enabled(requestDto.isSoftwareTokenMfaEnabled()))
                .emailMfaSettings(settings -> settings.enabled(requestDto.isEmailMfaEnabled())));
  }

  public AssociateSoftwareTokenResponse associateSoftwareToken(String accessToken) {
    var request = AssociateSoftwareTokenRequest.builder().accessToken(accessToken).build();
    return awsClient.associateSoftwareToken(request);
  }

  public VerifySoftwareTokenResponse verifySoftwareToken(
      String accessToken, String userCode, boolean friendlyDevice) {
    var request =
        VerifySoftwareTokenRequest.builder()
            .accessToken(accessToken)
            .userCode(userCode)
            .friendlyDeviceName(friendlyDevice ? "Trusted Device" : null)
            .build();
    return awsClient.verifySoftwareToken(request);
  }

  public GetUserResponse adminGetUser(String userPoolId, String username) {
    var request = AdminGetUserRequest.builder().userPoolId(userPoolId).username(username).build();
    var response = awsClient.adminGetUser(request);

    return GetUserResponse.builder()
        .username(response.username())
        .userAttributes(response.userAttributes())
        .userMFASettingList(response.userMFASettingList())
        .mfaOptions(response.mfaOptions())
        .build();
  }
}
