package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentCreateUpdateDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignmentCreateUpdateDTOMother {

  public static AssignmentCreateUpdateDTO build() {
    return builder().build();
  }

  public static AssignmentCreateUpdateDTO.AssignmentCreateUpdateDTOBuilder builder() {
    return AssignmentCreateUpdateDTO.builder()
        .requestId(2)
        .accountId(3)
        .assignmentStatusId(0)
        .assignmentTypeId(0);
  }
}
