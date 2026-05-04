package org.goldstandard.assurance.api.service.external.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.RegistryUpdateConfiguration;
import org.goldstandard.assurance.api.service.external.ExternalUpdateProcessor;
import org.goldstandard.assurance.api.service.external.ExternalUpdateProcessorFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class RegistryUpdateProcessor {

  private final RegistryUpdateConfiguration config;
  private final ExternalUpdateProcessorFactory processorFactory;

  @Scheduled(fixedDelayString = "${registry.update.interval}")
  public void processUpdatesIfRequired() {
    var processor = buildProcessor();
    processor.processUpdatesIfRequired();
  }

  public void processUpdates() {
    var processor = buildProcessor();
    processor.processUpdates();
  }

  private ExternalUpdateProcessor buildProcessor() {
    return processorFactory.build(config);
  }
}
