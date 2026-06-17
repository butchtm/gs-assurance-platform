package org.goldstandard.assurance.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProjectCurrentRequestUpdater {

  private final ProjectCurrentRequestUpdatePolicy policy;
  private final RequestFinder requestFinder;

  public void updateCurrentRequestIfRequired(Project project) {
    if (policy.shouldUpdateCurrentRequest(project)) {
      updateCurrentRequestId(project);
    }
  }

  private void updateCurrentRequestId(Project project) {
    var originalCurrentRequestId = project.getCurrentRequestId();
    var newCurrentRequestId = getCurrentRequestIdExcludingNotRequestedStatus(project.getId());
    log.info(
        "project {} {} replacing original current request id {} with {}",
        project.getId(),
        project.getGsid(),
        originalCurrentRequestId,
        newCurrentRequestId);
    newCurrentRequestId.ifPresent(project::setCurrentRequestId);
  }

  private Optional<Integer> getCurrentRequestIdExcludingNotRequestedStatus(Integer projectId) {
    return requestFinder.findCurrentRequestId(projectId);
  }
}
