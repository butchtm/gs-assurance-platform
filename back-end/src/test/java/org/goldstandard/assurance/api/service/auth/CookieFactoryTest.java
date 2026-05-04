package org.goldstandard.assurance.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.auth.CookieName.ACCESS_TOKEN;
import static org.goldstandard.assurance.api.service.auth.CookieName.ID_TOKEN;
import static org.goldstandard.assurance.api.service.auth.CookieName.MFA_SESSION_ID;
import static org.goldstandard.assurance.api.service.auth.CookieName.PASSWORD_SESSION_ID;
import static org.goldstandard.assurance.api.service.auth.CookieName.REFRESH_TOKEN;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

class CookieFactoryTest {

  private final CookieFactory factory = new CookieFactory();

  @Test
  void shouldBuildIdTokenCookie() {
    var token = "my-id-token";

    var cookie = factory.buildIdTokenCookie(token);

    assertThat(cookie.getName()).isEqualTo(ID_TOKEN);
    assertThat(cookie.getValue()).isEqualTo(token);
    assertCommonFields(cookie);
  }

  @Test
  void shouldBuildAccessTokenCookie() {
    var token = "my-access-token";

    var cookie = factory.buildAccessTokenCookie(token);

    assertThat(cookie.getName()).isEqualTo(ACCESS_TOKEN);
    assertThat(cookie.getValue()).isEqualTo(token);
    assertCommonFields(cookie);
  }

  @Test
  void shouldBuildRefreshTokenCookie() {
    var token = "my-refresh-token";

    var cookie = factory.buildRefreshTokenCookie(token);

    assertThat(cookie.getName()).isEqualTo(REFRESH_TOKEN);
    assertThat(cookie.getValue()).isEqualTo(token);
    assertCommonFields(cookie);
  }

  @Test
  void shouldBuildPasswordSessionIdTokenCookie() {
    var token = "my-session-id";

    var cookie = factory.buildPasswordSessionIdCookie(token);

    assertThat(cookie.getName()).isEqualTo(PASSWORD_SESSION_ID);
    assertThat(cookie.getValue()).isEqualTo(token);
    assertCommonFields(cookie);
  }

  @Test
  void shouldBuildMfaSessionIdTokenCookie() {
    var token = "my-mfa-session-id";

    var cookie = factory.buildMfaSessionIdCookie(token);

    assertThat(cookie.getName()).isEqualTo(MFA_SESSION_ID);
    assertThat(cookie.getValue()).isEqualTo(token);
    assertCommonFields(cookie);
  }

  @Test
  void shouldBuildAllSessionRemovalCookies() {
    var cookies = factory.buildAllRemovalCookies();

    assertThat(cookies)
        .map(Cookie::getName)
        .containsExactly(
            PASSWORD_SESSION_ID, MFA_SESSION_ID, ID_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN);
    cookies.forEach(this::assertRemovalCookie);
  }

  private void assertRemovalCookie(Cookie cookie) {
    assertCommonFields(cookie);
    assertThat(cookie.getValue()).isNull();
    assertThat(cookie.getMaxAge()).isZero();
  }

  private void assertCommonFields(Cookie cookie) {
    assertThat(cookie.isHttpOnly()).isTrue();
    assertThat(cookie.getSecure()).isTrue();
    assertThat(cookie.getPath()).isEqualTo("/api");
  }
}
