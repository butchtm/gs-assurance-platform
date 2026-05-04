package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryCreateDTO;
import org.goldstandard.assurance.api.entity.ProjectRiskSummary;
import org.goldstandard.assurance.api.repository.ProjectRiskSummaryRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectRiskSummaryService {
  private final ProjectRiskSummaryRepository projectRiskSummaryRepository;
  private final ProjectFinder projectFinder;
  private final AuthenticatedUserSupplier authenticatedUserSupplier;

  public ProjectRiskSummary create(Integer projectId, ProjectRiskSummaryCreateDTO dto) {
    var project = projectFinder.forceGetProjectById(projectId);
    ProjectRiskSummary projectRiskSummary =
        ProjectRiskSummary.builder()
            .project(project)
            .risk(dto.getRisk())
            .summary(dto.getSummary())
            .dateApplied(dto.getDateApplied())
            .user(authenticatedUserSupplier.forceGetAuthenticatedUser())
            .build();
    return projectRiskSummaryRepository.save(projectRiskSummary);
  }

  public Page<ProjectRiskSummary> getRiskSummary(Integer projectId, Pageable pageable) {
    return projectRiskSummaryRepository.findByProjectId(projectId, pageable);
  }

  public ProjectRiskSummary getCurrentRiskSummary(Integer projectId) {
    List<ProjectRiskSummary> summaries = projectRiskSummaryRepository.findByProjectId(projectId);
    return summaries.stream()
        .max((a, b) -> a.getDateApplied().compareTo(b.getDateApplied()))
        .orElse(null);
  }
}
