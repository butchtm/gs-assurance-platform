package org.goldstandard.assurance.api.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProjectAccessPolicy {

  private final PermissionsService permissionsService;
  private final AssignmentPermissionsService assignmentPermissionsService;

  public void validateCanUpdateProjects(Collection<Project> projects) {
    projects.forEach(this::validateCanUpdateProject);
  }

  public void validateCanUpdateProject(Project project) {
    if (canUpdateProject(project)) {
      return;
    }
    throw new PermissionException(
        "User not permitted to update project project %s", project.getGsid());
  }

  public void validateCanCreateProjectForAccount(Integer accountId) {
    if (canCreateProjectForAccount(accountId)) {
      return;
    }
    throw new PermissionException("User not permitted to create project for account %d", accountId);
  }

  public void validateUserCanViewProject(Project project) {
    if (!project.isNew()) {
      log.debug("non new projects are publicly visible so anyone can view");
      return;
    }
    if (permissionsService.isAdminOrFinanceOrArmOrTacOrRegistry()) {
      log.debug("current user is admin, finance, arm, tac or registry");
      return;
    }
    if (permissionsService.isProjectOwner(project)) {
      log.debug("current users owns project {}", project.getId());
      return;
    }
    if (permissionsService.isReviewerOrVVBOrRegistry()
        && hasRequestsAssignedToUserAccount(project)) {
      log.debug("current user is reviewer or VVB and is has request assigned to account");
      return;
    }
    throw new PermissionException("user not permitted to view project %s", project.getGsid());
  }

  private boolean canCreateProjectForAccount(Integer accountId) {
    if (permissionsService.isAdminOrArm()) {
      return true;
    }
    return permissionsService.isProjectDeveloper()
        && permissionsService.currentUserInAccount(accountId);
  }

  private boolean canUpdateProject(Project project) {
    if (permissionsService.isProjectOwner(project) && project.isNew()) {
      return true;
    }
    return permissionsService.isAdminOrArm();
  }

  private boolean hasRequestsAssignedToUserAccount(Project project) {
    return assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project);
  }
}
