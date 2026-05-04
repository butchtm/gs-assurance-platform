package org.goldstandard.assurance.api.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.CreditingPeriodMethodologyDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodId;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.util.CustomCollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Component
public class CreditingPeriodFactory {

  private final MonitoringPeriodFactory monitoringPeriodFactory;

  public List<ProjectCreditingPeriod> toCreditingPeriods(
      Project project, Collection<CreditingPeriodDTO> dtos) {
    return dtos.stream().map(dto -> toCreditingPeriod(project, dto)).toList();
  }

  public List<ProjectCreditingPeriodMethodology> toProjectCreditingPeriodMethodologies(
      ProjectCreditingPeriod creditingPeriod, Collection<RequestMethodology> methodologies) {
    if (CollectionUtils.isEmpty(methodologies)) {
      return Collections.emptyList();
    }
    return methodologies.stream()
        .map(methodology -> toProjectCreditingPeriodMethodology(creditingPeriod, methodology))
        .toList();
  }

  public ProjectCreditingPeriod addMonitoringPeriod(
      ProjectCreditingPeriod creditingPeriod,
      int originRequestId,
      RequestMonitoringPeriod draftMonitoringPeriod) {
    var monitoringPeriods = creditingPeriod.getMonitoringPeriods();
    var newMonitoringPeriods = CustomCollectionUtils.nullSafeCopy(monitoringPeriods);
    if (!datesWithinCreditingPeriod(
        draftMonitoringPeriod.getStartDate(),
        draftMonitoringPeriod.getEndDate(),
        creditingPeriod)) {
      throw new IllegalArgumentException(
          "New Monitoring Period dates must fall within Crediting Period dates");
    }
    if (overlapWithMonitoringPeriods(
        draftMonitoringPeriod.getStartDate(),
        draftMonitoringPeriod.getEndDate(),
        monitoringPeriods)) {
      throw new IllegalArgumentException(
          "New Monitoring Period dates must not overlap with existing Monitoring Periods");
    }
    var newMonitoringPeriod =
        monitoringPeriodFactory.toProjectCreditingMonitoringPeriod(
            creditingPeriod, originRequestId, draftMonitoringPeriod);
    newMonitoringPeriods.add(newMonitoringPeriod);
    return creditingPeriod.withMonitoringPeriods(newMonitoringPeriods);
  }

  public ProjectCreditingPeriod updateMonitoringPeriod(
      ProjectCreditingPeriod creditingPeriod, RequestMonitoringPeriod draftMonitoringPeriod) {
    var monitoringPeriods = creditingPeriod.getMonitoringPeriods();
    var copyOfMonitoringPeriods = CustomCollectionUtils.nullSafeCopy(monitoringPeriods);
    var newMonitoringPeriods =
        copyOfMonitoringPeriods.stream()
            .map(period -> updateMonitoringPeriod(period, draftMonitoringPeriod))
            .toList();
    return creditingPeriod.withMonitoringPeriods(newMonitoringPeriods);
  }

  private ProjectCreditingMonitoringPeriod updateMonitoringPeriod(
      ProjectCreditingMonitoringPeriod period, RequestMonitoringPeriod draftMonitoringPeriod) {
    if (Objects.equals(period.getId(), draftMonitoringPeriod.getPeriodId())) {
      return period.withFeeSchedule(draftMonitoringPeriod.getFeeSchedule());
    }
    return period;
  }

  private ProjectCreditingPeriod toCreditingPeriod(Project project, CreditingPeriodDTO dto) {
    var id = new ProjectCreditingPeriodId(project.getId(), dto.getPeriodId());
    return ProjectCreditingPeriod.builder()
        .projectId(id.getProjectId())
        .periodId(id.getPeriodId())
        .project(project)
        .startDate(dto.getStartDate())
        .endDate(dto.getEndDate())
        .methodologies(toMethodologies(id, dto.getMethodologies()))
        .monitoringPeriods(
            monitoringPeriodFactory.toProjectCreditingMonitoringPeriods(project, dto))
        .build();
  }

  private List<ProjectCreditingPeriodMethodology> toMethodologies(
      ProjectCreditingPeriodId creditingPeriodId, Collection<CreditingPeriodMethodologyDTO> dtos) {
    if (CollectionUtils.isEmpty(dtos)) {
      return Collections.emptyList();
    }
    return dtos.stream().map(dto -> toMethodology(creditingPeriodId, dto)).toList();
  }

  private ProjectCreditingPeriodMethodology toMethodology(
      ProjectCreditingPeriodId creditingPeriodId, CreditingPeriodMethodologyDTO dto) {
    return ProjectCreditingPeriodMethodology.builder()
        .projectId(creditingPeriodId.getProjectId())
        .periodId(creditingPeriodId.getPeriodId())
        .methodologyId(dto.getId())
        .methodology(new Methodology(dto.getId()))
        .version(dto.getVersion())
        .build();
  }

  private ProjectCreditingPeriodMethodology toProjectCreditingPeriodMethodology(
      ProjectCreditingPeriod creditingPeriod, RequestMethodology methodology) {
    return ProjectCreditingPeriodMethodology.builder()
        .projectId(creditingPeriod.getProjectId())
        .periodId(creditingPeriod.getPeriodId())
        .methodologyId(methodology.getMethodology().getId())
        .methodology(methodology.getMethodology())
        .version(methodology.getMethodologyVersion())
        .build();
  }

  private boolean datesWithinCreditingPeriod(
      ZonedDateTime monitoringStartDate,
      ZonedDateTime monitoringEndDate,
      ProjectCreditingPeriod creditingPeriod) {
    var startDate = creditingPeriod.getStartDate();
    var endDate = creditingPeriod.getEndDate();
    return dateWithinRange(monitoringStartDate, startDate, endDate)
        && dateWithinRange(monitoringEndDate, startDate, endDate);
  }

  private boolean overlapWithMonitoringPeriods(
      ZonedDateTime startDate,
      ZonedDateTime endDate,
      Collection<ProjectCreditingMonitoringPeriod> monitoringPeriods) {
    if (Objects.isNull(monitoringPeriods) || monitoringPeriods.isEmpty()) {
      return false;
    }
    return monitoringPeriods.stream()
        .anyMatch(
            period ->
                dateRangesOverlap(startDate, endDate, period.getStartDate(), period.getEndDate()));
  }

  private boolean dateWithinRange(
      ZonedDateTime dateTime, ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
    var date = excludeTime(dateTime);
    var startDate = excludeTime(startDateTime);
    var endDate = excludeTime(endDateTime);
    return !(date.isBefore(startDate) || date.isAfter(endDate));
  }

  private static ZonedDateTime excludeTime(ZonedDateTime dateTime) {
    return dateTime.truncatedTo(ChronoUnit.DAYS);
  }

  private boolean dateRangesOverlap(
      ZonedDateTime startDate,
      ZonedDateTime endDate,
      ZonedDateTime rangeStart,
      ZonedDateTime rangeEnd) {
    return dateWithinRange(startDate, rangeStart, rangeEnd)
        || dateWithinRange(endDate, rangeStart, rangeEnd);
  }
}
