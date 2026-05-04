package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.RequestAccountAssignmentConverter;
import org.goldstandard.assurance.api.dto.AssignmentCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.AssignmentStatusUpdate;
import org.goldstandard.assurance.api.dto.RequestAccountAssignmentDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.facade.AssignmentFacade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("assignments")
public class AssignmentController {

  private final AssignmentFacade facade;
  private final RequestAccountAssignmentConverter mapper;

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    FINANCE_ROLE,
    GS_REVIEWER_ROLE,
    VVB_ROLE
  })
  @GetMapping("account/{accountId}")
  public ResponseEntity<PagedResource<RequestAccountAssignmentDTO>> getAssignments(
      @PathVariable Integer accountId,
      @RequestParam(required = false) Integer requestId,
      @RequestParam(required = false) Integer assignmentTypeId,
      @PageableDefault(
              sort = {"id"},
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable) {
    return ResponseEntity.ok(
        new PagedResource<>(
            facade.findAllAssignments(accountId, requestId, assignmentTypeId, pageable)));
  }

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    FINANCE_ROLE,
    GS_REVIEWER_ROLE,
    VVB_ROLE,
    PROJECT_DEVELOPER_ROLE
  })
  @GetMapping("{assignmentId}")
  public ResponseEntity<RequestAccountAssignmentDTO> getAssignment(
      @PathVariable Integer assignmentId) {
    var assignment = facade.getAssignment(assignmentId);
    return ResponseEntity.ok(assignment);
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping
  public ResponseEntity<RequestAccountAssignmentDTO> createAssignment(
      @Valid @RequestBody AssignmentCreateUpdateDTO create) {
    return ResponseEntity.ok(facade.createAssignment(create));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PutMapping("{assignmentId}")
  public ResponseEntity<RequestAccountAssignmentDTO> updateAssignment(
      @PathVariable Integer assignmentId, @Valid @RequestBody AssignmentCreateUpdateDTO update) {
    return ResponseEntity.ok(facade.updateAssignment(assignmentId, update));
  }

  @RolesAllowed({GS_REVIEWER_ROLE, GS_REGISTRY_ROLE, FINANCE_ROLE, VVB_ROLE})
  @PutMapping("{assignmentId}/status")
  public ResponseEntity<String> updateAssignmentStatus(
      @PathVariable(value = "assignmentId") Integer assignmentId,
      @Valid @RequestBody AssignmentStatusUpdate assignmentStatusUpdate) {
    facade.updateAssignmentStatus(assignmentId, assignmentStatusUpdate);
    return ResponseEntity.ok().build();
  }
}
