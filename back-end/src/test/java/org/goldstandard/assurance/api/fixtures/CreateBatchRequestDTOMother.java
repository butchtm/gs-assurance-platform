package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO.BatchRequestCreateDTOBuilder;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBatchRequestDTOMother {

  public static BatchRequestCreateDTO createPoAAndRealCaseBatchDTO() {
    var projectPoa = TEST_PROJECTS.getFirst();
    var projectRealCase = TEST_PROJECTS.get(1);
    return createBatchDTO(projectPoa, projectRealCase);
  }

  public static BatchRequestCreateDTO createBatchDTO(Project... projects) {
    return batchDtoBuilder().projectRequestInfos(toBatchProjects(List.of(projects))).build();
  }

  private static BatchRequestCreateDTOBuilder batchDtoBuilder() {
    return BatchRequestCreateDTO.builder().batchTypeId(0).requestTypeId(1008);
  }

  private static List<BatchRequestCreateDTO.ProjectRequestInfo> toBatchProjects(
      List<Project> projects) {
    return projects.stream().map(CreateBatchRequestDTOMother::toBatchProject).toList();
  }

  private static BatchRequestCreateDTO.ProjectRequestInfo toBatchProject(Project project) {
    int projectId = project.getId();
    int creditingPeriodId =
        project.getCreditingPeriods().stream()
            .map(ProjectCreditingPeriod::getPeriodId)
            .findFirst()
            .orElse(1);
    return BatchRequestCreateDTO.ProjectRequestInfo.builder()
        .projectId(projectId)
        .creditingPeriod(new CreditingPeriodDTO(creditingPeriodId))
        .emissionReductionAmount(projectId * 10)
        .methodologies(createRequestMethodologies(creditingPeriodId))
        .build();
  }

  private static List<RequestMethodologyDTO> createRequestMethodologies(int creditingPeriodId) {
    var methodologyDTO =
        RequestMethodologyDTO.builder()
            .creditingPeriodId(creditingPeriodId)
            .methodologyId(1)
            .methodologyVersion(1.2)
            .build();
    return List.of(methodologyDTO);
  }
}
