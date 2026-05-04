package org.goldstandard.assurance.api.service.notification;

import static org.goldstandard.assurance.api.entity.system.Process.ProcessCode.CONSULTATION_NOTIFICATION;

import java.time.Clock;
import java.time.Duration;
import org.goldstandard.assurance.api.configuration.ConsultationNotificationProcessConfiguration;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.goldstandard.assurance.api.service.process.ProcessRunPolicy;
import org.springframework.stereotype.Component;

@Component
public class ConsultationNotificationJobRunPolicy extends ProcessRunPolicy {

  public ConsultationNotificationJobRunPolicy(
      ProcessRepository repository,
      Clock clock,
      ConsultationNotificationProcessConfiguration config) {
    super(
        repository, clock, Duration.ofSeconds(config.getSyncInterval()), CONSULTATION_NOTIFICATION);
  }
}
