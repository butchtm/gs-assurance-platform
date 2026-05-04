package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.enumeration.MethodologyOperation;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodologyUpdateConverter {

  public static Collection<MethodologyUpdate> toPeriodMethodologyUpdates(
      CreditingPeriodUpdate periodUpdate) {
    var originalMethodologies = periodUpdate.getOriginalMethodologies();
    var updatedMethodologies = periodUpdate.getUpdatedMethodologies();

    var deletions = toMethodologyDeletions(originalMethodologies, updatedMethodologies);
    var additions = toMethodologyAdditions(originalMethodologies, updatedMethodologies);
    var updates = toMethodologyUpdates(originalMethodologies, updatedMethodologies);
    return Stream.concat(deletions, Stream.concat(additions, updates)).toList();
  }

  public static Collection<MethodologyUpdate> toMethodologyDeletions(
      ProjectCreditingPeriod creditingPeriod) {
    return creditingPeriod.getMethodologies().stream()
        .map(MethodologyUpdateConverter::toMethodologyDeletion)
        .toList();
  }

  public static Collection<MethodologyUpdate> toMethodologyAdditions(
      ProjectCreditingPeriod creditingPeriod) {
    return creditingPeriod.getMethodologies().stream()
        .map(MethodologyUpdateConverter::toMethodologyAddition)
        .toList();
  }

  private static Stream<MethodologyUpdate> toMethodologyDeletions(
      Collection<ProjectCreditingPeriodMethodology> originalMethodologies,
      Collection<ProjectCreditingPeriodMethodology> updatedMethodologies) {
    return originalMethodologies.stream()
        .map(
            originalMethodology -> toMethodologyDeletion(originalMethodology, updatedMethodologies))
        .filter(Objects::nonNull);
  }

  private static MethodologyUpdate toMethodologyDeletion(
      ProjectCreditingPeriodMethodology originalMethodology,
      Collection<ProjectCreditingPeriodMethodology> updatedMethodologies) {
    var updatedMethodology =
        findMethodologyById(originalMethodology.getMethodologyId(), updatedMethodologies);
    if (Objects.nonNull(updatedMethodology)) {
      return null;
    }
    return toMethodologyDeletion(originalMethodology);
  }

  private static Stream<MethodologyUpdate> toMethodologyAdditions(
      Collection<ProjectCreditingPeriodMethodology> originalMethodologies,
      Collection<ProjectCreditingPeriodMethodology> updatedMethodologies) {
    return updatedMethodologies.stream()
        .map(updatedMethodology -> toMethodologyAddition(updatedMethodology, originalMethodologies))
        .filter(Objects::nonNull);
  }

  private static MethodologyUpdate toMethodologyAddition(
      ProjectCreditingPeriodMethodology updatedMethodology,
      Collection<ProjectCreditingPeriodMethodology> originalMethodologies) {
    var originalMethodology =
        findMethodologyById(updatedMethodology.getMethodologyId(), originalMethodologies);
    if (Objects.nonNull(originalMethodology)) {
      return null;
    }
    return toMethodologyAddition(updatedMethodology);
  }

  private static Stream<MethodologyUpdate> toMethodologyUpdates(
      Collection<ProjectCreditingPeriodMethodology> originalMethodologies,
      Collection<ProjectCreditingPeriodMethodology> updatedMethodologies) {
    return originalMethodologies.stream()
        .map(originalMethodology -> toMethodologyUpdate(originalMethodology, updatedMethodologies))
        .filter(Objects::nonNull);
  }

  private static MethodologyUpdate toMethodologyUpdate(
      ProjectCreditingPeriodMethodology originalMethodology,
      Collection<ProjectCreditingPeriodMethodology> updatedMethodologies) {
    var updatedMethodology =
        findMethodologyById(originalMethodology.getMethodologyId(), updatedMethodologies);
    if (Objects.isNull(updatedMethodology)) {
      return null;
    }
    if (Objects.equals(originalMethodology.getVersion(), updatedMethodology.getVersion())) {
      return null;
    }
    return toBuilder(originalMethodology)
        .operation(MethodologyOperation.UPDATE)
        .originalVersion(originalMethodology.getVersion())
        .updatedVersion(updatedMethodology.getVersion())
        .build();
  }

  private static ProjectCreditingPeriodMethodology findMethodologyById(
      Integer id, Collection<ProjectCreditingPeriodMethodology> methodologies) {
    return methodologies.stream()
        .filter(methodology -> Objects.equals(methodology.getMethodologyId(), id))
        .findFirst()
        .orElse(null);
  }

  private static MethodologyUpdate toMethodologyDeletion(
      ProjectCreditingPeriodMethodology methodology) {
    return toBuilder(methodology)
        .operation(MethodologyOperation.DELETE)
        .originalVersion(methodology.getVersion())
        .build();
  }

  private static MethodologyUpdate toMethodologyAddition(
      ProjectCreditingPeriodMethodology methodology) {
    return toBuilder(methodology)
        .operation(MethodologyOperation.ADD)
        .updatedVersion(methodology.getVersion())
        .build();
  }

  private static MethodologyUpdate.MethodologyUpdateBuilder toBuilder(
      ProjectCreditingPeriodMethodology methodology) {
    return MethodologyUpdate.builder()
        .projectId(methodology.getProjectId())
        .periodId(methodology.getPeriodId())
        .methodologyId(methodology.getMethodologyId());
  }
}
