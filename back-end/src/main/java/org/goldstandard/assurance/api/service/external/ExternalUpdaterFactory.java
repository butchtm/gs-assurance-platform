package org.goldstandard.assurance.api.service.external;

import java.time.Clock;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.repository.ExternalSystemProjectUpdateRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExternalUpdaterFactory {

  private final Clock clock;
  private final ExternalSystemProjectUpdateRepository updateRepository;
  private final ExternalUpdateCommandFactory commandFactory;
  private final Collection<ExternalUpdateConfig> configs;

  public Collection<ExternalUpdater> buildAll() {
    return configs.stream().map(this::build).toList();
  }

  public ExternalUpdater build(ExternalUpdateConfig config) {
    return ExternalUpdater.builder()
        .command(commandFactory.build(config.getSystem()))
        .persistor(new ExternalSystemProjectUpdatePersistor(config, clock, updateRepository))
        .build();
  }
}
