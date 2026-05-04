package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.user.UserSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserSyncController {

  private final UserSyncService service;

  @RolesAllowed(ADMIN_ROLE)
  @PostMapping("/user-synchronizations")
  public ResponseEntity<Void> synchronizeUsers() {
    service.syncUsers();
    return ResponseEntity.ok().build();
  }
}
