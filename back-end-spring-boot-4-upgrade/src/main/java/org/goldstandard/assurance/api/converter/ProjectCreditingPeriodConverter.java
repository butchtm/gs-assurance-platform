package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class ProjectCreditingPeriodConverter {

  private final MethodologyConverter methodologyConverter;
  private final ProjectCreditingMonitoringPeriodConverter monitoringPeriodConverter;

  public List<CreditingPeriodDTO> toDtos(Collection<ProjectCreditingPeriod> periods) {
    if (CollectionUtils.isEmpty(periods)) {
      return Collections.emptyList();
    }
    return periods.stream().map(this::toDto).toList();
  }

  public CreditingPeriodDTO toDto(ProjectCreditingPeriod period) {
    return CreditingPeriodDTO.builder()
        .periodId(period.getPeriodId())
        .startDate(period.getStartDate())
        .endDate(period.getEndDate())
        .methodologies(methodologyConverter.toDtos(period.getMethodologies()))
        .monitoringPeriods(monitoringPeriodConverter.toDtos(period.getMonitoringPeriods()))
        .build();
  }

  public CreditingPeriodDTO fromRequestCreditingPeriod(RequestCreditingPeriod period) {
    return CreditingPeriodDTO.builder()
        .periodId(null)
        .startDate(period.getStartDate())
        .endDate(period.getEndDate())
        .methodologies(Collections.emptyList())
        .build();
  }
}
