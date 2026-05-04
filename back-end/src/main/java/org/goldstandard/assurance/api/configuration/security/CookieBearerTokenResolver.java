package org.goldstandard.assurance.api.configuration.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.auth.AuthResultDTO;
import org.goldstandard.assurance.api.service.auth.AuthService;
import org.goldstandard.assurance.api.service.auth.CookieService;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieBearerTokenResolver implements BearerTokenResolver {
  private final AuthService authService;
  private final CookieService cookieService;

  @Override
  public String resolve(HttpServletRequest request) {
    if (isForPublicUrl(request)) {
      return null;
    }
    return extractAccessTokenFromCookies(request).orElse(null);
  }

  private Optional<String> extractAccessTokenFromCookies(HttpServletRequest request) {
    var accessToken = cookieService.findAccessTokenValue(request);
    if (accessToken.isPresent()) {
      return refreshIfExpired(request, accessToken.get());
    }
    return accessToken;
  }

  private Optional<String> refreshIfExpired(HttpServletRequest request, String accessToken) {
    if (!authService.isExpired(accessToken)) {
      return Optional.of(accessToken);
    }
    return refreshToken(request);
  }

  private Optional<String> refreshToken(HttpServletRequest request) {
    var refreshToken = cookieService.forceFindRefreshTokenValue(request);
    var result = authService.refreshToken(refreshToken);
    if (!result.isSuccessful()) {
      return Optional.empty();
    }
    var refreshedAccessToken = result.getAccessToken();
    authService.findUserByToken(refreshedAccessToken);
    populateCookiesIfPossible(result);
    return Optional.of(refreshedAccessToken);
  }

  private void populateCookiesIfPossible(AuthResultDTO result) {
    var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (Objects.isNull(requestAttributes)) {
      return;
    }
    var response = requestAttributes.getResponse();
    if (Objects.isNull(response)) {
      return;
    }
    cookieService.populateCookies(result, response);
  }

  private boolean isForPublicUrl(HttpServletRequest request) {
    return request.getServletPath().startsWith("/public/");
  }
}
