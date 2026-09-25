package org.goldstandard.assurance.api.configuration.security;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.goldstandard.assurance.api.configuration.LocalAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@Profile("local & local-auth")
@EnableConfigurationProperties(LocalAuthProperties.class)
public class LocalAuthJwtConfiguration {
  private static final int MINIMUM_SECRET_LENGTH = 32;

  @Bean
  public JwtDecoder jwtDecoder(LocalAuthProperties properties) {
    SecretKey key = secretKey(properties.getJwtSecret());
    validateCredentials(properties);
    var decoder = NimbusJwtDecoder.withSecretKey(key).build();
    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("local-auth"));
    return decoder;
  }

  private static void validateCredentials(LocalAuthProperties properties) {
    if (properties.getCredentials().isEmpty()) {
      throw new IllegalStateException(
          "At least one local-auth credential must be configured when the local-auth profile is active");
    }
    properties
        .getCredentials()
        .forEach(
            (alias, credential) -> {
              if (alias.isBlank()
                  || credential.getPassword() == null
                  || credential.getPassword().isBlank()
                  || credential.getDatabaseUserName() == null
                  || credential.getDatabaseUserName().isBlank()) {
                throw new IllegalStateException(
                    "Every local-auth credential requires an alias, password, and database-user-name");
              }
            });
  }

  private static SecretKey secretKey(String secret) {
    byte[] secretBytes = Objects.toString(secret, "").getBytes(StandardCharsets.UTF_8);
    if (secretBytes.length < MINIMUM_SECRET_LENGTH) {
      throw new IllegalStateException(
          "LOCAL_AUTH_JWT_SECRET must be configured with at least 32 characters when the local-auth profile is active");
    }
    return new SecretKeySpec(secretBytes, "HmacSHA256");
  }
}
