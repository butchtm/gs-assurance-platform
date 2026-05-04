package org.goldstandard.assurance.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class CorrelationIdMdcPopulatorFilter extends OncePerRequestFilter {

  private static final String CORRELATION_ID_FIELD_NAME = "x-correlation-id";

  private final Supplier<String> correlationIdSupplier;
  private final Predicate<HttpServletRequest> shouldNotFilter;

  public CorrelationIdMdcPopulatorFilter() {
    this(() -> UUID.randomUUID().toString(), new ShouldNotFilterRequestPredicate());
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    populateCorrelationId(request);
    chain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return shouldNotFilter.test(request);
  }

  private void populateCorrelationId(HttpServletRequest request) {
    String correlationId = getOrCreateCorrelationId(request);
    MDC.put(CORRELATION_ID_FIELD_NAME, correlationId);
  }

  private String getOrCreateCorrelationId(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(CORRELATION_ID_FIELD_NAME))
        .orElseGet(correlationIdSupplier);
  }
}
