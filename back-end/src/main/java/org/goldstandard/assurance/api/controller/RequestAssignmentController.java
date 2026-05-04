package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.goldstandard.assurance.api.util.CustomCollectionUtils.toCollection;

import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.RequestAccountAssignmentDTO;
import org.goldstandard.assurance.api.facade.AssignmentFacade;
import org.goldstandard.assurance.api.service.AssignmentFilters;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RequestAssignmentController {

  private final AssignmentFacade facade;

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    FINANCE_ROLE,
    GS_REVIEWER_ROLE,
    VVB_ROLE,
    PROJECT_DEVELOPER_ROLE
  })
  @GetMapping("/requests/{requestId}/assignments")
  public ResponseEntity<Collection<RequestAccountAssignmentDTO>> getRequestAssignments(
      @PathVariable Integer requestId,
      @RequestParam(value = "assignmentStatusId", required = false) Integer assignmentStatusId,
      @RequestParam(value = "assignmentTypeId", required = false) Integer assignmentTypeId) {
    var filters =
        AssignmentFilters.builder()
            .requestId(requestId)
            .assignmentStatusId(assignmentStatusId)
            .assignmentTypeIds(toCollection(assignmentTypeId))
            .build();
    var assignments = facade.getAssignments(filters);
    return ResponseEntity.ok(assignments);
  }
}
