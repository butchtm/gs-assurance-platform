package org.goldstandard.assurance.api.service.external.registry;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;

@RequiredArgsConstructor
public class RegistryUiLinkFactory {

  private final String registryUiHost;

  public Optional<String> buildProjectLinkUrl(Project project) {
    return project.getRegistryId().map(this::toProjectLinkUrl);
  }

  private String toProjectLinkUrl(Integer registryId) {
    return String.format("%s/projects/details/%d", registryUiHost, registryId);
  }
}
