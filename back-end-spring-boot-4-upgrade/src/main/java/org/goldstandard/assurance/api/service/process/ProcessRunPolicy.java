package org.goldstandard.assurance.api.service.process;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.system.Process.ProcessCode;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;

@Slf4j
@RequiredArgsConstructor
public class ProcessRunPolicy {

  private final ProcessRepository repository;
  private final Clock clock;
  private final Duration interval;
  private final ProcessCode code;

  public boolean shouldRun() {
    var latestProcess = repository.findLatestForProcessCode(code.name());
    if (Objects.isNull(latestProcess)) {
      log.info("{} process not found so will be run", code.name());
      return true;
    }
    var cutoff = calculateCutoff();
    var shouldRun = latestProcess.updatedBefore(cutoff.withZoneSameInstant(ZoneOffset.UTC));
    log.debug("{} process should run {}", code, shouldRun);
    return shouldRun;
  }

  private ZonedDateTime calculateCutoff() {
    var now = ZonedDateTime.now(clock);
    var cutoff = now.minus(interval);
    log.debug(
        "calculated cutoff {} from now {} minus {} interval for {} process",
        cutoff,
        now,
        interval,
        code);
    return cutoff;
  }
}
