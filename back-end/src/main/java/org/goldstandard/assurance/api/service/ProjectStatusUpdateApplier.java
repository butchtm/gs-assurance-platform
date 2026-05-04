package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.repository.ProjectStatusRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectStatusUpdateApplier {

  private final ProjectStatusRepository repository;
  private final Clock clock;

  public Collection<Project> forceUpdateProjectStatuses(
      Collection<Project> projects, Integer statusId) {
    var status = repository.forceFindById(statusId);
    var now = ZonedDateTime.now(clock);
    return updateProjectStatusesIfRequired(projects, status, now, project -> true);
  }

  public Collection<Project> updateProjectStatusesIfRequired(
      Collection<Project> projects, Integer statusId, ZonedDateTime date) {
    var status = repository.forceFindById(statusId);
    return updateProjectStatusesIfRequired(projects, status, date);
  }

  public Collection<Project> updateProjectStatusesIfRequired(
      Collection<Project> projects, ProjectStatus status, ZonedDateTime date) {
    var predicate = new ProjectStatusUpdateRequiredPredicate(status);
    return updateProjectStatusesIfRequired(projects, status, date, predicate);
  }

  private Collection<Project> updateProjectStatusesIfRequired(
      Collection<Project> projects,
      ProjectStatus status,
      ZonedDateTime date,
      Predicate<Project> updateRequired) {
    return projects.stream()
        .map(project -> updateProjectStatusIfRequired(project, status, date, updateRequired))
        .toList();
  }

  private Project updateProjectStatusIfRequired(
      Project project,
      ProjectStatus status,
      ZonedDateTime date,
      Predicate<Project> updateRequired) {
    if (updateRequired.test(project)) {
      return project.withProjectStatus(status, date);
    }
    return project;
  }
}
