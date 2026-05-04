package org.goldstandard.assurance.api.service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieService {

  private final CookieFactory factory;
  private final CookieFinder finder;

  public Cookie buildPasswordSessionIdCookie(String sessionId) {
    return factory.buildPasswordSessionIdCookie(sessionId);
  }

  public Cookie buildMfaSessionIdCookie(String sessionId) {
    return factory.buildMfaSessionIdCookie(sessionId);
  }

  public void populateCookies(AuthResultDTO result, HttpServletResponse response) {
    response.addCookie(factory.buildIdTokenCookie(result.getIdToken()));
    response.addCookie(factory.buildAccessTokenCookie(result.getAccessToken()));
    result.getRefreshToken().map(factory::buildRefreshTokenCookie).ifPresent(response::addCookie);
  }

  public String forceFindRefreshTokenValue(HttpServletRequest request) {
    return finder.forceFindRefreshTokenValue(request.getCookies());
  }

  public String forceFindPasswordSessionIdValue(HttpServletRequest request) {
    return finder.forceFindPasswordSessionIdValue(request.getCookies());
  }

  public String forceFindMfaSessionIdValue(HttpServletRequest request) {
    return finder.forceFindMfaSessionIdValue(request.getCookies());
  }

  public Optional<String> findAccessTokenValue(HttpServletRequest request) {
    return finder.findAccessTokenValue(request.getCookies());
  }

  public Optional<String> findRefreshTokenValue(HttpServletRequest request) {
    return finder.findRefreshTokenValue(request.getCookies());
  }

  public void removeCookies(HttpServletResponse response) {
    factory.buildAllRemovalCookies().forEach(response::addCookie);
  }

  public void removePasswordSessionIdCookie(HttpServletResponse response) {
    response.addCookie(factory.buildPasswordSessionIdRemovalCookie());
  }

  public void removeMfaSessionIdCookie(HttpServletResponse response) {
    response.addCookie(factory.buildMfaSessionIdRemovalCookie());
  }
}
