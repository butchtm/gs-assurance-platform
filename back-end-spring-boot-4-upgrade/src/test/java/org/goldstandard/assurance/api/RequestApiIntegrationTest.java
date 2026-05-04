package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory.REQUIRED_DISPLAY_CATEGORY_ID;
import static org.goldstandard.assurance.api.fixtures.CreateBatchRequestDTOMother.createPoAAndRealCaseBatchDTO;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;
import static org.goldstandard.assurance.api.populator.RequestPopulator.NUMBER_OF_REQUESTS_PER_PROJECT;
import static org.goldstandard.assurance.api.populator.UserPopulator.ASSIGNED_ACCOUNT1_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.service.AuditEventSubject.PROJECT_UPDATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_UPDATE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.BatchRequests;
import org.goldstandard.assurance.api.entity.DigitalFormType;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestStatusHistory;
import org.goldstandard.assurance.api.entity.RequestTypeDocumentType;
import org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.CreditingPeriodDTOMother;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.goldstandard.assurance.api.helper.AuditCleanup;
import org.goldstandard.assurance.api.helper.BatchRequestCleanup;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.populator.PiiMetadataPopulator;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.RequestTypeDocumentTypeRepository;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(RequestHelper.class)
class RequestApiIntegrationTest extends AbstractApiTest {

  @MockitoBean RequestTypeDocumentTypeRepository requestTypeDocumentTypeRepository;

  @Autowired RequestRepository requestRepository;

  @Autowired RequestStatusHistoryRepository requestStatusHistoryRepository;

  @Autowired AuditRepository auditRepository;

  @Autowired UserRepository userRepository;

  @Autowired ProjectRepository projectRepository;

  @Autowired RequestHelper requestHelper;

  @Autowired BatchRequestFinder batchRequestFinder;

  @Autowired BatchRequestCleanup batchRequestCleanup;

  @Autowired AuditCleanup auditCleanup;

  @Autowired ProjectPopulator projectPopulator;

  @Autowired ApiClient client;

  @Autowired JsonConverter jsonConverter;
  @Autowired PiiMetadataPopulator piiMetadataPopulator;
  @Autowired PiiMetadataRepository piiMetadataRepository;

  private static List<RequestTypeDocumentType> createRequiredDocumentTypes(int requestTypeId) {
    var formType = new DigitalFormType(0);
    return IntStream.range(0, 2)
        .mapToObj(
            i ->
                RequestTypeDocumentType.builder()
                    .requestTypeId(requestTypeId)
                    .documentTypeId(i)
                    .documentType(
                        new DocumentType(i, String.format("Document Type Description %s", i)))
                    .documentDisplayCategoryId(1)
                    .documentDisplayCategory(new DocumentDisplayCategory(1, "REQUIRED"))
                    .formTypeId(formType.getId())
                    .formType(formType)
                    .build())
        .toList();
  }

  private static Collection<Integer> toAllProjectIds(BatchRequests batchRequests) {
    var parentProjectId = batchRequests.getBatch().getParentRequest().getProject().getId();
    var childProjectIds =
        batchRequests.getChildren().stream().map(BatchRequest::getProject).map(Project::getId);
    return Stream.concat(Stream.of(parentProjectId), childProjectIds).toList();
  }

  private static String toString(Collection<Integer> ids) {
    return ids.stream().map(id -> Integer.toString(id)).collect(Collectors.joining(","));
  }

  @AfterEach
  void cleanupAudit() {
    auditCleanup.deleteAllAuditEvents();
  }

  @Test
  void testGetRequestsByProjectIdList() {
    var response = client.getRequestsByProjectId(1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    var requests = response.getBody().getContent();
    assertNotNull(requests);
    assertThat(requests).hasSize(NUMBER_OF_REQUESTS_PER_PROJECT);

    var lastRequest = requests.getLast();
    assertThat(lastRequest.getId()).isEqualTo(1);
    assertThat(lastRequest.getReviewName()).isEqualTo("review name 1");
    assertThat(lastRequest.getCreditingPeriod().getPeriodId()).isEqualTo(1);
    assertThat(lastRequest.getIndex()).isZero();
    assertThat(lastRequest.getManuallyAdded()).isTrue();
  }

  @Test
  void testGetAllRequestsForAdmin() {
    var response = client.getRequestsForRole(ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    var overviews = response.getBody().getContent();
    assertNotNull(overviews);
    assertThat(overviews).hasSize(TEST_PROJECTS.size() * NUMBER_OF_REQUESTS_PER_PROJECT);

    var lastOverview = overviews.getLast();
    assertThat(lastOverview.getId()).isEqualTo(1);
    assertThat(lastOverview.getReviewName()).isEqualTo("review name 1");
    assertThat(lastOverview.getCreditingPeriod().getPeriodId()).isEqualTo(1);
    assertThat(lastOverview.getIndex()).isZero();
    assertThat(lastOverview.getManuallyAdded()).isTrue();
  }

  @Test
  void testGetRequestsForUnauthenticatedErrors() {
    var response = client.getRequestsUnauthenticated();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testGetRequestsForNonAdminReturnsRequestsLinkedByAssignments() {
    var users = userRepository.findAll();
    var account1User =
        users.stream()
            .filter(u -> u.getAccount() != null && u.getAccount().getId() == 1)
            .findFirst()
            .orElseThrow();

    var response = client.getRequestsForUser(PROJECT_DEVELOPER_ROLE, account1User.getIdpUserId());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    var overviews = response.getBody().getContent();
    assertNotNull(overviews);
    assertThat(overviews).hasSize(2);

    var lastOverview = overviews.getLast();
    assertThat(lastOverview.getId()).isEqualTo(1);
    assertThat(lastOverview.getReviewName()).isEqualTo("review name 1");
    assertThat(lastOverview.getCreditingPeriod().getPeriodId()).isEqualTo(1);
    assertThat(lastOverview.getIndex()).isZero();
    assertThat(lastOverview.getManuallyAdded()).isTrue();
  }

  @Test
  void testGetRequest() {
    var response = client.getRequestById(1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());

    final var overview = response.getBody();

    assertThat(overview.getId()).isEqualTo(1);
    assertThat(overview.getReviewName()).isEqualTo("review name 1");
    assertThat(overview.getCreditingPeriod().getPeriodId()).isEqualTo(1);
    assertThat(overview.getIndex()).isZero();
    assertThat(overview.getManuallyAdded()).isTrue();
    assertThat(overview.getInvoiceId()).isEqualTo(1);
  }

  @Test
  void testCreateRequest() {
    var now = ZonedDateTime.now();
    var create = requestHelper.buildCreateRequestDTO(now);
    var response = client.createRequest(1, create);
    var requestId = Objects.requireNonNull(response.getBody());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    try {
      assertThat(requestId).isPositive();

      final var createdRequestOpt = requestRepository.findById(requestId);

      assertThat(createdRequestOpt).isPresent();

      final var createdRequest = createdRequestOpt.get();
      assertThat(createdRequest.getId()).isEqualTo(requestId);
      assertThat(createdRequest.getCurrentRequestStatus().getId()).isEqualTo(6);
      assertThat(createdRequest.getRequestName()).isEqualTo("Preliminary Review no. 2");
      assertThat(createdRequest.getRequestType().getId()).isEqualTo(create.getRequestTypeId());
      assertThat(createdRequest.getCreditingPeriod())
          .isEqualTo(create.getCreditingPeriod().getPeriodId());
      assertThat(createdRequest.getEmissionReductionAmount())
          .isEqualTo(create.getEmissionReductionAmount());
      assertThat(createdRequest.getManuallyAdded()).isEqualTo(create.getManuallyAdded());
      assertThat(createdRequest.getStartDate().toEpochSecond()).isEqualTo(now.toEpochSecond());
      assertThat(createdRequest.getProject().getId()).isEqualTo(1);

      List<AuditEvent> auditEvents =
          auditRepository.findAllByEventSubject(AuditEventSubject.REQUEST_CREATE.name());
      assertThat(auditEvents).hasSize(1);
      var auditEvent = auditEvents.getFirst();
      assertThat(jsonConverter.toMap(auditEvent.getAdditionalInformation()))
          .hasSize(2)
          .contains(entry("projectId", 1), entry("requestId", requestId));
    } finally {
      requestHelper.deleteTestRequest(requestId);
    }
  }

  @Test
  void testUpdateRequest() {
    var projectId = 1;
    var request = requestHelper.createTestRequest(projectId);

    try {
      var now = ZonedDateTime.now().plusSeconds(120);
      var update =
          RequestCreateUpdateDTOMother.updateBuilder()
              .startDate(now)
              .id(request.getId())
              .creditingPeriod(CreditingPeriodDTOMother.withId(9))
              .emissionReductionAmount(789)
              .requestStatusId(2)
              .fastTrack(false)
              .internal(true)
              .manuallyAdded(false)
              .build();

      var updateResponse = client.updateRequest(projectId, update);

      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertNotNull(updateResponse.getBody());
      assertThat(updateResponse.getBody()).isPositive();

      var updatedRequestOpt = requestRepository.findById(updateResponse.getBody());
      assertThat(updatedRequestOpt).isPresent();

      var updatedRequest = updatedRequestOpt.get();
      assertThat(updatedRequest.getId()).isEqualTo(request.getId());
      assertThat(updatedRequest.getCurrentRequestStatus().getId())
          .isEqualTo(update.getRequestStatusId());
      assertThat(updatedRequest.getRequestName()).isEqualTo("test request no. 1");
      assertThat(updatedRequest.getRequestType().getId())
          .isEqualTo(request.getRequestType().getId());
      assertThat(updatedRequest.getCreditingPeriod())
          .isEqualTo(update.getCreditingPeriod().getPeriodId());
      assertThat(updatedRequest.getEmissionReductionAmount())
          .isEqualTo(update.getEmissionReductionAmount());
      assertThat(updatedRequest.getManuallyAdded()).isEqualTo(update.getManuallyAdded());
      assertThat(updatedRequest.getFastTracked()).isEqualTo(update.getFastTrack());
      assertThat(updatedRequest.getInternal()).isEqualTo(update.getInternal());
      assertThat(updatedRequest.getStartDate().toEpochSecond()).isEqualTo(now.toEpochSecond());

      var updated =
          requestRepository.findByProjectId(1).stream()
              .filter(
                  r ->
                      r.getRequestType().getId().intValue()
                          == request.getRequestType().getId().intValue())
              .findFirst();
      assertThat(updated).isPresent();

      var auditEvents =
          auditRepository.findAllByEventSubject(REQUEST_UPDATE.name()).stream()
              .filter(
                  e ->
                      jsonConverter
                          .toMap(e.getAdditionalInformation())
                          .get("requestId")
                          .equals(request.getId()))
              .toList();
      assertThat(auditEvents).hasSize(1);
      assertThat(jsonConverter.toMap(auditEvents.getFirst().getAdditionalInformation()))
          .contains(entry("projectId", 1));
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void shouldUpdateProjectStatusWhenRequestStatusUpdatedToCompletedApproved() {
    var project = ProjectMother.withIdOnly(1);
    var request = requestHelper.createTestRequestWithRequestStatusAndType(project, 10, false, 1000);

    try {
      var update =
          RequestCreateUpdateDTOMother.updateBuilder()
              .startDate(ZonedDateTime.now().plusSeconds(120))
              .id(request.getId())
              .requestStatusId(4)
              .build();

      var response = client.updateRequest(project.getId(), update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).isPositive();

      var updatedProject = projectRepository.findById(project.getId());
      var expectedProjectStatusId = 2;
      assertThat(updatedProject)
          .map(Project::getProjectStatus)
          .map(ReferenceDataType::getId)
          .contains(expectedProjectStatusId);

      var auditEvents = auditRepository.findAllByEventSubject(PROJECT_UPDATE.name());
      assertThat(auditEvents).hasSize(1);
      assertThat(jsonConverter.toMap(auditEvents.getFirst().getAdditionalInformation()))
          .contains(
              entry("projectIds", Integer.toString(project.getId())),
              entry("statusIds", Integer.toString(expectedProjectStatusId)));
    } finally {
      requestHelper.deleteTestRequest(request);
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldUpdateAllProjectStatusesWhenBatchRequestStatusUpdatedToCompletedApproved() {
    var batchRequest = createBatchRequest();
    try {
      var update =
          RequestCreateUpdateDTOMother.updateBuilder()
              .startDate(ZonedDateTime.now().plusSeconds(120))
              .id(batchRequest.getParent().getRequest().getId())
              .requestStatusId(4)
              .build();

      var response = client.updateRequest(batchRequest.getParent().getProject().getId(), update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).isPositive();

      var allProjectIds = toAllProjectIds(batchRequest);
      var updatedProjects = projectRepository.findAllById(allProjectIds);
      var expectedProjectStatusId = 3;
      assertThat(updatedProjects)
          .map(Project::getProjectStatus)
          .map(ReferenceDataType::getId)
          .containsExactly(expectedProjectStatusId, expectedProjectStatusId);

      var auditEvents = auditRepository.findAllByEventSubject(PROJECT_UPDATE.name());
      assertThat(auditEvents).hasSize(1);
      assertThat(jsonConverter.toMap(auditEvents.getFirst().getAdditionalInformation()))
          .contains(
              entry("projectIds", toString(allProjectIds)),
              entry(
                  "statusIds",
                  toString(List.of(expectedProjectStatusId, expectedProjectStatusId))));
    } finally {
      batchRequestCleanup.deleteBatchRequests(batchRequest);
      projectPopulator.resetProjects();
    }
  }

  @Test
  void
      shouldUpdateOnlyMonitoringPeriodForChildOfBatchWhenBatchRequestChildMonitoringPeriodUpdated() {
    var batchRequest = createBatchRequest();
    var now = ignoringNanos(ZonedDateTime.now(ZoneId.of("UTC")));
    var monitoringPeriodDTO =
        MonitoringPeriodDTO.builder().startDate(now).endDate(now.plusYears(1)).build();
    try {
      var update =
          RequestCreateUpdateDTOMother.updateBuilder()
              .id(batchRequest.getChildren().getFirst().getRequest().getId())
              .monitoringPeriod(monitoringPeriodDTO)
              .build();

      var response =
          client.updateRequest(batchRequest.getChildren().getFirst().getProject().getId(), update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).isPositive();

      var updatedRequest =
          requestRepository
              .findById(batchRequest.getChildren().getFirst().getRequest().getId())
              .orElseThrow();
      assertThat(updatedRequest.getDraftMonitoringPeriod()).isPresent();
      var requestMonitoringPeriod = updatedRequest.getDraftMonitoringPeriod().orElseThrow();
      assertThat(requestMonitoringPeriod.getRequestId()).isEqualTo(updatedRequest.getId());
      assertThat(requestMonitoringPeriod.getStartDate())
          .isEqualTo(monitoringPeriodDTO.getStartDate());
      assertThat(requestMonitoringPeriod.getEndDate()).isEqualTo(monitoringPeriodDTO.getEndDate());

      var parentRequestOptional =
          requestRepository.findById(batchRequest.getParent().getRequest().getId());
      assertThat(parentRequestOptional).isPresent();
      var parentRequest = parentRequestOptional.get();
      assertThat(parentRequest.getDraftMonitoringPeriod()).isNotPresent();
    } finally {
      batchRequestCleanup.deleteBatchRequests(batchRequest);
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldNotAllowUnauthenticatedUserToUpdateRequest() {
    var request = requestHelper.createTestRequest(1);
    try {
      var update =
          RequestCreateUpdateDTOMother.updateBuilder()
              .startDate(ZonedDateTime.now())
              .id(request.getId())
              .build();

      var response = client.updateRequestUnauthenticated(1, update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @ParameterizedTest
  @EnumSource(UpdateRequestTestCase.class)
  void testUpdateRequestPermissions(UpdateRequestTestCase testCase) {
    var projectId = 1;
    var request =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            projectId, 6, 1, AccountMother.withIdOnly(1));
    try {
      var update =
          RequestCreateUpdateDTOMother.updateBuilder()
              .startDate(ZonedDateTime.now())
              .id(request.getId())
              .build();

      var response =
          client.updateRequest(projectId, update, testCase.getRole(), testCase.getUserId());

      assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @Test
  void testGetRequestTypeToDocumentTypeByRequestTypeId() {
    var requestTypeId = 1001;
    var requiredDocumentTypes = createRequiredDocumentTypes(requestTypeId);
    when(requestTypeDocumentTypeRepository.findAllByRequestTypeIdAndDocumentDisplayCategoryId(
            requestTypeId, REQUIRED_DISPLAY_CATEGORY_ID))
        .thenReturn(requiredDocumentTypes);

    var response = client.getRequiredDocumentTypes(requestTypeId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).containsKeys("REQUIRED");
    var requiredDocuments = response.getBody().get("REQUIRED");
    assertThat(requiredDocuments).hasSameSizeAs(requiredDocumentTypes);
    var first = requiredDocuments.getFirst();
    assertThat(first.getRequestTypeId()).isEqualTo(requestTypeId);
    assertThat(first.getDocumentTypeId()).isZero();
    assertThat(first.getDocumentDisplayCategoryId()).isOne();
    assertThat(first.getDocumentDisplayCategoryCode()).isEqualTo("REQUIRED");
    assertThat(first.getDocumentTypeDescription()).isEqualTo("Document Type Description 0");
    assertThat(first.getFormType().getId()).isZero();
  }

  @Test
  void shouldReturnParentRequestRoundsOnChildRequestGetter() {
    var batchRequest = createBatchRequest();
    var parentProjectId = batchRequest.getParent().getProject().getId();
    try {
      var parentRequest = batchRequest.getParent().getRequest();
      var now = ZonedDateTime.now();
      var update1 = updateStatus(parentRequest, now.plusSeconds(10), 9);
      client.updateRequest(parentProjectId, update1);
      var update2 =
          updateStatusBuilder(parentRequest, now.plusSeconds(20), 12).internal(false).build();
      client.updateRequest(parentProjectId, update2);
      var update3 = updateStatus(parentRequest, now.plusSeconds(30), 13);
      client.updateRequest(parentProjectId, update3);
      var childBatchRequest = batchRequest.getChildren().getFirst();
      var childProjectRequest = childBatchRequest.getRequest();
      var childProjectId = childProjectRequest.getProject().getId();

      var response = client.getRequestsByProjectId(childProjectId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var childRequest =
          response.getBody().getContent().stream()
              .filter(request -> request.getId().equals(childProjectRequest.getId()))
              .findFirst()
              .orElseThrow();
      var updatedParentRequest = requestRepository.findById(parentRequest.getId()).orElseThrow();
      var childRounds = childRequest.getRequestRounds();
      var parentRounds = updatedParentRequest.getRequestRounds();
      assertThat(parentRounds.size()).isOne();
      for (int i = 0; i < parentRounds.size(); i++) {
        var childRound = childRounds.get(i);
        var parentRound = parentRounds.get(i);
        assertThat(childRound.getRoundIndex()).isEqualTo(parentRound.getRoundIndex());
        assertThat(childRound.getEndDate()).isEqualTo(parentRound.getEndDate());
        assertThat(childRound.getStartDate()).isEqualTo(parentRound.getStartDate());
        assertThat(childRound.getStatusId()).isEqualTo(parentRound.getStatus().getId());
      }
    } finally {
      batchRequestCleanup.deleteBatchRequests(batchRequest);
    }
  }

  @Test
  void legacyBatchProjectIdsArePopulatedOnRequest() {
    var response = client.getRequestsByProjectId(1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    var requests = response.getBody().getContent();

    requests.forEach(request -> assertThat(request.getLegacyBatchProjectIds()).containsOnly(1));
  }

  @Test
  void shouldReturnRequestsForTacDashboard() {
    var requestId = givenRequestExists();
    try {
      givenRequestInConsultation(requestId);
      var expectedRequest = requestRepository.findById(requestId).orElseThrow();

      var response = client.getTacRequests();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertNotNull(response.getBody());

      var requests = response.getBody().getContent();

      assertThat(requests).isNotEmpty();
      var actualRequest = requests.getFirst();
      assertThat(actualRequest.getId()).isEqualTo(requestId);
      assertThat(actualRequest.getProjectGsid()).isEqualTo(expectedRequest.getProject().getGsid());
      assertThat(actualRequest.getProjectName()).isEqualTo(expectedRequest.getProject().getName());
      assertThat(actualRequest.getRequestName()).isEqualTo(expectedRequest.getRequestName());
      assertThat(actualRequest.getRequestStatus().getId())
          .isEqualTo(expectedRequest.getCurrentRequestStatus().getId());
    } finally {
      requestHelper.deleteTestRequest(requestId);
    }
  }

  @Test
  void shouldSetPiiAsApprovedOnRequestCompletion() {
    var project = ProjectMother.withIdOnly(1);
    var request =
        requestHelper.createTestRequestWithRequestStatusAndType(
            project,
            RequestStatus.IN_ROUND_COMPLETION_ID,
            false,
            RequestType.PRELIMINARY_REVIEW_ID);
    piiMetadataPopulator.populate(
        project.getId(), List.of("title", "description"), new PiiAction(PiiAction.NEW_STATUS_ID));
    piiMetadataPopulator.populate(1, List.of("notes"), new PiiAction(PiiAction.REMOVED_ID));
    var nonActionedPiiBefore =
        piiMetadataRepository.findByProjectIdAndTypeAndActionedFalse(1, PiiType.PROJECT);
    assertThat(nonActionedPiiBefore).hasSize(2);
    try {
      var response =
          client.armUpdateRequest(1, requestHelper.toCompletedRequestDTO(request.getId()));
      var nonActionedPiiAfter =
          piiMetadataRepository.findByProjectIdAndTypeAndActionedFalse(1, PiiType.PROJECT);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(nonActionedPiiAfter).isEmpty();
    } finally {
      requestHelper.deleteTestRequest(request.getId());
      piiMetadataPopulator.delete();
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldSetProjectDesignCertDateOnRequestCompletion() {
    var now = ZonedDateTime.now().withNano(0);
    var request =
        requestHelper.createTestRequestWithRequestTypeStatusAndAssignment(
            ProjectMother.withIdOnly(1),
            RequestStatus.IN_ASSIGNMENT_ID,
            0,
            new Account(1),
            RequestType.DESIGN_CERTIFICATION_PROJECT);
    try {
      var inProgress = client.armUpdateRequest(1, requestHelper.toInProgressDTO(request.getId()));
      requestStatusHistoryRepository.save(
          RequestStatusHistory.builder()
              .request(request)
              .nextStatus(RequestStatus.inProgress())
              .changedAt(now)
              .build());
      assertThat(inProgress.getStatusCode()).isEqualTo(HttpStatus.OK);
      var completed =
          client.armUpdateRequest(1, requestHelper.toCompletedRequestDTO(request.getId()));
      assertThat(completed.getStatusCode()).isEqualTo(HttpStatus.OK);
      var project = projectRepository.findById(1).orElseThrow();
      assertThat(project.getDesignCertificationDate()).isEqualTo(now.plusDays(28));
    } finally {
      requestHelper.deleteTestRequest(request.getId());
      projectPopulator.resetProjects();
    }
  }

  private int givenRequestExists() {
    return givenRequestExists(requestHelper.buildCreateRequestDTO(ZonedDateTime.now()));
  }

  private int givenRequestExists(RequestCreateUpdateDTO createDto) {
    var response = client.createRequest(1, createDto);
    return Objects.requireNonNull(response.getBody());
  }

  private void givenRequestInConsultation(int requestId) {
    var updateDto = requestHelper.toStartConsultationDTO(requestId);
    client.updateRequest(1, updateDto);
    assertThat(getRequest(requestId).isInStakeholderConsultation()).isTrue();
  }

  private RequestDTO getRequest(int id) {
    return Objects.requireNonNull(client.getRequestById(id).getBody());
  }

  private BatchRequests createBatchRequest() {
    var createDto = createPoAAndRealCaseBatchDTO();
    var response = client.createBatchRequest(createDto);
    var batchId = Objects.requireNonNull(response.getBody());
    return batchRequestFinder.getRequestsInBatch(batchId);
  }

  private static ZonedDateTime ignoringNanos(ZonedDateTime input) {
    return input.withNano(0);
  }

  private static RequestCreateUpdateDTO updateStatus(
      Request request, ZonedDateTime updatedAt, Integer newStatusId) {
    return updateStatusBuilder(request, updatedAt, newStatusId).build();
  }

  private static RequestCreateUpdateDTO.RequestCreateUpdateDTOBuilder updateStatusBuilder(
      Request request, ZonedDateTime updatedAt, Integer newStatusId) {
    return RequestCreateUpdateDTOMother.updateBuilder()
        .startDate(updatedAt)
        .id(request.getId())
        .requestStatusId(newStatusId);
  }

  @Getter
  @AllArgsConstructor
  public enum UpdateRequestTestCase {
    REVIEWER_ASSIGNED(GS_REGISTRY_ROLE, ASSIGNED_ACCOUNT1_USER_ID, HttpStatus.OK),
    REVIEWER_NOT_ASSIGNED(GS_REGISTRY_ROLE, NO_ACCOUNT_USER_ID1, HttpStatus.UNAUTHORIZED),
    VVB_ASSIGNED(VVB_ROLE, ASSIGNED_ACCOUNT1_USER_ID, HttpStatus.OK),
    FINANCE_ASSIGNED(FINANCE_ROLE, ASSIGNED_ACCOUNT1_USER_ID, HttpStatus.FORBIDDEN);
    private final String role;
    private final UUID userId;
    private final HttpStatus expectedStatus;
  }
}
