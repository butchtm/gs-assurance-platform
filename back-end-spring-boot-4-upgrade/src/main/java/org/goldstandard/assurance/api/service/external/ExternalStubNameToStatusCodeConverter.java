package org.goldstandard.assurance.api.service.external;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Slf4j
public class ExternalStubNameToStatusCodeConverter {

  private final HttpStatusCode defaultStatus;

  public ExternalStubNameToStatusCodeConverter() {
    this(HttpStatusCode.valueOf(500));
  }

  public Optional<HttpStatusCode> toErrorStatusCodeIfRequired(String name) {
    if (!name.contains("error")) {
      return Optional.empty();
    }
    return Optional.of(toStatusCode(name));
  }

  private HttpStatusCode toStatusCode(String name) {
    try {
      var parts = name.split("-");
      if (parts.length > 1) {
        return HttpStatusCode.valueOf(Integer.parseInt(parts[1].strip()));
      }
      return defaultStatus;
    } catch (NumberFormatException e) {
      log.debug("failed to parse status code from name {}", name, e);
      return defaultStatus;
    }
  }
}
