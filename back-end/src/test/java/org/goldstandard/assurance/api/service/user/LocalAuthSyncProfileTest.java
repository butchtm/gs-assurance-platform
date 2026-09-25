package org.goldstandard.assurance.api.service.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.goldstandard.assurance.api.configuration.security.LocalAuthJwtConfiguration;
import org.goldstandard.assurance.api.controller.UserSyncController;
import org.goldstandard.assurance.api.service.auth.LocalAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

class LocalAuthSyncProfileTest {
  @Test
  void localAuthAloneKeepsCognitoSyncAndDoesNotActivateLocalAuth() throws IOException {
    assertProfileActive(UserSyncController.class, true, "local-auth");
    assertProfileActive(UserSyncOnStartUpListener.class, true, "local-auth");
    assertLocalAuthInactive("local-auth");
    assertThat(localAuthConfig().getProperty("local-auth.enabled")).isNull();
  }

  @Test
  void bothLocalProfilesEnableBypassAndExcludeCognitoSync() throws IOException {
    assertProfileActive(UserSyncController.class, false, "local", "local-auth");
    assertProfileActive(UserSyncOnStartUpListener.class, false, "local", "local-auth");
    assertLocalAuthActive("local", "local-auth");
    assertThat(localAuthConfig().getProperty("local-auth.enabled")).isEqualTo(true);
  }

  @Test
  void plainLocalProfileKeepsCognitoSyncAndCognitoLogin() {
    assertProfileActive(UserSyncController.class, true, "local");
    assertProfileActive(UserSyncOnStartUpListener.class, true, "local");
    assertLocalAuthInactive("local");
  }

  private void assertProfileActive(Class<?> type, boolean expected, String... activeProfiles) {
    var expression = String.join(" & ", type.getAnnotation(Profile.class).value());
    assertThat(acceptsProfiles(expression, activeProfiles)).isEqualTo(expected);
  }

  private void assertLocalAuthActive(String... activeProfiles) {
    assertThat(acceptsProfiles(localAuthProfileExpression(), activeProfiles)).isTrue();
    assertThat(
            acceptsProfiles(
                String.join(" & ", LocalAuthService.class.getAnnotation(Profile.class).value()),
                activeProfiles))
        .isTrue();
    assertThat(
            acceptsProfiles(
                String.join(
                    " & ", LocalAuthJwtConfiguration.class.getAnnotation(Profile.class).value()),
                activeProfiles))
        .isTrue();
  }

  private void assertLocalAuthInactive(String... activeProfiles) {
    assertThat(acceptsProfiles(localAuthProfileExpression(), activeProfiles)).isFalse();
    assertThat(
            acceptsProfiles(
                String.join(" & ", LocalAuthService.class.getAnnotation(Profile.class).value()),
                activeProfiles))
        .isFalse();
    assertThat(
            acceptsProfiles(
                String.join(
                    " & ", LocalAuthJwtConfiguration.class.getAnnotation(Profile.class).value()),
                activeProfiles))
        .isFalse();
  }

  private boolean acceptsProfiles(String expression, String... activeProfiles) {
    var environment = new StandardEnvironment();
    environment.setActiveProfiles(activeProfiles);
    return environment.acceptsProfiles(Profiles.of(expression));
  }

  private String localAuthProfileExpression() {
    return localAuthConfig().getProperty("spring.config.activate.on-profile").toString();
  }

  private org.springframework.core.env.PropertySource<?> localAuthConfig() {
    try {
      return new YamlPropertySourceLoader()
          .load("application-local-auth", new ClassPathResource("application-local-auth.yml"))
          .getFirst();
    } catch (IOException e) {
      throw new AssertionError("Unable to load local-auth profile configuration", e);
    }
  }
}
