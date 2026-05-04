package org.goldstandard.assurance.api.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RemoteAddressExtractor {

  public String extractRemoteAddress(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    String remoteAddress = request.getRemoteAddr();
    log.debug("got remote address {} and forwarded for {}", remoteAddress, forwardedFor);
    if (StringUtils.isNotEmpty(forwardedFor)) {
      return forwardedFor;
    }
    return remoteAddress;
  }
}
