package org.goldstandard.assurance.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class RequestTypeAssignmentTypes {
  private final Integer requestTypeId;
  private final List<Rule> assignmentTypes;

  @RequiredArgsConstructor
  @Getter
  @Builder
  public static class Rule {
    private final Integer assignmentTypeId;
    private final Boolean mandatory;
  }
}
