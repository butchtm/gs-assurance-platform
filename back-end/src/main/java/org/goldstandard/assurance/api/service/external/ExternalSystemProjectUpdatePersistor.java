package org.goldstandard.assurance.api.service.external;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdate;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdateId;
import org.goldstandard.assurance.api.repository.ExternalSystemProjectUpdateRepository;

@RequiredArgsConstructor
@Slf4j
public class ExternalSystemProjectUpdatePersistor {

  private final ExternalUpdateConfig config;
  private final Clock clock;
  private final ExternalSystemProjectUpdateRepository repository;

  public void deleteAnyExistingUpdates(int projectId) {
    repository.deleteById(new ExternalSystemProjectUpdateId(projectId, config.getSystem().name()));
  }

  public void persistUpdateIfRequired(int projectId) {
    var system = config.getSystem();
    repository
        .findById(new ExternalSystemProjectUpdateId(projectId, system.name()))
        .ifPresentOrElse(
            this::recordRepeatFailedAttempt, () -> recordNewFailedAttempt(projectId, system));
  }

  private void recordRepeatFailedAttempt(ExternalSystemProjectUpdate update) {
    var failed = update.recordFailedAttempt(ZonedDateTime.now(clock));
    if (failed.attemptsGreaterThanOrEqualTo(config.getMaxRetries())) {
      log.error("max retries hit for project {}", failed.getProjectId());
      repository.delete(failed);
    } else {
      repository.save(failed);
    }
  }

  private void recordNewFailedAttempt(int projectId, ExternalSystem system) {
    repository.save(buildUpdate(projectId, system));
  }

  private ExternalSystemProjectUpdate buildUpdate(int projectId, ExternalSystem system) {
    var now = ZonedDateTime.now(clock);
    return ExternalSystemProjectUpdate.builder()
        .projectId(projectId)
        .createdTimestamp(now)
        .lastAttemptAt(now)
        .attempts(1)
        .destinationSystem(system.name())
        .build();
  }
}
