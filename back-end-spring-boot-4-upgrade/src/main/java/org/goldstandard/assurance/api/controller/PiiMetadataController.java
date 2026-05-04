package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;

import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.PiiActionRequestDTO;
import org.goldstandard.assurance.api.service.pii.PiiMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
@RestController
@RequiredArgsConstructor
@RequestMapping("/pii-metadata")
public class PiiMetadataController {

  private final PiiMetadataService metadataService;

  @PostMapping("/action")
  public ResponseEntity<Void> submitPiiAction(@RequestBody PiiActionRequestDTO piiActionRequest) {
    metadataService.update(piiActionRequest);
    return ResponseEntity.ok().build();
  }

  @PostMapping("{projectId}/action/not-found")
  public ResponseEntity<Void> updateProjectPiiToNotFound(
      @PathVariable Integer projectId, @RequestBody Collection<String> projectFields) {
    metadataService.updateProjectFieldsToNotFound(projectId, projectFields);
    return ResponseEntity.ok().build();
  }
}
