package org.goldstandard.assurance.api.service.auth;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.SUPPORT_ROLE;

import java.time.Clock;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.goldstandard.assurance.api.dto.auth.ConfirmForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.ForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.LoginRequestDTO;
import org.goldstandard.assurance.api.dto.auth.SetNewPasswordDTO;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.LoginFailedException;
import org.goldstandard.assurance.api.exception.UserAccountNotAssignedException;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;

@Service
@Builder
@Slf4j
public class AuthService {

  private final CognitoClient cognitoClient;
  private final JwtDecoder jwtDecoder;
  private final UserFinder userFinder;
  private final Clock clock;
  private final ClaimsExtractor claimsExtractor;

  public AuthResultDTO login(LoginRequestDTO request) {
    try {
      return cognitoClient.login(request);
    } catch (NotAuthorizedException e) {
      throw new LoginFailedException(e);
    }
  }

  public void logout(String refreshTokenValue) {
    cognitoClient.logout(refreshTokenValue);
  }

  public boolean isExpired(String accessToken) {
    try {
      jwtDecoder.decode(accessToken);
      return false;
    } catch (JwtValidationException e) {
      log.debug("jwt validation failed {}", e.getMessage());
      return true;
    }
  }

  public AuthResultDTO refreshToken(String refreshToken) {
    try {
      return cognitoClient.refreshToken(refreshToken);
    } catch (NotAuthorizedException e) {
      log.warn(e.getMessage(), e);
      return AuthResultDTO.builder().successful(false).newPasswordRequired(false).build();
    }
  }

  public User findUserByToken(String token) {
    var jwt = jwtDecoder.decode(token);
    var groups = claimsExtractor.extractGroups(jwt);
    var subject = jwt.getSubject();
    var user = userFinder.forceGetUserByIdpUserId(UUID.fromString(subject));
    validatePlatformAccount(user, groups);
    return user;
  }

  public void forgotPassword(ForgotPasswordDTO request) {
    cognitoClient.forgotPassword(request);
  }

  public void confirmForgotPassword(ConfirmForgotPasswordDTO request) {
    cognitoClient.confirmForgotPassword(request);
  }

  public void setNewPassword(SetNewPasswordDTO request) {
    cognitoClient.setNewPassword(request);
  }

  public AuthResultDTO respondToSelectMfaType(
      String session, String username, String challengeType) {
    return cognitoClient.respondToSelectMfaType(session, username, challengeType);
  }

  public AuthResultDTO respondToSoftwareTokenMfa(String session, String username, String code) {
    return cognitoClient.respondToSoftwareTokenMfa(session, username, code);
  }

  public AuthResultDTO respondToEmailOtp(String session, String username, String code) {
    return cognitoClient.respondToEmailOtp(session, username, code);
  }

  private static void validatePlatformAccount(User user, Collection<String> groups) {
    if (Objects.isNull(user.getAccount())
        && !groups.contains(ADMIN_ROLE)
        && !groups.contains(SUPPORT_ROLE)) {
      throw new UserAccountNotAssignedException(user.getUserName());
    }
  }
}
