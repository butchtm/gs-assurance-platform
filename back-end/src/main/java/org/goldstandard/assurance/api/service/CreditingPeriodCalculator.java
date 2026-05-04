package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdateCalculatorParams;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdates;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreditingPeriodCalculator {

  private final Clock clock;
  private final AuthenticatedUserSupplier userSupplier;

  public CreditingPeriodUpdates calculateUpdates(
      Collection<ProjectCreditingPeriod> originalPeriods,
      Collection<ProjectCreditingPeriod> updatedPeriods) {
    return calculateUpdates(originalPeriods, updatedPeriods, null);
  }

  public CreditingPeriodUpdates calculateUpdates(
      Collection<ProjectCreditingPeriod> originalPeriods,
      Collection<ProjectCreditingPeriod> updatedPeriods,
      Request request) {
    var params =
        CreditingPeriodUpdateCalculatorParams.builder()
            .updatedAt(ZonedDateTime.now(clock))
            .updatedBy(userSupplier.getAuthenticatedUserAccount().orElse(null))
            .originalPeriods(originalPeriods)
            .updatedPeriods(updatedPeriods)
            .request(request)
            .build();
    return CreditingPeriodUpdates.builder()
        .deletions(toPeriodDeletions(params))
        .updates(toPeriodUpdates(params))
        .additions(toPeriodAdditions(params))
        .build();
  }

  private Collection<CreditingPeriodUpdate> toPeriodDeletions(
      CreditingPeriodUpdateCalculatorParams params) {
    return params.getOriginalPeriods().stream()
        .map(originalPeriod -> toDeletion(params, originalPeriod))
        .filter(Objects::nonNull)
        .toList();
  }

  private Collection<CreditingPeriodUpdate> toPeriodUpdates(
      CreditingPeriodUpdateCalculatorParams params) {
    return params.getOriginalPeriods().stream()
        .map(originalPeriod -> toUpdate(params, originalPeriod))
        .filter(Objects::nonNull)
        .toList();
  }

  private Collection<CreditingPeriodUpdate> toPeriodAdditions(
      CreditingPeriodUpdateCalculatorParams params) {
    return params.getUpdatedPeriods().stream()
        .map(updatedPeriod -> toAddition(params, updatedPeriod))
        .filter(Objects::nonNull)
        .toList();
  }

  private CreditingPeriodUpdate toDeletion(
      CreditingPeriodUpdateCalculatorParams params, ProjectCreditingPeriod originalPeriod) {
    var updatedPeriod = findById(originalPeriod.getPeriodId(), params.getUpdatedPeriods());
    if (Objects.nonNull(updatedPeriod)) {
      return null;
    }
    return CreditingPeriodUpdate.builder()
        .updatedAt(params.getUpdatedAt())
        .updatedBy(params.getUpdatedBy())
        .id(originalPeriod.getPeriodId())
        .original(originalPeriod)
        .request(params.getRequest())
        .build();
  }

  private CreditingPeriodUpdate toUpdate(
      CreditingPeriodUpdateCalculatorParams params, ProjectCreditingPeriod originalPeriod) {
    var updatedPeriod = findById(originalPeriod.getPeriodId(), params.getUpdatedPeriods());
    if (Objects.isNull(updatedPeriod)) {
      return null;
    }
    return CreditingPeriodUpdate.builder()
        .updatedAt(params.getUpdatedAt())
        .updatedBy(params.getUpdatedBy())
        .id(originalPeriod.getPeriodId())
        .original(originalPeriod)
        .updated(updatedPeriod)
        .request(params.getRequest())
        .build();
  }

  private CreditingPeriodUpdate toAddition(
      CreditingPeriodUpdateCalculatorParams params, ProjectCreditingPeriod updatedPeriod) {
    var originalPeriod = findById(updatedPeriod.getPeriodId(), params.getOriginalPeriods());
    if (Objects.nonNull(originalPeriod)) {
      return null;
    }
    return CreditingPeriodUpdate.builder()
        .updatedAt(params.getUpdatedAt())
        .updatedBy(params.getUpdatedBy())
        .id(updatedPeriod.getPeriodId())
        .updated(updatedPeriod)
        .request(params.getRequest())
        .build();
  }

  private static ProjectCreditingPeriod findById(
      Integer id, Collection<ProjectCreditingPeriod> periods) {
    return periods.stream()
        .filter(period -> Objects.equals(period.getPeriodId(), id))
        .findFirst()
        .orElse(null);
  }
}
