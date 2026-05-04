package org.goldstandard.assurance.api.service.external;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ExternalSystemProjectUpdate;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.service.process.ProcessRunPolicy;
import org.goldstandard.assurance.api.service.process.ProcessService;

@Builder
@Slf4j
public class ExternalUpdateProcessor {

  private final Process.ProcessCode processCode;
  private final ProcessRunPolicy runPolicy;
  private final ProcessService processService;
  private final ExternalUpdateFinder updateFinder;
  private final ExternalUpdateFailureHandler failureHandler;
  private final ExternalUpdater externalUpdater;

  public void processUpdatesIfRequired() {
    if (!runPolicy.shouldRun()) {
      log.debug("{} not required so not running", processCode);
      return;
    }
    processUpdates();
  }

  public void processUpdates() {
    var process = processService.recordProcessStarted(processCode);
    try {
      updateFinder.getPageOfUpdates().forEach(this::processUpdate);
      processService.recordProcessComplete(process);
    } catch (ExternalUpdateAbortException e) {
      log.warn("{} updates aborted", e.getMessage());
      processService.recordProcessFailed(process, e.getMessage());
    }
  }

  private void processUpdate(ExternalSystemProjectUpdate update) {
    try {
      externalUpdater.update(update.getProjectId());
    } catch (ExternalApiClientException e) {
      failureHandler.abortIfRequired(e);
    }
  }
}
