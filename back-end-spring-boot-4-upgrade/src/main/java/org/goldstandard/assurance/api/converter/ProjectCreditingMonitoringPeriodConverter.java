package org.goldstandard.assurance.api.converter;

import java.util.*;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class ProjectCreditingMonitoringPeriodConverter {

  public List<MonitoringPeriodDTO> toDtos(Collection<ProjectCreditingMonitoringPeriod> periods) {
    if (CollectionUtils.isEmpty(periods)) {
      return Collections.emptyList();
    }
    return periods.stream()
        .sorted(Comparator.comparing(ProjectCreditingMonitoringPeriod::getStartDate))
        .map(this::toDto)
        .toList();
  }

  public MonitoringPeriodDTO toDto(Request request) {
    var monitoringPeriod = request.getDraftMonitoringPeriod().orElse(null);

    return MonitoringPeriodDTO.builder()
        .id(
            Optional.ofNullable(monitoringPeriod)
                .map(RequestMonitoringPeriod::getPeriodId)
                .orElse(request.getMonitoringPeriodId()))
        .startDate(
            Optional.ofNullable(monitoringPeriod)
                .map(RequestMonitoringPeriod::getStartDate)
                .orElse(null))
        .endDate(
            Optional.ofNullable(monitoringPeriod)
                .map(RequestMonitoringPeriod::getEndDate)
                .orElse(null))
        .feeSchedule(
            Optional.ofNullable(monitoringPeriod)
                .map(RequestMonitoringPeriod::getFeeSchedule)
                .orElse(null))
        .build();
  }

  private MonitoringPeriodDTO toDto(ProjectCreditingMonitoringPeriod period) {
    return MonitoringPeriodDTO.builder()
        .id(period.getId())
        .startDate(period.getStartDate())
        .endDate(period.getEndDate())
        .originRequestId(period.getOriginRequestId())
        .feeSchedule(period.getFeeSchedule())
        .build();
  }
}
