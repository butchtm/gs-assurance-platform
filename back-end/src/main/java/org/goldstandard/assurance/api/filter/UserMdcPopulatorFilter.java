package org.goldstandard.assurance.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class UserMdcPopulatorFilter extends OncePerRequestFilter {

  private static final String USERNAME_KEY = "username";

  private final AuthenticatedUserSupplier userSupplier;
  private final Predicate<HttpServletRequest> shouldNotFilter;

  public UserMdcPopulatorFilter(AuthenticatedUserSupplier userSupplier) {
    this(userSupplier, new ShouldNotFilterRequestPredicate());
  }

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String username = userSupplier.getAuthenticatedUsername().orElse("anonymous");
    populateInMdc(username);
    chain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return shouldNotFilter.test(request);
  }

  private static void populateInMdc(String username) {
    MDC.put(USERNAME_KEY, username);
  }
}
