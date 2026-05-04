package org.goldstandard.assurance.api.converter;

import java.util.Objects;
import org.goldstandard.assurance.api.dto.TransitionDetail;
import org.goldstandard.assurance.api.entity.TransitionProject;
import org.springframework.stereotype.Component;

@Component
public class TransitionDetailConverter {

  public TransitionDetail toDto(TransitionProject project) {
    if (Objects.isNull(project) || !project.hasTransitionDetails()) {
      return null;
    }
    return TransitionDetail.builder()
        .projectLinkInOtherScheme(project.getProjectLinkInOtherScheme())
        .creditingPeriodStartDateOtherSchemes(project.getCreditingPeriodStartDateOtherSchemes())
        .creditingPeriodEndDateOtherSchemes(project.getCreditingPeriodEndDateOtherSchemes())
        .transitionApprovalProcedure(project.getTransitionApprovalProcedure())
        .transitionFromOtherScheme(project.getTransitionFromOtherScheme())
        .transitionPathway(project.getTransitionPathway())
        .build();
  }
}
