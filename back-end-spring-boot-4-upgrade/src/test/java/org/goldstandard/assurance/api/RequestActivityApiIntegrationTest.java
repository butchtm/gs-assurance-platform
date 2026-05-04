package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.UserPopulator.ASSIGNED_ACCOUNT1_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.PROJECT_DEVELOPER_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.RequestActivityDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.goldstandard.assurance.api.helper.AuditCleanup;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(RequestHelper.class)
class RequestActivityApiIntegrationTest extends AbstractApiTest {

  @Autowired RequestRepository requestRepository;

  @Autowired RequestActivityRepository requestActivityRepository;

  @Autowired RequestHelper requestHelper;

  @Autowired AuditCleanup auditCleanup;

  @Autowired ProjectPopulator projectPopulator;

  @Autowired ApiClient client;

  @AfterEach
  void cleanupAudit() {
    auditCleanup.deleteAllAuditEvents();
  }

  @Test
  void shouldReturnCurrentActivityForRequest() {
    var projectId = 1;
    var request = requestHelper.createTestRequest(projectId);

    try {
      var update = updateStatus(request, ZonedDateTime.now().plusSeconds(120), 9);

      var response = client.updateRequest(projectId, update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var updatedRequest = requestRepository.findById(request.getId());
      assertThat(updatedRequest).map(Request::getCurrentActivity).isPresent();
      RequestActivity activity = updatedRequest.orElseThrow().getCurrentActivity().orElseThrow();
      assertThat(activity.getDisplayName()).isEqualTo("Assigning for CC");
      assertThat(activity.getStartDate()).isNotNull();
      assertThat(activity.getPlannedEndDate()).isNotNull();
      assertThat(activity.getEndDate()).isNull();
      assertThat(activity.getResponsibleAccount().getId()).isEqualTo(7);
      assertThat(activity.getResponsibleAccount().getName()).isEqualTo("Account 7");
    } finally {
      requestHelper.deleteTestRequest(request);
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldCloseAssigningForCompletenessCheckActivity() {
    var projectId = 1;
    var request = requestHelper.createTestRequest(projectId);

    try {
      var update = updateStatus(request, ZonedDateTime.now().plusSeconds(10), 9);
      client.updateRequest(projectId, update);
      var requestActivityId =
          requestRepository
              .findById(request.getId())
              .flatMap(Request::getCurrentActivity)
              .map(RequestActivity::getId)
              .orElseThrow();

      var response = client.closeActivity(requestActivityId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var completedActivity = requestActivityRepository.findById(requestActivityId).orElseThrow();
      assertThat(completedActivity.getEndDate()).isNotNull();

      var updatedRequest = requestRepository.findById(request.getId()).orElseThrow();
      assertThat(updatedRequest.getCurrentActivity()).isPresent();
      RequestActivity activity = updatedRequest.getCurrentActivity().orElseThrow();
      assertThat(activity.getDisplayName()).isEqualTo("Round 1 Completeness Check");
      assertThat(activity.getStartDate()).isNotNull();
      assertThat(activity.getPlannedEndDate()).isNotNull();
      assertThat(activity.getEndDate()).isNull();
      assertThat(activity.getResponsibleAccount().getId()).isEqualTo(7);
      assertThat(activity.getResponsibleAccount().getName()).isEqualTo("Account 7");
    } finally {
      requestHelper.deleteTestRequest(request);
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldReturnActivityHistoryForRequest() {
    var projectId = 1;
    var request = requestHelper.createTestRequest(projectId);

    try {
      var now = ZonedDateTime.now();
      var update1 = updateStatus(request, now.plusSeconds(10), 9);
      client.updateRequest(projectId, update1);
      var update2 = updateStatusBuilder(request, now.plusSeconds(20), 12).internal(true).build();
      client.updateRequest(projectId, update2);

      var response = client.getRequestActivityHistory(request.getId());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var pages = response.getBody();
      assertThat(pages.getTotalElements()).isEqualTo(3);
      var history = pages.getContent();
      var expectedAccountId = 7;
      var activity1 = history.getFirst();
      assertThat(activity1.getName()).isEqualTo("Assigning for Review");
      assertNotComplete(activity1);
      assertResponsibleAccount(activity1, expectedAccountId);
      var activity2 = history.get(1);
      assertThat(activity2.getName()).isEqualTo("Round 1 Completeness Check");
      assertComplete(activity2);
      assertResponsibleAccount(activity2, expectedAccountId);
      var activity3 = history.get(2);
      assertThat(activity3.getName()).isEqualTo("Assigning for CC");
      assertComplete(activity3);
      assertResponsibleAccount(activity3, expectedAccountId);
    } finally {
      requestHelper.deleteTestRequest(request);
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldReturnPathwayGroupTimelineForRequest() {
    var projectId = 1;
    var request = requestHelper.createTestRequest(projectId);

    try {
      var now = ZonedDateTime.now();
      var update1 = updateStatus(request, now.plusSeconds(10), 9);
      client.updateRequest(projectId, update1);
      var update2 = updateStatusBuilder(request, now.plusSeconds(20), 12).internal(false).build();
      client.updateRequest(projectId, update2);
      var update3 = updateStatus(request, now.plusSeconds(30), 13);
      client.updateRequest(projectId, update3);

      var response = client.getRequestPathwayGroupTimeline(request.getId());

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var activities = response.getBody();
      assertThat(activities).hasSize(3);
      var activity1 = activities.getFirst();
      assertThat(activity1.getIndex()).isZero();
      assertThat(activity1.getName()).isEqualTo("Assigning for Review");
      assertThat(activity1.isCompleted()).isTrue();
      assertThat(activity1.isExternalResponsibility()).isFalse();
      assertThat(activity1.isCurrent()).isFalse();
      var activity2 = activities.get(1);
      assertThat(activity2.getIndex()).isOne();
      assertThat(activity2.getName()).isEqualTo("Round 1 Review");
      assertThat(activity2.isCompleted()).isTrue();
      assertThat(activity2.isExternalResponsibility()).isTrue();
      assertThat(activity2.isCurrent()).isFalse();
      var activity3 = activities.get(2);
      assertThat(activity3.getIndex()).isEqualTo(2);
      assertThat(activity3.getName()).isEqualTo("Harmonising Round 1 Feedback");
      assertThat(activity3.isCompleted()).isFalse();
      assertThat(activity3.isExternalResponsibility()).isFalse();
      assertThat(activity3.isCurrent()).isTrue();
    } finally {
      requestHelper.deleteTestRequest(request);
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldStartFirstActivityForRequestMovingIntoCompletenessCheck() {
    var projectId = 1;
    var request = requestHelper.createTestRequest(projectId);
    try {
      var dto =
          updateStatus(
              request, ZonedDateTime.now().plusSeconds(10), RequestStatus.COMPLETENESS_CHECK_ID);
      var updateResponse = client.updateRequest(projectId, dto);
      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertNotNull(updateResponse.getBody());
      assertThat(updateResponse.getBody()).isPositive();

      var updatedRequest = requestRepository.findById(updateResponse.getBody()).orElseThrow();
      assertThat(updatedRequest.getCurrentActivity()).isPresent();
      var currentActivity = updatedRequest.getCurrentActivity().orElseThrow().getActivity();

      assertThat(currentActivity.getPathwayGroup().getPathway().getIndex()).isZero();
      assertThat(currentActivity.getPathwayGroup().getIndex()).isZero();
      assertThat(currentActivity.getIndex()).isZero();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  @ParameterizedTest
  @EnumSource(ActivityAccessTestCase.class)
  void shouldRedactActivityForVvbOrPd(ActivityAccessTestCase testCase) {
    var projectId = 4;
    var request =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            projectId, 6, 1, AccountMother.withIdOnly(1));
    try {
      var dto =
          updateStatus(
              request, ZonedDateTime.now().plusSeconds(10), RequestStatus.COMPLETENESS_CHECK_ID);
      var updateResponse = client.updateRequest(projectId, dto, testCase.role, testCase.userId);
      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

      var getResponse = client.getRequestById(testCase.role, request.getId(), testCase.userId);
      assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(getResponse.getBody()).isNotNull();
      assertThat(getResponse.getBody().getLatestRequestActivity()).isNull();
    } finally {
      requestHelper.deleteTestRequest(request);
    }
  }

  private RequestCreateUpdateDTO updateStatus(
      Request request, ZonedDateTime updatedAt, Integer newStatusId) {
    return updateStatusBuilder(request, updatedAt, newStatusId).build();
  }

  private RequestCreateUpdateDTO.RequestCreateUpdateDTOBuilder updateStatusBuilder(
      Request request, ZonedDateTime updatedAt, Integer newStatusId) {
    return RequestCreateUpdateDTOMother.updateBuilder()
        .startDate(updatedAt)
        .id(request.getId())
        .requestStatusId(newStatusId);
  }

  private static void assertResponsibleAccount(RequestActivityDTO dto, int expectedId) {
    var account = dto.getAccount();
    assertThat(account.getId()).isEqualTo(expectedId);
    assertThat(account.getName()).isEqualTo(String.format("Account %d", expectedId));
  }

  private static void assertNotComplete(RequestActivityDTO dto) {
    assertStarted(dto);
    assertThat(dto.getEndDate()).isNull();
  }

  private static void assertComplete(RequestActivityDTO dto) {
    assertStarted(dto);
    assertThat(dto.getEndDate()).isNotNull();
  }

  private static void assertStarted(RequestActivityDTO dto) {
    assertThat(dto.getStartDate()).isNotNull();
    assertThat(dto.getDueDate()).isNotNull();
  }

  @Getter
  @AllArgsConstructor
  public enum ActivityAccessTestCase {
    PROJECT_DEVELOPER(PROJECT_DEVELOPER_USER_ID, PROJECT_DEVELOPER_ROLE),
    VVB(ASSIGNED_ACCOUNT1_USER_ID, VVB_ROLE);
    private final UUID userId;
    private final String role;
  }
}
