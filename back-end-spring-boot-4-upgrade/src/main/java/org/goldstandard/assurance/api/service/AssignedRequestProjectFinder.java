package org.goldstandard.assurance.api.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignedRequestProjectFinder {

  private final AssignedRequestFinder finder;
  private final ProjectService projectService;

  public List<Project> getAssignedRequestProjects() {
    var projectIds =
        finder.getAssignedRequests().stream()
            .map(AssignedRequestView::getProjectId)
            .collect(Collectors.toSet());
    return projectService.getByIds(projectIds);
  }
}
