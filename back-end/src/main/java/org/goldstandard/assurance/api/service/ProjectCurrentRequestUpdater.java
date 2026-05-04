package org.goldstandard.assurance.api.service;

import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
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
    var newCurrentRequestId =
        getCurrentRequestExcludingNotRequestedStatus(project.getId())
            .map(Request::getId)
            .orElse(null);
    log.info(
        "project {} {} replacing original current request id {} with {}",
        project.getId(),
        project.getGsid(),
        originalCurrentRequestId,
        newCurrentRequestId);
    project.setCurrentRequestId(newCurrentRequestId);
  }

  private Optional<Request> getCurrentRequestExcludingNotRequestedStatus(Integer projectId) {
    return requestFinder.getByProjectId(projectId).stream()
        .filter(Request::hasActiveOrCompletedCurrentStatus)
        .max(Comparator.comparing(Request::getIndex));
  }
}
