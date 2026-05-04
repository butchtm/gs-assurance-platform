package org.goldstandard.assurance.api.service.external;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.service.external.d365.D365UpdateCommand;
import org.goldstandard.assurance.api.service.external.registry.RegistryApiClient;
import org.goldstandard.assurance.api.service.external.registry.RegistryProjectConverter;
import org.goldstandard.assurance.api.service.external.registry.RegistryUpdateCommand;
import org.goldstandard.assurance.api.service.external.sdg.SdgApiClient;
import org.goldstandard.assurance.api.service.external.sdg.SdgProjectConverter;
import org.goldstandard.assurance.api.service.external.sdg.SdgUpdateCommand;
import org.goldstandard.assurance.api.service.finance.FinanceService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExternalUpdateCommandFactory {

  private final RegistryProjectConverter registryConverter;
  private final RegistryApiClient registryClient;

  private final SdgProjectConverter sdgConverter;
  private final SdgApiClient sdgClient;

  private final FinanceService financeService;
  private final ProjectRepository projectRepository;

  public ExternalUpdateCommand build(ExternalSystem system) {
    return switch (system) {
      case REGISTRY -> buildRegistry();
      case SDG_TOOL -> buildSdg();
      case D365 -> buildD365();
    };
  }

  public ExternalUpdateCommand buildRegistry() {
    return RegistryUpdateCommand.builder()
        .converter(registryConverter)
        .client(registryClient)
        .repository(projectRepository)
        .build();
  }

  public ExternalUpdateCommand buildSdg() {
    return SdgUpdateCommand.builder().converter(sdgConverter).client(sdgClient).build();
  }

  public ExternalUpdateCommand buildD365() {
    return new D365UpdateCommand(financeService);
  }
}
