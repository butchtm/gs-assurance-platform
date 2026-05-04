package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ProjectIdsDTO;
import org.goldstandard.assurance.api.entity.pii.PiiScanRun;
import org.goldstandard.assurance.api.service.pii.PiiScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/project-pii-check")
public class ProjectPiiController {

  private final PiiScanService piiScanService;

  @RolesAllowed(ADMIN_ROLE)
  @PostMapping("/start")
  public ResponseEntity<Integer> startProjectPiiCheck(
      @Valid @RequestBody ProjectIdsDTO projectIds) {
    PiiScanRun scanRun = piiScanService.initiatePiiScan(projectIds);
    return ResponseEntity.accepted().body(scanRun.getId());
  }
}
