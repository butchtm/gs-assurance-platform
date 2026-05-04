package org.goldstandard.assurance.api.service.external.registry;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpRegistryApiClient implements RegistryApiClient {

  @Override
  public Optional<Integer> updateProject(RegistryProject project) {
    log.warn(
        "no op registry api client configured, would have sent registry project {}",
        project.getId());
    return Optional.empty();
  }
}
