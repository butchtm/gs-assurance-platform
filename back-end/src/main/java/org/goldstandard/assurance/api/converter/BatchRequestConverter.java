package org.goldstandard.assurance.api.converter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BatchProjectDTO;
import org.goldstandard.assurance.api.dto.BatchProjectsDTO;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryDTO;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.BatchRequests;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassificationSemantics;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.ProjectRiskSummaryService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchRequestConverter {

  private final PermissionsService permissionsService;
  private final ProjectRiskSummaryService projectRiskSummaryService;
  private final ProjectConverter projectConverter;

  public BatchProjectsDTO toPublicBatchProjects(BatchRequests batchRequests) {
    return toBatchProjects(batchRequests).clearCreditingPeriods();
  }

  public BatchProjectsDTO toBatchProjects(BatchRequests batchRequests) {
    return BatchProjectsDTO.builder()
        .parent(toBatchProject(batchRequests.getParent()))
        .children(toBatchProjects(batchRequests.getChildren()))
        .build();
  }

  private List<BatchProjectDTO> toBatchProjects(List<BatchRequest> requests) {
    return requests.stream().map(this::toBatchProject).toList();
  }

  private BatchProjectDTO toBatchProject(BatchRequest batchRequest) {
    var project = batchRequest.getProject();
    var request = batchRequest.getRequest();
    var builder =
        BatchProjectDTO.builder()
            .id(project.getId())
            .gsid(project.getGsid())
            .name(project.getName())
            .classification(
                ProjectClassificationSemantics.toDisplayDescription(
                    project.getProjectClassification()))
            .requestId(request.getId())
            .creditingPeriods(project.getCreditingPeriods())
            .methodologies(project.getMethodologies());
    if (permissionsService.isAdminOrArm()) {
      builder.currentRiskSummary(toBatchRiskSummary(project.getId()));
    }
    return builder.build();
  }

  private ProjectRiskSummaryDTO toBatchRiskSummary(Integer projectId) {
    return projectConverter.toBasicProjectRiskSummaryDTO(
        projectRiskSummaryService.getCurrentRiskSummary(projectId));
  }
}
