package org.goldstandard.assurance.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestTypeAssignmentTypes {
  private Integer requestTypeId;
  private List<Rule> assignmentTypes;

  @Getter
  @Builder
  public static class Rule {
    private Integer assignmentTypeId;
    private Boolean mandatory;
  }
}
