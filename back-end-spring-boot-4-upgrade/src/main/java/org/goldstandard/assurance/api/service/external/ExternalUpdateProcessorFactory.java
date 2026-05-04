package org.goldstandard.assurance.api.service.external;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.repository.ExternalSystemProjectUpdateRepository;
import org.goldstandard.assurance.api.repository.system.ProcessRepository;
import org.goldstandard.assurance.api.service.process.ProcessRunPolicy;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExternalUpdateProcessorFactory {

  private final Clock clock;
  private final ProcessRepository processRepository;
  private final ExternalSystemProjectUpdateRepository updateRepository;
  private final ExternalUpdaterFactory externalUpdaterFactory;

  public ExternalUpdateProcessor build(ExternalUpdateConfig config) {
    var processCode = toProcessCode(config.getSystem());
    var interval = config.getInterval();
    return ExternalUpdateProcessor.builder()
        .processCode(processCode)
        .runPolicy(new ProcessRunPolicy(processRepository, clock, interval, processCode))
        .processService(new ProcessService(processRepository, clock))
        .updateFinder(new ExternalUpdateFinder(config, clock, updateRepository))
        .failureHandler(new ExternalUpdateFailureHandler(config))
        .externalUpdater(externalUpdaterFactory.build(config))
        .build();
  }

  private static Process.ProcessCode toProcessCode(ExternalSystem system) {
    return switch (system) {
      case REGISTRY -> Process.ProcessCode.REGISTRY_UPDATE;
      case SDG_TOOL -> Process.ProcessCode.SDG_UPDATE;
      case D365 -> Process.ProcessCode.D365_UPDATE;
    };
  }
}
