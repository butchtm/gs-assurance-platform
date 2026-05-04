package org.goldstandard.assurance.api.service.auth;

import static org.goldstandard.assurance.api.service.auth.CookieName.ACCESS_TOKEN;
import static org.goldstandard.assurance.api.service.auth.CookieName.MFA_SESSION_ID;
import static org.goldstandard.assurance.api.service.auth.CookieName.PASSWORD_SESSION_ID;
import static org.goldstandard.assurance.api.service.auth.CookieName.REFRESH_TOKEN;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.springframework.stereotype.Component;

@Component
public class CookieFinder {

  public String forceFindRefreshTokenValue(Cookie[] cookies) {
    return forceFindCookieValueByName(cookies, REFRESH_TOKEN);
  }

  public Optional<String> findRefreshTokenValue(Cookie[] cookies) {
    return findCookieValueByName(cookies, REFRESH_TOKEN);
  }

  public String forceFindPasswordSessionIdValue(Cookie[] cookies) {
    return forceFindCookieValueByName(cookies, PASSWORD_SESSION_ID);
  }

  public String forceFindMfaSessionIdValue(Cookie[] cookies) {
    return forceFindCookieValueByName(cookies, MFA_SESSION_ID);
  }

  public Optional<String> findAccessTokenValue(Cookie[] cookies) {
    return findCookieValueByName(cookies, ACCESS_TOKEN);
  }

  public String forceFindCookieValueByName(Cookie[] cookies, String name) {
    return findCookieValueByName(cookies, name)
        .orElseThrow(
            () ->
                new PermissionException("Cookie value not found for on request for name %s", name));
  }

  public Optional<String> findCookieValueByName(Cookie[] cookies, String name) {
    return findFirstCookieByName(cookies, name)
        .map(Cookie::getValue)
        .filter(StringUtils::isNotEmpty);
  }

  private static Optional<Cookie> findFirstCookieByName(Cookie[] cookies, String name) {
    return filterByName(cookies, name).findFirst();
  }

  private static Stream<Cookie> filterByName(Cookie[] cookies, String name) {
    if (Objects.isNull(cookies)) {
      return Stream.empty();
    }
    return Arrays.stream(cookies).filter(cookie -> name.equals(cookie.getName()));
  }
}
