package org.goldstandard.assurance.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType.EMAIL_OTP;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.goldstandard.assurance.api.dto.auth.ConfirmForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.ForgotPasswordDTO;
import org.goldstandard.assurance.api.dto.auth.LoginRequestDTO;
import org.goldstandard.assurance.api.dto.auth.SetNewPasswordDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.LoginFailedException;
import org.goldstandard.assurance.api.exception.UserAccountNotAssignedException;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  private AuthService service;

  @Mock private CognitoClient cognitoClient;
  @Mock private JwtDecoder jwtDecoder;
  @Mock private UserFinder userFinder;
  @Mock private ClaimsExtractor claimsExtractor;

  @BeforeEach
  void setUp() {
    Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
    service =
        AuthService.builder()
            .cognitoClient(cognitoClient)
            .jwtDecoder(jwtDecoder)
            .userFinder(userFinder)
            .clock(fixedClock)
            .claimsExtractor(claimsExtractor)
            .build();
  }

  @Test
  void loginSuccessReturnsAuthResult() {
    var loginRequest =
        LoginRequestDTO.builder().username("user@example.com").password("password123").build();

    var expectedResult =
        AuthResultDTO.builder()
            .successful(true)
            .idToken("id-token")
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .build();

    when(cognitoClient.login(loginRequest)).thenReturn(expectedResult);

    var result = service.login(loginRequest);

    assertThat(result).isEqualTo(expectedResult);
    assertThat(result.isSuccessful()).isTrue();
    verify(cognitoClient).login(loginRequest);
  }

  @Test
  void loginFailureThrowsLoginFailedException() {
    var loginRequest =
        LoginRequestDTO.builder().username("user@example.com").password("wrongpassword").build();

    when(cognitoClient.login(loginRequest))
        .thenThrow(NotAuthorizedException.builder().message("Invalid credentials").build());

    assertThrows(LoginFailedException.class, () -> service.login(loginRequest));
  }

  @Test
  void isExpiredReturnsFalseForValidToken() {
    var accessToken = "valid-token";
    var mockJwt = createMockJwt();

    when(jwtDecoder.decode(accessToken)).thenReturn(mockJwt);

    var result = service.isExpired(accessToken);

    assertThat(result).isFalse();
  }

  @Test
  void refreshTokenSuccessReturnsAuthResult() {
    var refreshToken = "refresh-token-123";
    var expectedResult =
        AuthResultDTO.builder()
            .successful(true)
            .accessToken("new-access-token")
            .refreshToken("new-refresh-token")
            .build();

    when(cognitoClient.refreshToken(refreshToken)).thenReturn(expectedResult);

    var result = service.refreshToken(refreshToken);

    assertThat(result).isEqualTo(expectedResult);
    assertThat(result.isSuccessful()).isTrue();
  }

  @Test
  void refreshTokenFailureReturnsUnsuccessfulResult() {
    var refreshToken = "invalid-refresh-token";

    when(cognitoClient.refreshToken(refreshToken))
        .thenThrow(NotAuthorizedException.builder().message("Token expired").build());

    var result = service.refreshToken(refreshToken);

    assertThat(result.isSuccessful()).isFalse();
    assertThat(result.isNewPasswordRequired()).isFalse();
  }

  @Test
  void findUserByTokenReturnsUserWithAccount() {
    var token = "valid-token";
    var userId = UUID.randomUUID();
    var mockJwt = createMockJwt(userId.toString());
    var mockUser = createMockUserWithAccount();

    when(jwtDecoder.decode(token)).thenReturn(mockJwt);
    when(claimsExtractor.extractGroups(mockJwt)).thenReturn(List.of("PROJECT_DEVELOPER"));
    when(userFinder.forceGetUserByIdpUserId(userId)).thenReturn(mockUser);

    var result = service.findUserByToken(token);

    assertThat(result).isEqualTo(mockUser);
    assertThat(result.getAccount()).isNotNull();
  }

  @Test
  void findUserByTokenThrowsException() {
    var token = "user-token";
    var userId = UUID.randomUUID();
    var mockJwt = createMockJwt(userId.toString());
    var mockUser = createMockUserWithoutAccount();

    when(jwtDecoder.decode(token)).thenReturn(mockJwt);
    when(claimsExtractor.extractGroups(mockJwt)).thenReturn(List.of("PROJECT_DEVELOPER"));
    when(userFinder.forceGetUserByIdpUserId(userId)).thenReturn(mockUser);

    assertThrows(UserAccountNotAssignedException.class, () -> service.findUserByToken(token));
  }

  @Test
  void forgotPasswordCallsCognitoClient() {
    var request = ForgotPasswordDTO.builder().username("user@example.com").build();

    service.forgotPassword(request);

    verify(cognitoClient).forgotPassword(request);
  }

  @Test
  void confirmForgotPasswordCallsCognitoClient() {
    var request =
        ConfirmForgotPasswordDTO.builder()
            .username("user@example.com")
            .newPassword("newPassword123")
            .confirmationCode("123456")
            .build();

    service.confirmForgotPassword(request);

    verify(cognitoClient).confirmForgotPassword(request);
  }

  @Test
  void setNewPasswordCallsCognitoClient() {
    var request =
        SetNewPasswordDTO.builder()
            .username("user@example.com")
            .newPassword("newPassword123")
            .sessionId("session-123")
            .givenName("John")
            .familyName("Doe")
            .build();

    service.setNewPassword(request);

    verify(cognitoClient).setNewPassword(request);
  }

  @Test
  void shouldUseCognitoClientToRespondToSelectMfaTypeChallenge() {
    var session = "session-123";
    var username = "user@example.com";
    var challengeType = EMAIL_OTP.name();
    var expectedResult = AuthResultDTO.builder().challengeType(challengeType).build();

    when(cognitoClient.respondToSelectMfaType(session, username, challengeType))
        .thenReturn(expectedResult);

    var result = service.respondToSelectMfaType(session, username, challengeType);

    assertThat(result).isEqualTo(expectedResult);
    verify(cognitoClient).respondToSelectMfaType(session, username, challengeType);
  }

  @Test
  void respondToSoftwareTokenMfaCallsCognitoClient() {
    var session = "session-123";
    var username = "user@example.com";
    var code = "123456";
    var expectedResult =
        AuthResultDTO.builder().successful(true).accessToken("access-token").build();

    when(cognitoClient.respondToSoftwareTokenMfa(session, username, code))
        .thenReturn(expectedResult);

    var result = service.respondToSoftwareTokenMfa(session, username, code);

    assertThat(result).isEqualTo(expectedResult);
    verify(cognitoClient).respondToSoftwareTokenMfa(session, username, code);
  }

  @Test
  void respondToEmailOtpCallsCognitoClient() {
    var session = "session-123";
    var username = "user@example.com";
    var code = "123456";
    var expectedResult =
        AuthResultDTO.builder().successful(true).accessToken("access-token").build();

    when(cognitoClient.respondToEmailOtp(session, username, code)).thenReturn(expectedResult);

    var result = service.respondToEmailOtp(session, username, code);

    assertThat(result).isEqualTo(expectedResult);
    verify(cognitoClient).respondToEmailOtp(session, username, code);
  }

  private Jwt createMockJwt() {
    return createMockJwt("test-subject");
  }

  private Jwt createMockJwt(String subject) {
    return Jwt.withTokenValue("token")
        .header("alg", "RS256")
        .subject(subject)
        .claim("groups", List.of("PROJECT_DEVELOPER"))
        .build();
  }

  private User createMockUserWithAccount() {
    var account = Account.builder().id(1).name("Test Account").build();

    return User.builder().id(2).userName("user@example.com").account(account).build();
  }

  private User createMockUserWithoutAccount() {
    return User.builder().id(3).userName("admin@example.com").account(null).build();
  }
}
