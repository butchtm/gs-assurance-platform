package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectStatusUpdatePolicy {

  private final ProjectUpdateFinder updateFinder;
  private final ProjectFinder projectFinder;
  private final ProjectAccessPolicy accessPolicy;
  private final ProjectStatusUpdateApplier statusUpdateApplier;
  private final ProjectStatusUpdateCalculator statusCalculator;
  private final ProjectStatusUpdateDateCalculator dateCalculator;

  public Collection<Project> calculateProjectStatusUpdates(
      Integer projectId, Integer requestId, Integer projectStatusId) {
    var projects = getProjects(projectId, requestId);
    accessPolicy.validateCanUpdateProjects(projects);
    return statusUpdateApplier.forceUpdateProjectStatuses(projects, projectStatusId);
  }

  public Collection<Project> calculateProjectStatusUpdates(
      Collection<Project> projects, Request request) {
    var newProjectStatus = statusCalculator.calculateNewProjectStatus(request);
    if (newProjectStatus.isEmpty()) {
      return projects;
    }

    accessPolicy.validateCanUpdateProjects(projects);
    var updateDate = dateCalculator.calculateProjectStatusUpdateDate(request);
    return statusUpdateApplier.updateProjectStatusesIfRequired(
        projects, newProjectStatus.get(), updateDate);
  }

  private Collection<Project> getProjects(Integer projectId, Integer requestId) {
    if (Objects.isNull(requestId)) {
      return List.of(projectFinder.forceGetProjectById(projectId));
    }
    return updateFinder.toProjectsAppendingLegacyBatchProjectsIfPresent(requestId);
  }
}
