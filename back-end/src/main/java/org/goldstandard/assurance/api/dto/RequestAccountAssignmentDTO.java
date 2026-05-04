package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;

@Getter
@Setter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class RequestAccountAssignmentDTO {
  private final Integer id;
  private final Integer requestId;
  private final AccountDTO account;
  private final AssignmentType assignmentType;
  private final AssignmentStatus assignmentStatus;
  private final ZonedDateTime createdTime;
  private final ZonedDateTime updatedTime;
  private final ZonedDateTime dueDate;
}
