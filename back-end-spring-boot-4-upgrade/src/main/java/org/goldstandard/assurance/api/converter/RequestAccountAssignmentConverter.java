package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestAccountAssignmentDTO;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestAccountAssignmentConverter {

  private final AccountConverter accountConverter;

  public Collection<RequestAccountAssignmentDTO> toDtos(
      Collection<RequestAccountAssignment> assignments) {
    return assignments.stream().map(this::toDto).toList();
  }

  public RequestAccountAssignmentDTO toDto(RequestAccountAssignment assignment) {
    return RequestAccountAssignmentDTO.builder()
        .id(assignment.getId())
        .requestId(assignment.getRequestId())
        .account(accountConverter.toDtoWithoutUsers(assignment.getAccount()))
        .assignmentType(assignment.getAssignmentType())
        .assignmentStatus(assignment.getAssignmentStatus())
        .createdTime(assignment.getCreatedTime())
        .updatedTime(assignment.getUpdatedTime())
        .dueDate(assignment.getDueDate())
        .build();
  }
}
