package org.goldstandard.assurance.api.service.external;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.RegistryUpdateConfiguration;
import org.goldstandard.assurance.api.configuration.SdgUpdateConfiguration;
import org.goldstandard.assurance.api.entity.Project;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ExternalUpdateExecutor {

  private final RegistryUpdateConfiguration registryConfig;
  private final SdgUpdateConfiguration sdgConfig;
  private final ThreadPoolTaskExecutor taskExecutor;
  private final ExternalUpdaterFactory updaterFactory;

  public void executeAsync(Collection<Project> projects) {
    projects.stream().map(Project::getId).forEach(this::executeAsync);
  }

  public void executeAsync(Integer projectId) {
    buildUpdaters().forEach(updater -> executeAsync(updater, projectId));
  }

  public void executeRegistrySync(Integer projectId) {
    executeSync(projectId, registryConfig);
  }

  public void executeSdgToolSync(Integer projectId) {
    executeSync(projectId, sdgConfig);
  }

  private void executeSync(Integer projectId, ExternalUpdateConfig config) {
    var updater = updaterFactory.build(config);
    updater.update(projectId);
  }

  private Collection<ExternalUpdater> buildUpdaters() {
    return updaterFactory.buildAll();
  }

  private void executeAsync(ExternalUpdater updater, Integer projectId) {
    try {
      taskExecutor.execute(() -> updater.update(projectId));
    } catch (TaskRejectedException e) {
      log.warn(
          "external update task rejected, persisting to database {} {}",
          projectId,
          e.getMessage(),
          e);
      updater.persistUpdateIfRequired(projectId);
    }
  }
}
