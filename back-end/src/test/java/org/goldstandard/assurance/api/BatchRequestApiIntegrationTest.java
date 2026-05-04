package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.fixtures.CreateBatchRequestDTOMother.createBatchDTO;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;
import static org.goldstandard.assurance.api.service.AuditEventSubject.BATCH_CREATED;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.dto.BatchProjectDTO;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.helper.BatchRequestCleanup;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.repository.*;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Import(RequestHelper.class)
class BatchRequestApiIntegrationTest extends AbstractApiTest {

  public static final String ENDPOINT_TO_TEST = "batch-requests";
  @Autowired RequestRepository requestRepository;

  @Autowired AuditRepository auditRepository;

  @Autowired BatchRequestRepository batchRequestRepository;

  @Autowired BatchRepository batchRepository;

  @Autowired ApiClient client;

  @Autowired BatchRequestCleanup cleanup;

  @Autowired ProjectRepository projectRepository;

  @Autowired ProjectCreditingPeriodRepository projectCreditingPeriodRepository;

  @Autowired ProjectPopulator projectPopulator;

  @Autowired JsonConverter jsonConverter;

  @Test
  void candidateProjectsContainOverview() {
    var projectPoa = createAndSaveTestProject(7, null);
    var realCase = createAndSaveTestProject(8, projectPoa);
    var body =
        GetBatchCandidateProjectsDTO.builder()
            .projectId(projectPoa.getId())
            .batchTypeId(0)
            .requestTypeId(1000)
            .build();
    var requestEntity = this.getHttpEntityWithTokenAndBodyForRole(body, ADMIN_ROLE);

    try {
      var response =
          this.restTemplate.exchange(
              String.format("%s/%s/%s", baseUrl, ENDPOINT_TO_TEST, "candidate-projects"),
              HttpMethod.POST,
              requestEntity,
              new ParameterizedTypeReference<List<ProjectDTO>>() {});
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertNotNull(response.getBody());
      var candidate = response.getBody().getFirst();

      assertThat(candidate.getName()).isEqualTo(realCase.getName());
      assertThat(candidate.getProjectStatus().getId())
          .isEqualTo(realCase.getProjectStatus().getId());
      assertThat(candidate.getProjectClassificationId())
          .isEqualTo(realCase.getProjectClassification().getId());
      assertThat(candidate.getMasterProject().getId()).isEqualTo(projectPoa.getId());
      assertThat(candidate.getParentProject().getId()).isEqualTo(projectPoa.getId());
      assertThat(candidate.getCreditingPeriods().size())
          .isEqualTo(realCase.getCreditingPeriods().size());
    } finally {
      projectPopulator.deleteProject(realCase.getId());
      projectPopulator.deleteProject(projectPoa.getId());
    }
  }

  @Test
  void createNewBatchRequest() {
    var projectPoa = TEST_PROJECTS.getFirst();
    var projectRealCase = TEST_PROJECTS.get(1);
    var createDTO = createBatchDTO(projectPoa, projectRealCase);

    var response = client.createBatchRequest(createDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var batchId = response.getBody();

    try {
      var savedBatch = batchRepository.findById(batchId).orElseThrow();
      var savedBatchRequests = batchRequestRepository.findAllByBatchId(batchId);
      assertThat(savedBatchRequests)
          .map(BatchRequest::getRequest)
          .map(Request::getProject)
          .map(Project::getId)
          .containsExactly(projectPoa.getId(), projectRealCase.getId());
      savedBatchRequests.forEach(
          savedBatchRequest -> {
            var savedRequest =
                requestRepository.findById(savedBatchRequest.getRequest().getId()).orElseThrow();
            assertThat(savedRequest.getBatch().getId()).isEqualTo(savedBatch.getId());
          });
      var auditEvent = findBatchCreatedAuditEvent(savedBatch.getId());
      assertThat(auditEvent).isPresent();
      assertThat(jsonConverter.toMap(auditEvent.get().getAdditionalInformation()))
          .hasSize(2)
          .containsEntry("batchId", savedBatch.getId())
          .containsEntry("parentRequestId", savedBatch.getParentRequest().getId());
    } finally {
      cleanup.deleteBatchRequests(batchId);
    }
  }

  @ParameterizedTest
  @MethodSource("requestTypeIdsProjectsAndExpectedBatchTypeIds")
  void getBatchTypeByRequestTypeAndProjectId(
      int requestTypeId, Project project, List<Integer> expectedBatchTypeIds) {
    var getDTO = new GetBatchTypesDTO(requestTypeId, project.getId());

    var response = client.getBatchTypes(getDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var batchTypes = response.getBody();
    assertThat(batchTypes)
        .map(ReferenceDataType::getId)
        .containsExactlyElementsOf(expectedBatchTypeIds);
  }

  @Test
  void getAllProjectsInBatchByBatchIdForPublicPage() {
    var projectPoa = TEST_PROJECTS.getFirst();
    var projectRealCase = TEST_PROJECTS.get(1);
    var createDTO = createBatchDTO(projectPoa, projectRealCase);
    var createResponse = client.createBatchRequest(createDTO);
    var batchId = createResponse.getBody();

    try {
      var response = client.getPublicBatchProjects(batchId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var batchProjects = response.getBody();
      var parent = batchProjects.getParent();
      assertThat(parent.getId()).isEqualTo(projectPoa.getId());
      assertThat(parent.getCreditingPeriods()).isEmpty();
      var children = batchProjects.getChildren();
      assertThat(children).map(BatchProjectDTO::getId).containsExactly(projectRealCase.getId());
      assertThat(children).map(BatchProjectDTO::getCreditingPeriods).allMatch(Collection::isEmpty);
    } finally {
      cleanup.deleteBatchRequests(batchId);
    }
  }

  @Test
  void getAllProjectsInBatchByBatchIdForPrivatePage() {
    var projectPoa = TEST_PROJECTS.getFirst();
    var projectRealCase = TEST_PROJECTS.get(1);
    var createDTO = createBatchDTO(projectPoa, projectRealCase);
    var createResponse = client.createBatchRequest(createDTO);
    var batchId = createResponse.getBody();

    try {
      var response = client.getBatchProjects(batchId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var batchProjects = response.getBody();
      var parent = batchProjects.getParent();
      assertThat(parent.getId()).isEqualTo(projectPoa.getId());
      assertThat(parent.getCreditingPeriods()).hasSize(1);
      var children = batchProjects.getChildren();
      assertThat(children).map(BatchProjectDTO::getId).containsExactly(projectRealCase.getId());
      assertThat(children)
          .map(BatchProjectDTO::getCreditingPeriods)
          .map(Collection::size)
          .containsExactly(1);
    } finally {
      cleanup.deleteBatchRequests(batchId);
    }
  }

  @Test
  void returnsNotFoundWhenBatchIdDoesNotExist() {
    var batchId = 100;

    var response = client.getPublicBatchProjects(batchId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  private Optional<AuditEvent> findBatchCreatedAuditEvent(int batchId) {
    return auditRepository.findAllByEventSubject(BATCH_CREATED.name()).stream()
        .filter(event -> isForBatchId(event, batchId))
        .findFirst();
  }

  private Project createAndSaveTestProject(Integer classificationId, Project poa) {
    var project = new Project();
    project.setName("Test Project");
    project.setProjectClassification(new ProjectClassification(classificationId));
    project.setAccountId(1);
    project.setProjectStatus(ProjectStatus.newStatus());
    if (!Objects.isNull(poa)) {
      project.setMasterProject(poa);
      project.setParentProject(poa);
    }
    var saved = projectRepository.save(project);
    var periods = List.of(projectPopulator.buildCreditingPeriod(saved, ZonedDateTime.now()));
    saved.setCreditingPeriods(periods);
    projectCreditingPeriodRepository.saveAll(periods);
    return saved;
  }

  private boolean isForBatchId(AuditEvent event, int batchId) {
    return Objects.equals(
        jsonConverter.toMap(event.getAdditionalInformation()).getOrDefault("batchId", -1), batchId);
  }

  private static Stream<Arguments> requestTypeIdsProjectsAndExpectedBatchTypeIds() {
    int requestTypeId = 1000;
    return Stream.of(
        Arguments.of(requestTypeId, TEST_PROJECTS.getFirst(), List.of(0, 1)),
        Arguments.of(requestTypeId, TEST_PROJECTS.get(1), List.of(0)),
        Arguments.of(requestTypeId, TEST_PROJECTS.get(2), List.of()));
  }
}
