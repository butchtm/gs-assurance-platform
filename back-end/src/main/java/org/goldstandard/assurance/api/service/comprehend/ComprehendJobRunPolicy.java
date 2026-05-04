package org.goldstandard.assurance.api.service.comprehend;

import static org.goldstandard.assurance.api.entity.system.Process.ProcessCode.PII_SCANNING;

import java.time.Clock;
import java.time.Duration;
import org.goldstandard.assurance.api.configuration.ComprehendJobServiceConfiguration;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.goldstandard.assurance.api.service.process.ProcessRunPolicy;
import org.springframework.stereotype.Component;

@Component
public class ComprehendJobRunPolicy extends ProcessRunPolicy {

  public ComprehendJobRunPolicy(
      ProcessRepository repository, Clock clock, ComprehendJobServiceConfiguration config) {
    super(repository, clock, Duration.ofSeconds(config.getSyncInterval()), PII_SCANNING);
  }
}
