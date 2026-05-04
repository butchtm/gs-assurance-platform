package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectRiskSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRiskSummaryRepository extends JpaRepository<ProjectRiskSummary, Integer> {
  List<ProjectRiskSummary> findByProject(Project project);

  Page<ProjectRiskSummary> findByProject(Project project, Pageable pageable);

  List<ProjectRiskSummary> findByProjectId(Integer projectId);

  List<ProjectRiskSummary> findAllByProjectId(Integer projectId);

  Page<ProjectRiskSummary> findByProjectId(Integer projectId, Pageable pageable);
}
