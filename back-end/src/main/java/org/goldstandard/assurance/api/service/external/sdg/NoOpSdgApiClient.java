package org.goldstandard.assurance.api.service.external.sdg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpSdgApiClient implements SdgApiClient {

  @Override
  public void updateProject(SdgProject project) {
    log.warn("no op sdg api client configured, would have sent sdg payload");
  }
}
