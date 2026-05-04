package org.goldstandard.assurance.api.service;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;

@RequiredArgsConstructor
@Slf4j
public class ProjectStatusUpdateRequiredPredicate implements Predicate<Project> {

  private final ProjectStatus status;

  @Override
  public boolean test(Project project) {
    var currentStatus = project.getProjectStatus();
    boolean required = status.isAfter(currentStatus);
    log.info(
        "status update required {} for project {} with current status {} to new status {}",
        required,
        project.getId(),
        currentStatus.getId(),
        status.getId());
    return required;
  }
}
