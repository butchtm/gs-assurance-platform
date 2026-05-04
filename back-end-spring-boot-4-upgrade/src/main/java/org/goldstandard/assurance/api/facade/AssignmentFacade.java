package org.goldstandard.assurance.api.facade;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.RequestAccountAssignmentConverter;
import org.goldstandard.assurance.api.dto.AssignmentCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.AssignmentStatusUpdate;
import org.goldstandard.assurance.api.dto.RequestAccountAssignmentDTO;
import org.goldstandard.assurance.api.service.AssignmentFilters;
import org.goldstandard.assurance.api.service.AssignmentLookupService;
import org.goldstandard.assurance.api.service.AssignmentPermissionsService;
import org.goldstandard.assurance.api.service.AssignmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class AssignmentFacade {

  private final AssignmentLookupService assignmentLookupService;
  private final AssignmentService assignmentService;
  private final AssignmentPermissionsService assignmentPermissionsService;
  private final RequestAccountAssignmentConverter mapper;

  @Transactional(readOnly = true)
  public Page<RequestAccountAssignmentDTO> findAllAssignments(
      Integer accountId, Integer requestId, Integer assignmentTypeId, Pageable pageable) {
    var assignments =
        assignmentLookupService.findAllAssignments(
            accountId, requestId, assignmentTypeId, pageable);
    return assignments.map(mapper::toDto);
  }

  @Transactional(readOnly = true)
  public Collection<RequestAccountAssignmentDTO> getAssignments(AssignmentFilters filters) {
    var assignments = assignmentLookupService.getAssignments(filters);
    return mapper.toDtos(assignments);
  }

  @Transactional(readOnly = true)
  public RequestAccountAssignmentDTO getAssignment(Integer assignmentId) {
    var assignment = assignmentLookupService.forceFindAssignmentById(assignmentId);
    assignmentPermissionsService.validateCanReadAssignment(assignment);
    return mapper.toDto(assignment);
  }

  public RequestAccountAssignmentDTO createAssignment(AssignmentCreateUpdateDTO create) {
    return mapper.toDto(assignmentService.createAssignment(create));
  }

  public RequestAccountAssignmentDTO updateAssignment(
      Integer assignmentId, AssignmentCreateUpdateDTO update) {
    return mapper.toDto(assignmentService.updateAssignment(assignmentId, update));
  }

  public void updateAssignmentStatus(Integer assignmentId, AssignmentStatusUpdate update) {
    assignmentService.updateAssignmentStatus(assignmentId, update.getAssignmentStatusId());
  }
}
