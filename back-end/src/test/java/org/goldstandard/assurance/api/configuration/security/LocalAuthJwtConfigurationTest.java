package org.goldstandard.assurance.api.configuration.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;
import java.util.Date;
import org.goldstandard.assurance.api.configuration.LocalAuthProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtException;

class LocalAuthJwtConfigurationTest {
  private static final String SECRET = "01234567890123456789012345678901";

  private final LocalAuthJwtConfiguration configuration = new LocalAuthJwtConfiguration();

  @Test
  void decoderAcceptsLocallySignedTokenWithLocalIssuer() {
    var decoder = configuration.jwtDecoder(properties(SECRET, true));
    var token = token(SECRET, "local-auth", Instant.now().plusSeconds(60));

    assertThat(decoder.decode(token).getSubject()).isEqualTo("local-user");
  }

  @Test
  void decoderRejectsWrongSignatureIssuerAndExpiredToken() {
    var decoder = configuration.jwtDecoder(properties(SECRET, true));

    assertThatThrownBy(
            () ->
                decoder.decode(
                    token(
                        "different-secret-012345678901",
                        "local-auth",
                        Instant.now().plusSeconds(60))))
        .isInstanceOf(JwtException.class);
    assertThatThrownBy(
            () -> decoder.decode(token(SECRET, "other-issuer", Instant.now().plusSeconds(60))))
        .isInstanceOf(JwtException.class);
    assertThatThrownBy(
            () -> decoder.decode(token(SECRET, "local-auth", Instant.now().minusSeconds(60))))
        .isInstanceOf(JwtException.class);
  }

  @Test
  void decoderRequiresLongSecretAndAtLeastOneWellFormedCredential() {
    assertThatThrownBy(() -> configuration.jwtDecoder(properties("short", true)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("at least 32 characters");
    assertThatThrownBy(() -> configuration.jwtDecoder(properties(SECRET, false)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("At least one");
  }

  private static LocalAuthProperties properties(String secret, boolean withCredential) {
    var properties = new LocalAuthProperties();
    properties.setJwtSecret(secret);
    if (withCredential) {
      var credential = new LocalAuthProperties.Credential();
      credential.setPassword("password");
      credential.setDatabaseUserName("local-user");
      properties.setCredentials(java.util.Map.of("developer", credential));
    }
    return properties;
  }

  private static String token(String secret, String issuer, Instant expiresAt) {
    return JWT.create()
        .withIssuer(issuer)
        .withSubject("local-user")
        .withExpiresAt(Date.from(expiresAt))
        .sign(Algorithm.HMAC256(secret));
  }
}
