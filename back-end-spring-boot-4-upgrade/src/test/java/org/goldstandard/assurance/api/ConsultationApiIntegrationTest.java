package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.goldstandard.assurance.api.fixtures.CreateBatchRequestDTOMother.createBatchDTO;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;
import static org.goldstandard.assurance.api.service.AuditEventSubject.CONSULTATION_COMPLETE;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.awaitility.Awaitility;
import org.goldstandard.assurance.api.dto.ConsultationDTO;
import org.goldstandard.assurance.api.dto.ConsultationTypeDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.goldstandard.assurance.api.helper.BatchRequestCleanup;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(RequestHelper.class)
class ConsultationApiIntegrationTest extends AbstractApiTest {

  @Autowired private RequestHelper requestHelper;

  @Autowired private AuditRepository auditRepository;

  @Autowired private ApiClient client;

  @Autowired BatchRequestCleanup batchRequestCleanup;

  @Autowired JsonConverter jsonConverter;

  @BeforeEach
  void setUp() {
    auditRepository.deleteAll();
  }

  @Test
  void shouldEndStakeholderConsultationForGivenRequest() {
    var requestId = givenRequestExists();
    try {
      givenRequestInConsultation(requestId);

      var response = client.completeConsultationsForRequest(requestId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
      assertThat(getRequest(requestId).isInStakeholderConsultation()).isFalse();
    } finally {
      requestHelper.deleteTestRequest(requestId);
    }
  }

  @Test
  void shouldAuditEndStakeholderConsultationForGivenRequest() {
    var requestId = givenRequestExists();
    try {
      givenRequestInConsultation(requestId);

      client.completeConsultationsForRequest(requestId);

      var auditEvent = getConsultationCompleteAuditEvent();
      assertThat(auditEvent).isPresent();
      assertThat(jsonConverter.toMap(auditEvent.get().getAdditionalInformation()))
          .hasSize(2)
          .contains(entry("requestId", requestId));
    } finally {
      requestHelper.deleteTestRequest(requestId);
    }
  }

  @Test
  void shouldGetStakeholderConsultationsForGivenRequest() {
    var requestId = givenRequestExists();
    try {
      givenRequestInConsultation(requestId);

      var response = client.getConsultationsForRequest(requestId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .map(ConsultationDTO::getType)
          .map(ConsultationTypeDTO::getUserType)
          .containsExactly("TAC", "PUBLIC");
    } finally {
      requestHelper.deleteTestRequest(requestId);
    }
  }

  @Test
  void shouldGetBatchParentsStakeholderConsultationsForGivenChildRequest() {
    var projectPoa = TEST_PROJECTS.getFirst();
    var projectRealCase = TEST_PROJECTS.get(1);
    var createDTO = createBatchDTO(projectPoa, projectRealCase);
    var createResponse = client.createBatchRequest(createDTO);
    var batchId = createResponse.getBody();

    try {
      var response = client.getBatchProjects(batchId);
      var batchProjects = Objects.requireNonNull(response.getBody());
      var parent = batchProjects.getParent();
      givenRequestInConsultation(parent.getRequestId());

      var children = batchProjects.getChildren();
      assertThat(children)
          .flatMap(child -> client.getConsultationsForRequest(child.getRequestId()).getBody())
          .map(ConsultationDTO::getType)
          .map(ConsultationTypeDTO::getUserType)
          .containsExactly("TAC", "PUBLIC");
    } finally {
      batchRequestCleanup.deleteBatchRequests(batchId);
    }
  }

  @ParameterizedTest
  @MethodSource("timeAndExpectedConsultationCount")
  void shouldReturnPageOfLiveConsultations(ZonedDateTime now, int expectedCount) {
    var requestId = givenRequestExists();
    try {
      givenRequestInConsultation(requestId);

      var response = client.getPageOfConsultations(now);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(expectedCount);
    } finally {
      requestHelper.deleteTestRequest(requestId);
    }
  }

  @ParameterizedTest
  @MethodSource("consultationTypeIdAndExpectedCount")
  void shouldFilterPageOfLiveConsultationsByType(Integer consultationTypeId, int expectedCount) {
    var requestId = givenRequestExists();
    try {
      givenRequestInConsultation(requestId);

      var response = client.getPageOfConsultations(nowUtc(), consultationTypeId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(expectedCount);
    } finally {
      requestHelper.deleteTestRequest(requestId);
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
    Awaitility.await()
        .atMost(Duration.ofSeconds(3))
        .ignoreExceptions()
        .until(() -> getRequest(requestId).isInStakeholderConsultation());
  }

  private RequestDTO getRequest(int id) {
    return Objects.requireNonNull(client.getRequestById(id).getBody());
  }

  private Optional<AuditEvent> getConsultationCompleteAuditEvent() {
    return auditRepository.findAllByEventSubject(CONSULTATION_COMPLETE.name()).stream().findFirst();
  }

  private static Stream<Arguments> timeAndExpectedConsultationCount() {
    return Stream.of(
        Arguments.of(nowUtc().plusHours(1), 2), Arguments.of(nowUtc().minusHours(1), 0));
  }

  private static Stream<Arguments> consultationTypeIdAndExpectedCount() {
    return Stream.of(Arguments.of(null, 2), Arguments.of(0, 1), Arguments.of(1, 1));
  }

  private static ZonedDateTime nowUtc() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }
}
