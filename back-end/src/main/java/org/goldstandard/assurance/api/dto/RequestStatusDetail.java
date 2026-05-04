package org.goldstandard.assurance.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestStatusDetail {
  private Integer requestStatusId;
  private List<PermittedStatuses> permittedStatuses;
  private List<InvolvedAccountTypes> activeAccountTypes;

  @Getter
  @Builder
  public static class PermittedStatuses {
    private Integer requestStatusId;
    private Integer minimumRoundOfReviewCount;
    private boolean permittedDuringConsultation;
  }

  @Getter
  @Builder
  public static class InvolvedAccountTypes {
    private Integer accountTypeId;
    private Boolean canProgress;
  }
}
