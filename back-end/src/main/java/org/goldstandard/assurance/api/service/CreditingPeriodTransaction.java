package org.goldstandard.assurance.api.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdates;
import org.goldstandard.assurance.api.entity.creditingperiod.DbMethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MonitoringPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.enumeration.ProjectUpdateVerdict;
import org.goldstandard.assurance.api.repository.MethodologyUpdateRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingMonitoringPeriodRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodMethodologyRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.ProjectMethodologyUpdateRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Component
public class CreditingPeriodTransaction {
  private final CreditingPeriodCalculator creditingPeriodCalculator;
  private final ProjectCreditingPeriodRepository periodRepository;
  private final ProjectCreditingPeriodMethodologyRepository methodologyRepository;
  private final ProjectCreditingMonitoringPeriodRepository monitoringPeriodRepository;
  private final ProjectMethodologyUpdateRepository projectMethodologyUpdateRepository;
  private final MethodologyUpdateRepository methodologyUpdateRepository;
  private final CreditingPeriodUpdateConverter periodUpdatesConverter;
  private final ProjectMethodologyUpdateFactory methodologyUpdateFactory;

  public void apply(Project originalProject, Project updatedProject, Request request) {
    var originalPeriods = originalProject.getCreditingPeriods();
    var updatedPeriods = updatedProject.getCreditingPeriods();
    var verdict = calculateMethodologyUpdateVerdict(request);
    apply(
        creditingPeriodCalculator.calculateUpdates(originalPeriods, updatedPeriods, request),
        verdict);
  }

  public void apply(
      Collection<ProjectCreditingPeriod> originalPeriods,
      Collection<ProjectCreditingPeriod> updatedPeriods) {
    apply(
        creditingPeriodCalculator.calculateUpdates(originalPeriods, updatedPeriods),
        ProjectUpdateVerdict.PROJECT_FORCED);
  }

  private void apply(CreditingPeriodUpdates updates, ProjectUpdateVerdict verdict) {
    updates.getDeletions().forEach(d -> deleteCreditingPeriod(d, verdict));
    updates.getUpdates().forEach(u -> updateCreditingPeriod(u, verdict));
    updates.getAdditions().forEach(a -> addCreditingPeriod(a, verdict));
  }

  private void deleteCreditingPeriod(CreditingPeriodUpdate deletion, ProjectUpdateVerdict verdict) {
    if (persistMethodologiesToProject(verdict)) {
      methodologyRepository.deleteAll(deletion.getOriginalMethodologies());
      monitoringPeriodRepository.deleteAll(deletion.getOriginalMonitoringPeriods());
      periodRepository.delete(deletion.getOriginal());
    }
    saveMethodologyUpdatesIfRequired(deletion, verdict);
  }

  private void updateCreditingPeriod(
      CreditingPeriodUpdate periodUpdate, ProjectUpdateVerdict verdict) {
    var period = periodUpdate.getUpdated();
    var methodologyUpdates = periodUpdatesConverter.toMethodologyUpdates(periodUpdate);
    var monitoringPeriodUpdates = periodUpdatesConverter.toMonitoringPeriodUpdates(periodUpdate);
    if (persistMethodologiesToProject(verdict)) {
      applyMethodologies(methodologyUpdates);
      applyMonitoringPeriods(monitoringPeriodUpdates);
      periodRepository.save(period);
    }
    saveMethodologyUpdatesIfRequired(periodUpdate, methodologyUpdates, verdict);
  }

  private void addCreditingPeriod(CreditingPeriodUpdate addition, ProjectUpdateVerdict verdict) {
    var period = addition.getUpdated();
    if (persistMethodologiesToProject(verdict)) {
      periodRepository.save(period);
      methodologyRepository.saveAll(period.getMethodologies());
    }
    saveMethodologyUpdatesIfRequired(addition, verdict);
  }

  private void applyMethodologies(Collection<MethodologyUpdate> updates) {
    updates.forEach(this::applyMethodology);
  }

  private void applyMethodology(MethodologyUpdate update) {
    if (update.isDeletion()) {
      methodologyRepository.delete(toMethodology(update, update.getOriginalVersion()));
      return;
    }
    methodologyRepository.save(toMethodology(update, update.getUpdatedVersion()));
  }

  private void applyMonitoringPeriods(Collection<MonitoringPeriodUpdate> updates) {
    updates.forEach(this::applyMonitoringPeriod);
  }

  private void applyMonitoringPeriod(MonitoringPeriodUpdate update) {
    if (update.isDeletion()) {
      monitoringPeriodRepository.delete(toMonitoringPeriod(update, update.getOriginalPeriod()));
      return;
    }
    monitoringPeriodRepository.save(toMonitoringPeriod(update, update.getUpdatedPeriod()));
  }

  private static ProjectCreditingPeriodMethodology toMethodology(
      MethodologyUpdate update, Double version) {
    return ProjectCreditingPeriodMethodology.builder()
        .projectId(update.getProjectId())
        .periodId(update.getPeriodId())
        .methodologyId(update.getMethodologyId())
        .methodology(new Methodology(update.getMethodologyId()))
        .version(version)
        .build();
  }

  private static ProjectCreditingMonitoringPeriod toMonitoringPeriod(
      MonitoringPeriodUpdate update, ProjectCreditingMonitoringPeriod period) {
    return ProjectCreditingMonitoringPeriod.builder()
        .projectId(update.getProjectId())
        .periodId(update.getPeriodId())
        .id(update.getId())
        .startDate(period.getStartDate())
        .endDate(period.getEndDate())
        .feeSchedule(period.getFeeSchedule())
        .build();
  }

  private void saveMethodologyUpdatesIfRequired(
      CreditingPeriodUpdate periodUpdate, ProjectUpdateVerdict verdict) {
    saveMethodologyUpdatesIfRequired(
        periodUpdate, periodUpdatesConverter.toMethodologyUpdates(periodUpdate), verdict);
  }

  private void saveMethodologyUpdatesIfRequired(
      CreditingPeriodUpdate periodUpdate,
      Collection<MethodologyUpdate> methodologyUpdates,
      ProjectUpdateVerdict verdict) {
    if (CollectionUtils.isEmpty(methodologyUpdates)) {
      return;
    }
    var savedProjectUpdate =
        projectMethodologyUpdateRepository.save(
            methodologyUpdateFactory.toProjectMethodologyUpdate(periodUpdate, verdict));
    methodologyUpdateRepository.saveAll(
        toDbMethodologyUpdates(savedProjectUpdate, methodologyUpdates));
  }

  private Collection<DbMethodologyUpdate> toDbMethodologyUpdates(
      ProjectMethodologyUpdate projectUpdate, Collection<MethodologyUpdate> updates) {
    return updates.stream()
        .map(update -> methodologyUpdateFactory.toDbMethodologyUpdate(projectUpdate, update))
        .toList();
  }

  private static ProjectUpdateVerdict calculateMethodologyUpdateVerdict(Request request) {
    if (request.hasRejectedStatus()) {
      return ProjectUpdateVerdict.REQUEST_NOT_APPROVED;
    }
    return ProjectUpdateVerdict.REQUEST_APPROVED;
  }

  private static boolean persistMethodologiesToProject(ProjectUpdateVerdict verdict) {
    return verdict.equals(ProjectUpdateVerdict.REQUEST_APPROVED)
        || verdict.equals(ProjectUpdateVerdict.PROJECT_FORCED);
  }
}
