package org.goldstandard.assurance.api.service.auth;

import static software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType.REFRESH_TOKEN_AUTH;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType.USER_PASSWORD_AUTH;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.EMAIL_OTP;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.SELECT_MFA_TYPE;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.SOFTWARE_TOKEN_MFA;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.goldstandard.assurance.api.dto.auth.ConfirmForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.ForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.LoginRequestDTO;
import org.goldstandard.assurance.api.dto.auth.MfaChallengeResponseDTO;
import org.goldstandard.assurance.api.dto.auth.SetNewPasswordDTO;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeResponse;

@Component
public class CognitoAuthFactory {

  private static final String USERNAME_PARAM = "USERNAME";
  private static final String ANSWER_PARAM = "ANSWER";
  private static final String MFA_OPTIONS_PARAM = "MFAS_CAN_CHOOSE";

  public InitiateAuthRequest createLoginInitiateAuthRequest(String clientId, LoginRequestDTO dto) {
    return InitiateAuthRequest.builder()
        .clientId(clientId)
        .authFlow(USER_PASSWORD_AUTH)
        .authParameters(Map.of(USERNAME_PARAM, dto.getUsername(), "PASSWORD", dto.getPassword()))
        .build();
  }

  public InitiateAuthRequest createRefreshInitiateAuthRequest(
      String clientId, String refreshToken) {
    return InitiateAuthRequest.builder()
        .clientId(clientId)
        .authFlow(REFRESH_TOKEN_AUTH)
        .authParameters(Map.of("REFRESH_TOKEN", refreshToken))
        .build();
  }

  public ForgotPasswordRequest createForgotPasswordRequest(String clientId, ForgotPasswordDTO dto) {
    return ForgotPasswordRequest.builder().clientId(clientId).username(dto.getUsername()).build();
  }

  public ConfirmForgotPasswordRequest createConfirmForgotPasswordRequest(
      String clientId, ConfirmForgotPasswordDTO dto) {
    return ConfirmForgotPasswordRequest.builder()
        .clientId(clientId)
        .username(dto.getUsername())
        .password(dto.getNewPassword())
        .confirmationCode(dto.getConfirmationCode())
        .build();
  }

  public RespondToAuthChallengeRequest createNewPasswordChallengeRequest(
      String clientId, SetNewPasswordDTO dto) {
    var responses =
        Map.of(
            USERNAME_PARAM,
            dto.getUsername(),
            "NEW_PASSWORD",
            dto.getNewPassword(),
            "userAttributes.given_name",
            dto.getGivenName(),
            "userAttributes.family_name",
            dto.getFamilyName());
    return buildRespondToChallengeRequest(
            NEW_PASSWORD_REQUIRED, clientId, dto.getSessionId(), responses)
        .build();
  }

  public RespondToAuthChallengeRequest createMfaChallengeRequest(
      String clientId, MfaChallengeResponseDTO dto) {
    Map<String, String> responses;

    if (SOFTWARE_TOKEN_MFA.name().equals(dto.getChallengeType())) {
      responses =
          Map.of(USERNAME_PARAM, dto.getUsername(), "SOFTWARE_TOKEN_MFA_CODE", dto.getCode());
      return buildRespondToChallengeRequest(
              SOFTWARE_TOKEN_MFA, clientId, dto.getSession(), responses)
          .build();
    } else if (EMAIL_OTP.name().equals(dto.getChallengeType())) {
      responses = Map.of("EMAIL_OTP_CODE", dto.getCode(), USERNAME_PARAM, dto.getUsername());
      return buildRespondToChallengeRequest(EMAIL_OTP, clientId, dto.getSession(), responses)
          .build();
    } else if (SELECT_MFA_TYPE.name().equals(dto.getChallengeType())) {
      responses =
          Map.of(USERNAME_PARAM, dto.getUsername(), ANSWER_PARAM, dto.getChallengeSelection());
      return buildRespondToChallengeRequest(SELECT_MFA_TYPE, clientId, dto.getSession(), responses)
          .build();
    } else {
      throw new IllegalArgumentException("Unsupported challenge type: " + dto.getChallengeType());
    }
  }

  public AuthResultDTO createAuthResultFromInitiateAuth(InitiateAuthResponse response) {
    var result = response.authenticationResult();
    var challengeName = response.challengeName();

    if (SOFTWARE_TOKEN_MFA.equals(challengeName)) {
      return buildMfaAuthResult(response, SOFTWARE_TOKEN_MFA).build();
    }

    if (EMAIL_OTP.equals(challengeName)) {
      return buildMfaAuthResult(response, EMAIL_OTP).build();
    }

    if (SELECT_MFA_TYPE.equals(challengeName)) {
      return buildMfaAuthResult(response, SELECT_MFA_TYPE)
          .availableChallengeTypes(extractMfaOptionsFromResponse(response))
          .build();
    }

    if (NEW_PASSWORD_REQUIRED.equals(challengeName)) {
      return AuthResultDTO.builder()
          .successful(false)
          .session(response.session())
          .newPasswordRequired(true)
          .build();
    }

    if (Objects.nonNull(result)) {
      return AuthResultDTO.builder()
          .successful(true)
          .idToken(result.idToken())
          .accessToken(result.accessToken())
          .refreshToken(result.refreshToken())
          .build();
    }

    return AuthResultDTO.builder().successful(false).session(response.session()).build();
  }

  public AuthResultDTO createAuthResultFromChallengeResponse(
      RespondToAuthChallengeResponse response) {
    var result = response.authenticationResult();
    var challengeName = response.challengeName();

    if (SOFTWARE_TOKEN_MFA.equals(challengeName)) {
      return AuthResultDTO.builder()
          .successful(false)
          .session(response.session())
          .mfaChallengeRequired(true)
          .challengeType(SOFTWARE_TOKEN_MFA.name())
          .build();
    }

    if (EMAIL_OTP.equals(challengeName)) {
      return AuthResultDTO.builder()
          .successful(false)
          .session(response.session())
          .mfaChallengeRequired(true)
          .challengeType(EMAIL_OTP.name())
          .build();
    }

    if (NEW_PASSWORD_REQUIRED.equals(challengeName)) {
      return AuthResultDTO.builder()
          .successful(false)
          .session(response.session())
          .newPasswordRequired(true)
          .build();
    }

    if (Objects.nonNull(result)) {
      return AuthResultDTO.builder()
          .successful(true)
          .idToken(result.idToken())
          .accessToken(result.accessToken())
          .refreshToken(result.refreshToken())
          .build();
    }

    return AuthResultDTO.builder().successful(false).session(response.session()).build();
  }

  private static RespondToAuthChallengeRequest.Builder buildRespondToChallengeRequest(
      ChallengeNameType challengeType, String clientId, String dto, Map<String, String> responses) {
    return RespondToAuthChallengeRequest.builder()
        .challengeName(challengeType)
        .clientId(clientId)
        .session(dto)
        .challengeResponses(responses);
  }

  /**
   * AWS returns the MFA options in the format ["EMAIL_OTP","SOFTWARE_TOKEN_MFA"] so this method
   * extracts it into a nice list
   */
  private static List<String> extractMfaOptionsFromResponse(InitiateAuthResponse response) {
    var mfaOptionsRaw = response.challengeParameters().get(MFA_OPTIONS_PARAM);
    return Arrays.asList(
        mfaOptionsRaw.substring(1, mfaOptionsRaw.length() - 1).replace("\"", "").split(","));
  }

  private static AuthResultDTO.AuthResultDTOBuilder buildMfaAuthResult(
      InitiateAuthResponse response, ChallengeNameType challengeType) {
    return AuthResultDTO.builder()
        .successful(false)
        .session(response.session())
        .mfaChallengeRequired(true)
        .challengeType(challengeType.name());
  }
}
