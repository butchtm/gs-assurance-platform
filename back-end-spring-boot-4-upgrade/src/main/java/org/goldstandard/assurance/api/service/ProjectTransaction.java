package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.AuditEventSubject.PROJECT_CREATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.PROJECT_STATUS_UPDATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.PROJECT_UPDATE;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.service.pii.PiiMetadataService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProjectTransaction {

  private final ProjectRepository projectRepository;
  private final CreditingPeriodTransaction creditingPeriodTransaction;
  private final AuditService auditService;
  private final PiiMetadataService metadataService;
  private final ProjectTransactionChildUpdater childUpdater;

  @Transactional
  public Project performCreate(
      Project project,
      List<ProjectCreditingPeriod> creditingPeriods,
      TextAnalysisResultsDTO probablePii) {
    return performSave(project, creditingPeriods, PROJECT_CREATE, probablePii);
  }

  @Transactional
  public Collection<Project> performUpdate(
      Project originalProject,
      Project updatedProject,
      List<ProjectCreditingPeriod> creditingPeriods,
      TextAnalysisResultsDTO probablePii) {
    var savedProject = performSave(updatedProject, creditingPeriods, PROJECT_UPDATE, probablePii);
    var savedChildren = childUpdater.updateChildrenIfRequired(originalProject, updatedProject);
    return Stream.concat(Stream.of(savedProject), savedChildren.stream()).toList();
  }

  @Transactional
  public Collection<Project> performStatusUpdate(Collection<Project> projects) {
    var savedProjects = IterableUtils.toList(projectRepository.saveAll(projects));
    auditService.auditEvent(PROJECT_STATUS_UPDATE, savedProjects);
    return savedProjects;
  }

  private Project performSave(
      Project project,
      List<ProjectCreditingPeriod> creditingPeriods,
      AuditEventSubject subject,
      TextAnalysisResultsDTO probablePii) {
    var savedProject = projectRepository.save(project);
    saveProbablePiiMetadata(probablePii, savedProject);
    saveCreditingPeriods(savedProject, creditingPeriods);
    savedProject.setCreditingPeriods(creditingPeriods);
    auditService.auditEvent(subject, savedProject);
    return savedProject;
  }

  private void saveProbablePiiMetadata(TextAnalysisResultsDTO probablePii, Project savedProject) {
    if (savedProject.isNew() && Objects.nonNull(probablePii)) {
      metadataService.persistProjectPiiMetadata(probablePii, savedProject);
    }
  }

  private void saveCreditingPeriods(Project project, Collection<ProjectCreditingPeriod> periods) {
    var updatedPeriods = periods.stream().map(period -> period.withProject(project)).toList();
    creditingPeriodTransaction.apply(project.getCreditingPeriods(), updatedPeriods);
  }
}
