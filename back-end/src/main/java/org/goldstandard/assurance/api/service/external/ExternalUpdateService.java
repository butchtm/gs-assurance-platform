package org.goldstandard.assurance.api.service.external;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.external.registry.RegistryUpdateProcessor;
import org.goldstandard.assurance.api.service.external.sdg.SdgUpdateProcessor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalUpdateService {

  private final PermissionsService permissionsService;
  private final ExternalUpdateExecutor executor;
  private final RegistryUpdateProcessor registerUpdateProcessor;
  private final SdgUpdateProcessor sdgUpdateProcessor;

  public void publishToRegistry(Integer projectId) {
    permissionsService.validateIsAdmin();
    executor.executeRegistrySync(projectId);
  }

  public void publishToSdgTool(Integer projectId) {
    permissionsService.validateIsAdmin();
    executor.executeSdgToolSync(projectId);
  }

  public void processRegistryUpdates() {
    registerUpdateProcessor.processUpdates();
  }

  public void processSdgUpdates() {
    sdgUpdateProcessor.processUpdates();
  }
}
