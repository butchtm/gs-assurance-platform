package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.goldstandard.assurance.api.converter.BatchRequestConverter;
import org.goldstandard.assurance.api.converter.ProjectConverter;
import org.goldstandard.assurance.api.dto.BatchProjectDTO;
import org.goldstandard.assurance.api.dto.BatchProjectsDTO;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryDTO;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.BatchRequests;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectRiskSummary;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.ProjectRiskSummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchRequestConverterTest {
  @InjectMocks BatchRequestConverter converter;
  @Mock PermissionsService permissionsService;
  @Mock ProjectRiskSummaryService projectRiskSummaryService;
  @Mock ProjectConverter projectConverter;

  @Test
  void shouldNotConvertCreditingPeriodsForBatchParentRequestToPublicParentProject() {
    BatchRequests batchRequests = buildBatchRequests();

    BatchProjectsDTO batchProjects = converter.toPublicBatchProjects(batchRequests);

    var parent = batchProjects.getParent();
    assertThat(parent.getCreditingPeriods()).isEmpty();
  }

  @Test
  void shouldNotConvertCreditingPeriodsConvertBatchChildRequestsToPublicChildProjects() {
    BatchRequests batchRequests = buildBatchRequests();

    BatchProjectsDTO batchProjects = converter.toPublicBatchProjects(batchRequests);

    var children = batchProjects.getChildren();
    assertThat(children)
        .map(BatchProjectDTO::getCreditingPeriods)
        .containsExactly(Collections.emptyList(), Collections.emptyList());
  }

  @Test
  void shouldConvertBatchParentRequestToParentProject() {
    BatchRequests batchRequests = buildBatchRequests();

    BatchProjectsDTO batchProjects = converter.toBatchProjects(batchRequests);

    var parent = batchProjects.getParent();
    assertThat(parent.getId()).isEqualTo(2);
    assertThat(parent.getGsid()).isEqualTo("GS2");
    assertThat(parent.getName()).isEqualTo("Project 2");
    assertThat(parent.getClassification()).isEqualTo("Class 2");
    assertThat(parent.getRequestId()).isEqualTo(22);
    assertThat(parent.getCreditingPeriods()).hasSize(2);
  }

  @Test
  void shouldConvertBatchChildRequestsToChildProjects() {
    BatchRequests batchRequests = buildBatchRequests();

    BatchProjectsDTO batchProjects = converter.toBatchProjects(batchRequests);

    var children = batchProjects.getChildren();

    var child1 = children.getFirst();
    assertThat(child1.getId()).isEqualTo(1);
    assertThat(child1.getGsid()).isEqualTo("GS1");
    assertThat(child1.getName()).isEqualTo("Project 1");
    assertThat(child1.getClassification()).isEqualTo("Class 1");
    assertThat(child1.getRequestId()).isEqualTo(11);
    assertThat(child1.getCreditingPeriods()).hasSize(1);
    var child2 = children.get(1);
    assertThat(child2.getId()).isEqualTo(3);
    assertThat(child2.getGsid()).isEqualTo("GS3");
    assertThat(child2.getName()).isEqualTo("Project 3");
    assertThat(child2.getClassification()).isEqualTo("Class 3");
    assertThat(child2.getRequestId()).isEqualTo(33);
    assertThat(child2.getCreditingPeriods()).hasSize(3);
  }

  @Test
  void shouldIncludeRiskSummaryForAdminUsers() {
    BatchRequests batchRequests = buildBatchRequests();
    var mockRiskSummary = buildRiskSummary();
    when(permissionsService.isAdminOrArm()).thenReturn(true);
    when(projectRiskSummaryService.getCurrentRiskSummary(2)).thenReturn(mockRiskSummary);
    when(projectConverter.toBasicProjectRiskSummaryDTO(mockRiskSummary))
        .thenReturn(toRiskSummaryDto(mockRiskSummary));

    BatchProjectsDTO batchProjects = converter.toBatchProjects(batchRequests);

    var parent = batchProjects.getParent();
    assertThat(parent.getCurrentRiskSummary().getSummary())
        .isEqualTo("This is a high risk summary");
    assertThat(parent.getCurrentRiskSummary().getRisk()).isTrue();
  }

  @Test
  void shouldNotIncludeRiskSummaryForMostUsers() {
    BatchRequests batchRequests = buildBatchRequests();
    when(permissionsService.isAdminOrArm()).thenReturn(false);

    BatchProjectsDTO batchProjects = converter.toBatchProjects(batchRequests);

    var parent = batchProjects.getParent();
    assertThat(parent.getCurrentRiskSummary()).isNull();
  }

  private static BatchRequests buildBatchRequests() {
    var parentRequest = buildRequest(22, buildProject(2));
    var request1 = toBatchRequest(buildRequest(11, buildProject(1)));
    var request2 = toBatchRequest(parentRequest);
    var request3 = toBatchRequest(buildRequest(33, buildProject(3)));
    return BatchRequests.builder()
        .batch(buildBatch(parentRequest))
        .requests(List.of(request1, request2, request3))
        .build();
  }

  private static Batch buildBatch(Request parentRequest) {
    var batch = new Batch();
    batch.setParentRequest(parentRequest);
    return batch;
  }

  private static Request buildRequest(int id, Project project) {
    var request = new Request();
    request.setId(id);
    request.setProject(project);
    return request;
  }

  private static Project buildProject(int id) {
    var project = new Project();
    project.setId(id);
    project.setGsid(String.format("GS%d", id));
    project.setName(String.format("Project %d", id));
    project.setProjectClassification(buildClassification(id));
    project.setCreditingPeriods(
        IntStream.range(0, id).mapToObj(i -> new ProjectCreditingPeriod()).toList());
    return project;
  }

  private static ProjectClassification buildClassification(int id) {
    var classification = new ProjectClassification();
    classification.setDescription(String.format("Class %d", id));
    return classification;
  }

  private static BatchRequest toBatchRequest(Request request) {
    var batchRequest = new BatchRequest();
    batchRequest.setRequest(request);
    batchRequest.setProject(request.getProject());
    return batchRequest;
  }

  private static ProjectRiskSummary buildRiskSummary() {
    return ProjectRiskSummary.builder()
        .risk(true)
        .summary("This is a high risk summary")
        .id(1)
        .build();
  }

  private static ProjectRiskSummaryDTO toRiskSummaryDto(ProjectRiskSummary riskSummary) {
    return ProjectRiskSummaryDTO.builder()
        .summary(riskSummary.getSummary())
        .risk(riskSummary.getRisk())
        .build();
  }
}
