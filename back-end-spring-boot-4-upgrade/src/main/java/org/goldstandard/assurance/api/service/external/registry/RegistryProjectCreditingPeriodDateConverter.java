package org.goldstandard.assurance.api.service.external.registry;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;

public class RegistryProjectCreditingPeriodDateConverter {

  public ZonedDateTime toEarliestCreditingPeriodStartDate(Project project) {
    var creditingPeriods = project.getCreditingPeriods();
    if (Objects.isNull(creditingPeriods)) {
      return null;
    }
    return creditingPeriods.stream()
        .map(ProjectCreditingPeriod::getStartDate)
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .orElse(null);
  }

  public ZonedDateTime toLatestCreditingPeriodEndDate(Project project) {
    var creditingPeriods = project.getCreditingPeriods();
    if (Objects.isNull(creditingPeriods)) {
      return null;
    }
    return creditingPeriods.stream()
        .map(ProjectCreditingPeriod::getEndDate)
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder())
        .orElse(null);
  }
}
