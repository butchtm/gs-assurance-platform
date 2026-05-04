package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectMethodologyUpdate;
import org.goldstandard.assurance.api.repository.ProjectMethodologyUpdateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodologyHistoryService {

  private final ProjectMethodologyUpdateSpecFactory specFactory;
  private final ProjectMethodologyUpdateRepository repository;
  private final ProjectFinder projectFinder;

  public Page<ProjectMethodologyUpdate> getHistory(
      Integer projectId, Integer periodId, Pageable pageable) {
    var project = projectFinder.forceGetProjectById(projectId);
    Specification<ProjectMethodologyUpdate> spec = specFactory.build(projectId, periodId);
    return repository.findAll(spec, pageable).map(entry -> entry.withGsid(project.getGsid()));
  }
}
