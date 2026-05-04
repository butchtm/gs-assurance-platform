package org.goldstandard.assurance.api.helper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.repository.ProjectRiskSummaryRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RiskSummaryCleanup {
  private final ProjectRiskSummaryRepository projectRiskSummaryRepository;

  @Transactional
  public void deleteAllByProjectId(Integer projectId) {
    var riskSummaries = projectRiskSummaryRepository.findAllByProjectId(projectId);
    for (var riskSummary : riskSummaries) {
      projectRiskSummaryRepository.delete(riskSummary);
    }
  }
}
