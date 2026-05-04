package org.goldstandard.assurance.api.service.external;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExternalCreditingPeriodsConverter {

  private final ExternalCreditingPeriodMethodologyConverter methodologyConverter;

  public ExternalCreditingPeriodsConverter() {
    this(new ExternalCreditingPeriodMethodologyConverter());
  }

  public Collection<ExternalCreditingPeriod> toExternalCreditingPeriods(
      Collection<ProjectCreditingPeriod> periods) {
    return periods.stream().map(this::toExternalCreditingPeriod).toList();
  }

  private ExternalCreditingPeriod toExternalCreditingPeriod(ProjectCreditingPeriod period) {
    return ExternalCreditingPeriod.builder()
        .id(period.getPeriodId())
        .startDate(period.getStartDate())
        .endDate(period.getEndDate())
        .methodologies(methodologyConverter.toMethodologyDtos(period.getMethodologies()))
        .build();
  }
}
