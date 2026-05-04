package org.goldstandard.assurance.api.service.external.sdg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.SdgUpdateConfiguration;
import org.goldstandard.assurance.api.service.external.ExternalUpdateProcessor;
import org.goldstandard.assurance.api.service.external.ExternalUpdateProcessorFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class SdgUpdateProcessor {

  private final SdgUpdateConfiguration config;
  private final ExternalUpdateProcessorFactory processorFactory;

  @Scheduled(fixedDelayString = "${sdg.tool.update.interval}")
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
