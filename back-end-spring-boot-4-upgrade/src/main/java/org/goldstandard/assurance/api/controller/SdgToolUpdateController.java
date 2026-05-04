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
@RequestMapping("/sdg-tool-updates/projects")
public class SdgToolUpdateController {

  private final ExternalUpdateService externalUpdateService;

  @RolesAllowed({ADMIN_ROLE})
  @PostMapping("{projectId}")
  public ResponseEntity<Void> publishProjectToSdgTool(@PathVariable Integer projectId) {
    externalUpdateService.publishToSdgTool(projectId);
    return ResponseEntity.ok().build();
  }

  @RolesAllowed({ADMIN_ROLE})
  @PostMapping
  public ResponseEntity<Void> processSdgUpdates() {
    externalUpdateService.processSdgUpdates();
    return ResponseEntity.ok().build();
  }
}
