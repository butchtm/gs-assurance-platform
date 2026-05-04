package org.goldstandard.assurance.api.service.user;

import static org.goldstandard.assurance.api.entity.system.Process.ProcessCode.USER_SYNC;

import java.time.Clock;
import java.time.Duration;
import org.goldstandard.assurance.api.configuration.UserSyncConfiguration;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.goldstandard.assurance.api.service.process.ProcessRunPolicy;
import org.springframework.stereotype.Component;

@Component
public class UserSyncRunPolicy extends ProcessRunPolicy {

  public UserSyncRunPolicy(
      ProcessRepository repository, Clock clock, UserSyncConfiguration config) {
    super(repository, clock, Duration.ofSeconds(config.getSyncInterval()), USER_SYNC);
  }
}
