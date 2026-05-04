package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.CommentAccessRights;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentAccessRightsCalculator {
  private final PermissionsService permissionsService;

  public CommentAccessRights calculateAccessRights() {
    return CommentAccessRights.builder()
        .includeTac(!permissionsService.isProjectDeveloperOrVVB())
        .includeSuppressed(permissionsService.isAdminOrArm())
        .includeDeleted(permissionsService.isAdminOrArm())
        .includePrivate(permissionsService.isAdminOrArmOrTac())
        .anonymise(!permissionsService.isAdminOrArmOrTac())
        .build();
  }
}
