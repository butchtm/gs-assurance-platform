package org.goldstandard.assurance.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class RequestMdcPopulatorFilter extends OncePerRequestFilter {

  private final Clock clock;
  private final Predicate<HttpServletRequest> shouldNotFilter;

  public RequestMdcPopulatorFilter() {
    this(Clock.systemUTC(), new ShouldNotFilterRequestPredicate());
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var start = clock.instant();
    try {
      populateValues(request);
      chain.doFilter(request, response);
    } finally {
      long duration = millisBetweenNowAnd(start);
      populateValues(response, duration);
      log.info(toCompletionMessage(request, response, duration));
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return shouldNotFilter.test(request);
  }

  private void populateValues(HttpServletRequest request) {
    MDC.put("request-method", request.getMethod());
    MDC.put("request-uri", request.getRequestURI());
    MDC.put("request-remote-address", request.getRemoteAddr());
    MDC.put("request-forwarded-for-address", request.getHeader("X-Forwarded-For"));
    MDC.put("user-agent", request.getHeader("User-Agent"));
  }

  private void populateValues(HttpServletResponse response, long duration) {
    MDC.put("request-duration", Long.toString(duration));
    MDC.put("request-status", Integer.toString(response.getStatus()));
  }

  private long millisBetweenNowAnd(Instant start) {
    return Duration.between(start, clock.instant()).toMillis();
  }

  private String toCompletionMessage(
      HttpServletRequest request, HttpServletResponse response, long duration) {
    return String.format(
        "took %dms for %s %s to return status %d",
        duration, request.getMethod(), request.getRequestURI(), response.getStatus());
  }
}
