package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.external.ExternalUpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registry-updates/projects")
public class RegistryUpdateController {

  private final ExternalUpdateService externalUpdateService;

  @RolesAllowed({ADMIN_ROLE})
  @PostMapping("{projectId}")
  public ResponseEntity<Void> publishProjectToRegistry(@PathVariable Integer projectId) {
    externalUpdateService.publishToRegistry(projectId);
    return ResponseEntity.ok().build();
  }

  @RolesAllowed({ADMIN_ROLE})
  @PostMapping
  public ResponseEntity<Void> processRegistryUpdates() {
    externalUpdateService.processRegistryUpdates();
    return ResponseEntity.ok().build();
  }
}
