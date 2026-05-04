package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestRoundUpdateDTO;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.service.RequestRoundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RequestRoundController {

  private final RequestRoundService requestRoundService;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @Transactional
  @PutMapping("/requests/{requestId}/round/{roundIndex}")
  public ResponseEntity<RequestRound> updateRequestRound(
      @PathVariable Integer requestId,
      @PathVariable Integer roundIndex,
      @Valid @RequestBody RequestRoundUpdateDTO roundDetails) {
    return ResponseEntity.ok(
        requestRoundService.updateRequestRound(requestId, roundIndex, roundDetails));
  }
}
