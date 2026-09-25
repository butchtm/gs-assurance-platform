package org.goldstandard.assurance.api.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.configuration.LocalAuthProperties;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.goldstandard.assurance.api.dto.auth.LoginRequestDTO;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.LoginFailedException;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local & local-auth")
@RequiredArgsConstructor
public class LocalAuthService {
  private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);
  private static final String ISSUER = "local-auth";

  private final LocalAuthProperties properties;
  private final UserFinder userFinder;
  private final Clock clock;

  public AuthResultDTO login(LoginRequestDTO request) {
    if (request == null || request.getUsername() == null || request.getPassword() == null) {
      throw new LoginFailedException();
    }

    var credential = properties.getCredentials().get(request.getUsername());
    if (credential == null || !credential.matches(request.getPassword())) {
      throw new LoginFailedException();
    }

    User user =
        userFinder
            .getByUserName(credential.getDatabaseUserName())
            .filter(User::isActive)
            .filter(found -> found.getIdpUserId() != null)
            .orElseThrow(LoginFailedException::new);

    Instant now = clock.instant();
    String accessToken = createToken(user, now, "access");
    String idToken = createToken(user, now, "id");
    return AuthResultDTO.builder()
        .idToken(idToken)
        .accessToken(accessToken)
        .successful(true)
        .newPasswordRequired(false)
        .mfaChallengeRequired(false)
        .build();
  }

  private String createToken(User user, Instant issuedAt, String tokenUse) {
    var groups = Objects.requireNonNullElse(user.getGroups(), List.<String>of());
    return JWT.create()
        .withIssuer(ISSUER)
        .withSubject(user.getIdpUserId().toString())
        .withIssuedAt(Date.from(issuedAt))
        .withExpiresAt(Date.from(issuedAt.plus(TOKEN_LIFETIME)))
        .withClaim("cognito:groups", groups)
        .withClaim("token_use", tokenUse)
        .sign(Algorithm.HMAC256(properties.getJwtSecret()));
  }
}
