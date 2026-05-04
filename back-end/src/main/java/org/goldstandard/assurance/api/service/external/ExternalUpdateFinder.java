package org.goldstandard.assurance.api.service.external;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdate;
import org.goldstandard.assurance.api.repository.ExternalSystemProjectUpdateRepository;
import org.springframework.data.domain.Page;

@RequiredArgsConstructor
@Slf4j
public class ExternalUpdateFinder {

  private final ExternalUpdateConfig config;
  private final Clock clock;
  private final ExternalSystemProjectUpdateRepository repository;

  public Page<ExternalSystemProjectUpdate> getPageOfUpdates() {
    var cutoff = calculateCutoff();
    var pageSize = config.getMaxRequests();
    var system = config.getSystem();
    var page = repository.getPageOfUpdates(pageSize, cutoff, system);
    log.info(
        "found {} events for system {} using page size {} with cutoff {}",
        page.getTotalElements(),
        system,
        pageSize,
        cutoff);
    return page;
  }

  private ZonedDateTime calculateCutoff() {
    return ZonedDateTime.now(clock).minus(config.getRetryInterval());
  }
}
