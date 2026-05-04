package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.BatchRequestConverter;
import org.goldstandard.assurance.api.converter.ProjectConverter;
import org.goldstandard.assurance.api.dto.BatchProjectsDTO;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.service.BatchRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("batch-requests")
@RestController
@RequiredArgsConstructor
public class BatchRequestController {

  private final BatchRequestService service;

  private final BatchRequestConverter batchRequestConverter;
  private final ProjectConverter projectConverter;

  @PostMapping("/types")
  public ResponseEntity<List<BatchType>> getBatchTypes(@RequestBody GetBatchTypesDTO dto) {
    return ResponseEntity.ok(service.getBatchTypes(dto));
  }

  @PostMapping("/candidate-projects")
  public ResponseEntity<List<ProjectDTO>> getCandidateProjects(
      @RequestBody GetBatchCandidateProjectsDTO dto) {
    var projects = service.getCandidateProjects(dto);
    return ResponseEntity.ok(
        projects.stream().map(projectConverter::toCandidateProjectDTO).toList());
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping
  public ResponseEntity<Integer> createBatchRequest(@Valid @RequestBody BatchRequestCreateDTO dto) {
    return ResponseEntity.ok(service.createBatchRequest(dto));
  }

  @GetMapping("/{batchId}/projects")
  public ResponseEntity<BatchProjectsDTO> getAllProjectsInBatch(@PathVariable int batchId) {
    var batchRequests = service.getRequestsInBatch(batchId);
    return ResponseEntity.ok(batchRequestConverter.toBatchProjects(batchRequests));
  }
}
