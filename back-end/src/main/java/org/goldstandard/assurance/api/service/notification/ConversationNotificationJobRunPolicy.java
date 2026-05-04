package org.goldstandard.assurance.api.service.notification;

import static org.goldstandard.assurance.api.entity.system.Process.ProcessCode.CONVERSATION_NOTIFICATION;

import java.time.Clock;
import java.time.Duration;
import org.goldstandard.assurance.api.configuration.ConversationNotificationProcessConfiguration;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.goldstandard.assurance.api.service.process.ProcessRunPolicy;
import org.springframework.stereotype.Component;

@Component
public class ConversationNotificationJobRunPolicy extends ProcessRunPolicy {

  public ConversationNotificationJobRunPolicy(
      ProcessRepository repository,
      Clock clock,
      ConversationNotificationProcessConfiguration config) {
    super(
        repository, clock, Duration.ofSeconds(config.getSyncInterval()), CONVERSATION_NOTIFICATION);
  }
}
