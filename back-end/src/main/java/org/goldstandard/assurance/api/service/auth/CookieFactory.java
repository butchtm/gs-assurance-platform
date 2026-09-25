package org.goldstandard.assurance.api.service.auth;

import static org.goldstandard.assurance.api.service.auth.CookieName.ACCESS_TOKEN;
import static org.goldstandard.assurance.api.service.auth.CookieName.ID_TOKEN;
import static org.goldstandard.assurance.api.service.auth.CookieName.MFA_SESSION_ID;
import static org.goldstandard.assurance.api.service.auth.CookieName.PASSWORD_SESSION_ID;
import static org.goldstandard.assurance.api.service.auth.CookieName.REFRESH_TOKEN;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Component
public class CookieFactory {

  @Value("${local-auth.enabled:false}")
  private boolean localAuthEnabled;

  public Cookie buildIdTokenCookie(String idToken) {
    return buildHttpCookie(ID_TOKEN, idToken);
  }

  public Cookie buildAccessTokenCookie(String accessToken) {
    return buildHttpCookie(ACCESS_TOKEN, accessToken);
  }

  public Cookie buildRefreshTokenCookie(String refreshToken) {
    return buildHttpCookie(REFRESH_TOKEN, refreshToken);
  }

  public Cookie buildPasswordSessionIdCookie(String sessionId) {
    return buildHttpCookie(PASSWORD_SESSION_ID, sessionId);
  }

  public Cookie buildMfaSessionIdCookie(String sessionId) {
    return buildHttpCookie(MFA_SESSION_ID, sessionId);
  }

  public Collection<Cookie> buildAllRemovalCookies() {
    return buildRemovalCookies(
        PASSWORD_SESSION_ID, MFA_SESSION_ID, ID_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN);
  }

  public Cookie buildPasswordSessionIdRemovalCookie() {
    return buildRemovalCookie(PASSWORD_SESSION_ID);
  }

  public Cookie buildMfaSessionIdRemovalCookie() {
    return buildRemovalCookie(MFA_SESSION_ID);
  }

  public Cookie buildRefreshTokenRemovalCookie() {
    return buildRemovalCookie(REFRESH_TOKEN);
  }

  private Collection<Cookie> buildRemovalCookies(String... names) {
    return Arrays.stream(names).map(this::buildRemovalCookie).toList();
  }

  private Cookie buildHttpCookie(String name, String value) {
    var cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(isSecureCookie());
    cookie.setPath("/api");
    return cookie;
  }

  private Cookie buildRemovalCookie(String cookieName) {
    var cookie = new Cookie(cookieName, null);
    cookie.setHttpOnly(true);
    cookie.setSecure(isSecureCookie());
    cookie.setPath("/api");
    cookie.setMaxAge(0);
    return cookie;
  }

  private boolean isSecureCookie() {
    if (!localAuthEnabled) {
      return true;
    }
    if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
      return true;
    }
    var request = attrs.getRequest();
    var host = request.getServerName();
    return request.isSecure()
        || !("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host));
  }
}
