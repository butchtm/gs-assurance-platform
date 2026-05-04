package org.goldstandard.assurance.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class AssignmentCreateUpdateDTO {
  @NotNull private final Integer requestId;
  @NotNull private final Integer accountId;
  @NotNull private final Integer assignmentTypeId;
  private final Integer assignmentStatusId;
}
