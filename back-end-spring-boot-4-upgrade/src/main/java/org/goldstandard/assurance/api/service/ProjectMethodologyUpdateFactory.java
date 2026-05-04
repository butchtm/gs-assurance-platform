package org.goldstandard.assurance.api.service;

import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.DbMethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.goldstandard.assurance.api.enumeration.ProjectUpdateVerdict;
import org.springframework.stereotype.Component;

@Component
public class ProjectMethodologyUpdateFactory {

  public ProjectMethodologyUpdate toProjectMethodologyUpdate(
      CreditingPeriodUpdate update, ProjectUpdateVerdict verdict) {
    return ProjectMethodologyUpdate.builder()
        .periodId(update.getId())
        .projectId(update.getProjectId())
        .request(update.getRequestId().map(this::buildRequestWithId).orElse(null))
        .createdTimestamp(update.getUpdatedAt())
        .createdBy(update.getUpdatedBy())
        .verdict(verdict)
        .build();
  }

  public DbMethodologyUpdate toDbMethodologyUpdate(
      ProjectMethodologyUpdate projectUpdate, MethodologyUpdate update) {
    return DbMethodologyUpdate.builder()
        .projectMethodologyUpdateId(projectUpdate.getId())
        .methodologyId(update.getMethodologyId())
        .initialMethodologyVersion(update.getOriginalVersion())
        .newMethodologyVersion(update.getUpdatedVersion())
        .operation(update.getOperation())
        .build();
  }

  private Request buildRequestWithId(Integer requestId) {
    return Request.builder().id(requestId).build();
  }
}
