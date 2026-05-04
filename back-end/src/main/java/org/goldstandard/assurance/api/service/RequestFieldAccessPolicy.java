package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestFieldAccessPolicy {

  private final PermissionsService permissionsService;

  public boolean canViewActivity() {
    return permissionsService.isAdminOrArmOrReviewer() || permissionsService.isSupport();
  }

  public boolean canViewSubmittedBy() {
    return permissionsService.isAdminOrArm();
  }

  public boolean canViewFastTrackedFlag(Project project) {
    return permissionsService.isAdminOrArm()
        || permissionsService.isProjectOwner(project)
        || permissionsService.isSupport();
  }

  public boolean canViewProjectRisk() {
    return permissionsService.isAdminOrArm();
  }
}
