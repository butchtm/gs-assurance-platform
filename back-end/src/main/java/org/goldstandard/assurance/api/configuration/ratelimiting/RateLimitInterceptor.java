package org.goldstandard.assurance.api.configuration.ratelimiting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.filter.RemoteAddressExtractor;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.goldstandard.assurance.api.service.pub.PublicRateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

  private final PublicRateLimitService service;
  private final RemoteAddressExtractor extractor;
  private final AuthenticatedUserSupplier userSupplier;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String remoteAddress = extractor.extractRemoteAddress(request);
    if (StringUtils.isEmpty(remoteAddress)) {
      response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing IP Address");
      return false;
    }

    var username = userSupplier.getAuthenticatedUsername();
    if (username.isPresent()) {
      log.debug("not rate limiting request from authenticated user {}", username);
      return true;
    }

    var originatingAddress = toOriginatingIfMultiple(remoteAddress);
    if (service.isWhitelisted(originatingAddress)) {
      log.info("not rate limiting request from whitelisted ip {}", originatingAddress);
      return true;
    }

    var bucket = service.resolveBucket(originatingAddress);
    var probe = bucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
      return true;
    } else {
      log.warn(
          "response rate limited for request {} {} from originating ip {}",
          request.getMethod(),
          request.getRequestURI(),
          originatingAddress);
      long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
      response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
      response.setHeader("Content-Type", "application/json");
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      return false;
    }
  }

  private static String toOriginatingIfMultiple(String addresses) {
    var first = Stream.of(addresses.split(",")).map(String::trim).findFirst().orElse(addresses);
    log.debug("returning originating address {} from original value {}", first, addresses);
    return first;
  }
}
