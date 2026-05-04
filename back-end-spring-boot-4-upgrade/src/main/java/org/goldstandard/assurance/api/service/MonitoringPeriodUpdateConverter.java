package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MonitoringPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.enumeration.MonitoringPeriodOperation;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MonitoringPeriodUpdateConverter {

  public static Collection<MonitoringPeriodUpdate> toCreditingPeriodMonitoringPeriodUpdates(
      CreditingPeriodUpdate creditingPeriodUpdate) {
    var originalMonitoringPeriods = creditingPeriodUpdate.getOriginalMonitoringPeriods();
    var updatedMonitoringPeriods = creditingPeriodUpdate.getUpdatedMonitoringPeriods();

    var deletions =
        toMonitoringPeriodDeletions(originalMonitoringPeriods, updatedMonitoringPeriods);
    var additions =
        toMonitoringPeriodAdditions(originalMonitoringPeriods, updatedMonitoringPeriods);
    var updates = toMonitoringPeriodUpdates(originalMonitoringPeriods, updatedMonitoringPeriods);
    return Stream.concat(deletions, Stream.concat(additions, updates)).toList();
  }

  public static Collection<MonitoringPeriodUpdate> toMonitoringPeriodDeletions(
      ProjectCreditingPeriod creditingPeriod) {
    return creditingPeriod.getMonitoringPeriods().stream()
        .map(MonitoringPeriodUpdateConverter::toMonitoringPeriodDeletion)
        .toList();
  }

  public static Collection<MonitoringPeriodUpdate> toMonitoringPeriodAdditions(
      ProjectCreditingPeriod creditingPeriod) {
    return creditingPeriod.getMonitoringPeriods().stream()
        .map(MonitoringPeriodUpdateConverter::toMonitoringPeriodAddition)
        .toList();
  }

  private static Stream<MonitoringPeriodUpdate> toMonitoringPeriodDeletions(
      Collection<ProjectCreditingMonitoringPeriod> originalMonitoringPeriods,
      Collection<ProjectCreditingMonitoringPeriod> updatedMonitoringPeriods) {
    return originalMonitoringPeriods.stream()
        .map(
            originalMonitoringPeriod ->
                toMonitoringPeriodDeletion(originalMonitoringPeriod, updatedMonitoringPeriods))
        .filter(Objects::nonNull);
  }

  private static MonitoringPeriodUpdate toMonitoringPeriodDeletion(
      ProjectCreditingMonitoringPeriod originalMonitoringPeriod,
      Collection<ProjectCreditingMonitoringPeriod> updatedMonitoringPeriods) {
    var updatedMonitoringPeriod =
        findMonitoringPeriodById(originalMonitoringPeriod.getId(), updatedMonitoringPeriods);
    if (Objects.nonNull(updatedMonitoringPeriod)) {
      return null;
    }
    return toMonitoringPeriodDeletion(originalMonitoringPeriod);
  }

  private static Stream<MonitoringPeriodUpdate> toMonitoringPeriodAdditions(
      Collection<ProjectCreditingMonitoringPeriod> originalMonitoringPeriods,
      Collection<ProjectCreditingMonitoringPeriod> updatedMonitoringPeriods) {
    return updatedMonitoringPeriods.stream()
        .map(
            updatedMonitoringPeriod ->
                toMonitoringPeriodAddition(updatedMonitoringPeriod, originalMonitoringPeriods))
        .filter(Objects::nonNull);
  }

  private static MonitoringPeriodUpdate toMonitoringPeriodAddition(
      ProjectCreditingMonitoringPeriod updatedMonitoringPeriod,
      Collection<ProjectCreditingMonitoringPeriod> originalMonitoringPeriods) {
    var originalMonitoringPeriod =
        findMonitoringPeriodById(updatedMonitoringPeriod.getId(), originalMonitoringPeriods);
    if (Objects.nonNull(originalMonitoringPeriod)) {
      return null;
    }
    return toMonitoringPeriodAddition(updatedMonitoringPeriod);
  }

  private static Stream<MonitoringPeriodUpdate> toMonitoringPeriodUpdates(
      Collection<ProjectCreditingMonitoringPeriod> originalMonitoringPeriods,
      Collection<ProjectCreditingMonitoringPeriod> updatedMonitoringPeriods) {
    return originalMonitoringPeriods.stream()
        .map(
            originalMonitoringPeriod ->
                toMonitoringPeriodUpdate(originalMonitoringPeriod, updatedMonitoringPeriods))
        .filter(Objects::nonNull);
  }

  private static MonitoringPeriodUpdate toMonitoringPeriodUpdate(
      ProjectCreditingMonitoringPeriod originalMonitoringPeriod,
      Collection<ProjectCreditingMonitoringPeriod> updatedMonitoringPeriods) {
    var updatedMonitoringPeriod =
        findMonitoringPeriodById(originalMonitoringPeriod.getId(), updatedMonitoringPeriods);
    if (Objects.isNull(updatedMonitoringPeriod)) {
      return null;
    }
    if (Objects.equals(
            originalMonitoringPeriod.getStartDate(), updatedMonitoringPeriod.getStartDate())
        && Objects.equals(
            originalMonitoringPeriod.getEndDate(), updatedMonitoringPeriod.getEndDate())) {
      return null;
    }
    return toBuilder(originalMonitoringPeriod)
        .operation(MonitoringPeriodOperation.UPDATE)
        .originalPeriod(originalMonitoringPeriod)
        .updatedPeriod(updatedMonitoringPeriod)
        .build();
  }

  private static MonitoringPeriodUpdate toMonitoringPeriodDeletion(
      ProjectCreditingMonitoringPeriod monitoringPeriod) {
    return toBuilder(monitoringPeriod)
        .operation(MonitoringPeriodOperation.DELETE)
        .originalPeriod(monitoringPeriod)
        .build();
  }

  private static MonitoringPeriodUpdate toMonitoringPeriodAddition(
      ProjectCreditingMonitoringPeriod monitoringPeriod) {
    return toBuilder(monitoringPeriod)
        .operation(MonitoringPeriodOperation.ADD)
        .updatedPeriod(monitoringPeriod)
        .build();
  }

  private static ProjectCreditingMonitoringPeriod findMonitoringPeriodById(
      Integer id, Collection<ProjectCreditingMonitoringPeriod> monitoringPeriods) {
    return monitoringPeriods.stream()
        .filter(monitoringPeriod -> Objects.equals(monitoringPeriod.getId(), id))
        .findFirst()
        .orElse(null);
  }

  private static MonitoringPeriodUpdate.MonitoringPeriodUpdateBuilder toBuilder(
      ProjectCreditingMonitoringPeriod monitoringPeriod) {
    return MonitoringPeriodUpdate.builder()
        .projectId(monitoringPeriod.getProjectId())
        .periodId(monitoringPeriod.getPeriodId())
        .id(monitoringPeriod.getId());
  }
}
