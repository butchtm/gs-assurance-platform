package org.goldstandard.assurance.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.goldstandard.assurance.api.configuration.LocalAuthProperties;
import org.goldstandard.assurance.api.dto.auth.LoginRequestDTO;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.LoginFailedException;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalAuthServiceTest {
  private static final String PASSWORD = "local-password";
  private static final String ALIAS = "developer";
  private static final String DATABASE_USERNAME = "persisted-user";
  private static final UUID IDP_USER_ID = UUID.fromString("c2f62d77-b205-4882-8204-89052d1271fe");
  private static final Instant NOW = Instant.parse("2026-09-23T00:00:00Z");

  @Mock UserFinder userFinder;

  private final LocalAuthProperties properties = new LocalAuthProperties();
  private LocalAuthService service;

  @BeforeEach
  void setUp() {
    var credential = new LocalAuthProperties.Credential();
    credential.setPassword(PASSWORD);
    credential.setDatabaseUserName(DATABASE_USERNAME);
    properties.setJwtSecret("01234567890123456789012345678901");
    properties.setCredentials(java.util.Map.of(ALIAS, credential));
    service = new LocalAuthService(properties, userFinder, Clock.fixed(NOW, ZoneOffset.UTC));
  }

  @Test
  void loginIssuesTokensForActivePersistedUserWithPersistedGroups() {
    var user =
        User.builder()
            .userName(DATABASE_USERNAME)
            .isActive(true)
            .idpUserId(IDP_USER_ID)
            .groups(List.of("VVB", "REVIEWER"))
            .build();
    when(userFinder.getByUserName(DATABASE_USERNAME)).thenReturn(Optional.of(user));

    var result = service.login(new LoginRequestDTO(ALIAS, PASSWORD));

    assertThat(result.isSuccessful()).isTrue();
    var accessToken = JWT.decode(result.getAccessToken());
    var idToken = JWT.decode(result.getIdToken());
    assertThat(accessToken.getSubject()).isEqualTo(IDP_USER_ID.toString());
    assertThat(accessToken.getClaim("cognito:groups").asList(String.class))
        .containsExactly("VVB", "REVIEWER");
    assertThat(accessToken.getClaim("token_use").asString()).isEqualTo("access");
    assertThat(idToken.getSubject()).isEqualTo(IDP_USER_ID.toString());
    assertThat(idToken.getClaim("token_use").asString()).isEqualTo("id");
    assertThat(accessToken.getExpiresAtAsInstant()).isEqualTo(NOW.plusSeconds(3600));
    verify(userFinder).getByUserName(DATABASE_USERNAME);
  }

  @Test
  void loginRejectsUnknownAliasAndWrongPasswordBeforeLookingUpUser() {
    assertThatThrownBy(() -> service.login(new LoginRequestDTO("unknown", PASSWORD)))
        .isInstanceOf(LoginFailedException.class);
    assertThatThrownBy(() -> service.login(new LoginRequestDTO(ALIAS, "wrong")))
        .isInstanceOf(LoginFailedException.class);
  }

  @Test
  void loginRejectsMissingOrInactivePersistedUser() {
    when(userFinder.getByUserName(DATABASE_USERNAME)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.login(new LoginRequestDTO(ALIAS, PASSWORD)))
        .isInstanceOf(LoginFailedException.class);

    when(userFinder.getByUserName(DATABASE_USERNAME))
        .thenReturn(Optional.of(User.builder().isActive(false).idpUserId(IDP_USER_ID).build()));
    assertThatThrownBy(() -> service.login(new LoginRequestDTO(ALIAS, PASSWORD)))
        .isInstanceOf(LoginFailedException.class);
  }

  @Test
  void loginRejectsPersistedUserWithoutIdentityProviderSubject() {
    when(userFinder.getByUserName(DATABASE_USERNAME))
        .thenReturn(Optional.of(User.builder().isActive(true).build()));

    assertThatThrownBy(() -> service.login(new LoginRequestDTO(ALIAS, PASSWORD)))
        .isInstanceOf(LoginFailedException.class);
  }
}
