package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Component
public class MonitoringPeriodFactory {

  public ProjectCreditingMonitoringPeriod toProjectCreditingMonitoringPeriod(
      ProjectCreditingPeriod creditingPeriod,
      int originRequestId,
      RequestMonitoringPeriod draftMonitoringPeriod) {
    return ProjectCreditingMonitoringPeriod.builder()
        .id(getNextMonitoringPeriodId(creditingPeriod.getMonitoringPeriods()))
        .projectId(creditingPeriod.getProjectId())
        .periodId(creditingPeriod.getPeriodId())
        .startDate(draftMonitoringPeriod.getStartDate())
        .endDate(draftMonitoringPeriod.getEndDate())
        .originRequestId(originRequestId)
        .feeSchedule(draftMonitoringPeriod.getFeeSchedule())
        .build();
  }

  public List<ProjectCreditingMonitoringPeriod> toProjectCreditingMonitoringPeriods(
      Project project, CreditingPeriodDTO creditingPeriod) {
    return Optional.ofNullable(creditingPeriod.getMonitoringPeriods()).stream()
        .flatMap(Collection::stream)
        .map(dto -> toMonitoringPeriod(project, creditingPeriod, dto))
        .toList();
  }

  private ProjectCreditingMonitoringPeriod toMonitoringPeriod(
      Project project, CreditingPeriodDTO creditingPeriod, MonitoringPeriodDTO dto) {
    return ProjectCreditingMonitoringPeriod.builder()
        .id(dto.getId())
        .projectId(project.getId())
        .periodId(creditingPeriod.getPeriodId())
        .startDate(dto.getStartDate())
        .endDate(dto.getEndDate())
        .originRequestId(dto.getOriginRequestId())
        .feeSchedule(dto.getFeeSchedule())
        .build();
  }

  private Integer getNextMonitoringPeriodId(
      Collection<ProjectCreditingMonitoringPeriod> monitoringPeriods) {
    if (CollectionUtils.isEmpty(monitoringPeriods)) {
      return 1;
    }
    return monitoringPeriods.stream()
            .mapToInt(ProjectCreditingMonitoringPeriod::getId)
            .max()
            .orElse(0)
        + 1;
  }
}
