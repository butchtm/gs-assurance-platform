package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestAccessPolicy {

  private final PermissionsService permissionsService;
  private final AssignmentPermissionsService assignmentPermissionsService;

  public void validateCanCreateRequest(Project project) {
    if (!canAccessProject(project)) {
      throw new PermissionException(
          "Not permitted to create request for project %d", project.getId());
    }
  }

  public void validateCanUpdateRequest(Request request) {
    var project = request.getProject();
    if (!canAccessProject(project)
        && !assignmentPermissionsService.isRequestAssignedToUserAccount(request.getId())) {
      throw new PermissionException(
          "Not permitted to update request %d for project %d", request.getId(), project.getId());
    }
  }

  private boolean canAccessProject(Project project) {
    return permissionsService.canAccessAllProjects() || permissionsService.isProjectOwner(project);
  }
}
