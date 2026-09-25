package org.goldstandard.assurance.api.configuration.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import java.util.Optional;
import org.goldstandard.assurance.api.service.auth.AuthService;
import org.goldstandard.assurance.api.service.auth.CookieService;
import org.goldstandard.assurance.api.service.auth.LocalAuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class CookieBearerTokenResolverLocalAuthTest {
  private final AuthService authService = mock(AuthService.class);
  private final CookieService cookieService = mock(CookieService.class);
  private final ObjectProvider<LocalAuthService> localAuthProvider = mock(ObjectProvider.class);
  private final CookieBearerTokenResolver resolver =
      new CookieBearerTokenResolver(authService, cookieService, localAuthProvider);

  @AfterEach
  void clearRequestContext() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void expiredLocalAccessTokenIsDiscardedWithoutCognitoRefresh() {
    var request = new MockHttpServletRequest();
    request.setServletPath("/secure/resource");
    request.setCookies(new Cookie("access-token", "expired-token"));
    var response = new MockHttpServletResponse();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
    when(cookieService.findAccessTokenValue(request)).thenReturn(Optional.of("expired-token"));
    when(authService.isExpired("expired-token")).thenReturn(true);
    when(localAuthProvider.getIfAvailable()).thenReturn(mock(LocalAuthService.class));

    var token = resolver.resolve(request);

    assertThat(token).isNull();
    verify(cookieService).removeCookies(response);
    verify(authService, never()).refreshToken(org.mockito.ArgumentMatchers.anyString());
  }
}
