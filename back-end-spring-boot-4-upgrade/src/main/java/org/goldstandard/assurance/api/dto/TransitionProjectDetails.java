package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransitionProjectDetails {
  private String projectLinkInOtherScheme;
  private ZonedDateTime creditingPeriodStartDateOtherSchemes;
  private ZonedDateTime creditingPeriodEndDateOtherSchemes;
  private Integer transitionApprovalProcedureId;
  private Integer transitionFromOtherSchemeId;
  private Integer transitionPathwayId;
}
