package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.TransitionApprovalProcedure;
import org.goldstandard.assurance.api.entity.lookup.TransitionFromOtherScheme;
import org.goldstandard.assurance.api.entity.lookup.TransitionPathway;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Getter
public class TransitionDetail {
  private final String projectLinkInOtherScheme;
  private final ZonedDateTime creditingPeriodStartDateOtherSchemes;
  private final ZonedDateTime creditingPeriodEndDateOtherSchemes;
  private final TransitionApprovalProcedure transitionApprovalProcedure;
  private final TransitionFromOtherScheme transitionFromOtherScheme;
  private final TransitionPathway transitionPathway;
}
