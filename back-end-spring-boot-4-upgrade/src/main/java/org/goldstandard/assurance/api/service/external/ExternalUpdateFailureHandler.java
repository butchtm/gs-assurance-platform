package org.goldstandard.assurance.api.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ExternalUpdateFailureHandler {

  private final ExternalUpdateConfig config;

  public void abortIfRequired(ExternalApiClientException e) {
    var abortCodes = config.getAbortCodes();
    boolean abort = e.getStatusCode().map(code -> abortCodes.contains(code.value())).orElse(false);
    if (abort) {
      throw new ExternalUpdateAbortException(config.getSystem(), e);
    }
  }
}
