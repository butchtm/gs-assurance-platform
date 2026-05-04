package org.goldstandard.assurance.api.service.external;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.exception.NotFoundException;

@Builder
@Slf4j
public class ExternalUpdater {

  private final ExternalUpdateCommand command;
  private final ExternalSystemProjectUpdatePersistor persistor;

  public void update(Integer projectId) {
    try {
      command.execute(projectId);
      persistor.deleteAnyExistingUpdates(projectId);
    } catch (ExternalApiClientException e) {
      log.error("external update failed {}", e.getMessage(), e);
      persistUpdateIfRequired(projectId);
      throw e;
    } catch (NotFoundException e) {
      log.error("external update failed {}", e.getMessage(), e);
      persistor.deleteAnyExistingUpdates(projectId);
      throw e;
    }
  }

  public void persistUpdateIfRequired(int projectId) {
    persistor.persistUpdateIfRequired(projectId);
  }
}
