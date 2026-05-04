package org.goldstandard.assurance.api.service.external.registry;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.service.external.ExternalUpdateCommand;

@Slf4j
@Builder
public class RegistryUpdateCommand implements ExternalUpdateCommand {

  private final RegistryProjectConverter converter;
  private final RegistryApiClient client;
  private final ProjectRepository repository;

  @Override
  public void execute(Integer projectId) {
    var registryProject = converter.toRegistryProject(projectId);
    var registryId = client.updateProject(registryProject);
    registryId.ifPresent(id -> saveRegistryId(projectId, id));
  }

  private void saveRegistryId(Integer projectId, Integer registryId) {
    repository.updateRegistryId(projectId, registryId);
    log.info("saved registry id {} against project id {}", registryId, projectId);
  }
}
