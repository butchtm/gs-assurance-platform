package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.activity.Activity.CONCLUSION_OF_REVIEW_IDS;
import static org.goldstandard.assurance.api.entity.activity.Activity.HARMONISATION_OF_FEEDBACK_IDS;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.QAQC_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.REVIEW_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.COMPLETED_APPROVED_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.COMPLETED_REJECTED_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_PROGRESS_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_ROUND_COMPLETION_ID;
import static org.goldstandard.assurance.api.fixtures.ProjectMother.newProjectWithTypes;
import static org.goldstandard.assurance.api.populator.UserPopulator.ANY_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.ARM_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.goldstandard.assurance.api.helper.AccountHelper;
import org.goldstandard.assurance.api.helper.RequestHelper;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import({RequestHelper.class, AccountHelper.class})
class AssignmentSummaryApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @Autowired RequestHelper requestHelper;
  @Autowired AccountHelper accountHelper;
  @Autowired ProjectPopulator projectPopulator;

  @ParameterizedTest
  @ValueSource(
      strings = {
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void shouldReturnForbiddenForNonPermittedUsersOnReviewEndpoint(String role) {
    var filter =
        "assignmentTypeId=" + REVIEW_ASSIGNMENT_TYPE_ID + "&qaqcAssignedToCallingAccount=true";
    var response = client.getReviewAssignmentSummary(filter, role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {ARM_TEAM_ROLE, ADMIN_ROLE})
  void shouldBeAccessibleByPermittedUsersOnReviewEndpoint(String role) {
    var filter =
        "assignmentTypeId=" + REVIEW_ASSIGNMENT_TYPE_ID + "&qaqcAssignedToCallingAccount=true";
    var response = client.getReviewAssignmentSummary(filter, role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getAssignmentSummary()).isNotNull();
    assertThat(body.getAssignmentTotals()).isNotNull();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void shouldReturnForbiddenForNonPermittedUsersOnArmEndpoint(String role) {
    var response = client.getArmAssignmentSummary("", role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {ARM_TEAM_ROLE, ADMIN_ROLE})
  void shouldBeAccessibleByPermittedUsersOnArmEndpoint(String role) {
    var response = client.getArmAssignmentSummary("", role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getAssignmentSummary()).isNotNull();
    assertThat(response.getBody().getAssignmentTotals()).isNotNull();
  }

  @Test
  void shouldReturnArmAssignmentSummary() {
    var account1 = accountHelper.createTestAccountWithType(PROJECT_DEVELOPER_TYPE_ID);

    var request1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1);
    var request2 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            3, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1);

    var activityId = HARMONISATION_OF_FEEDBACK_IDS.iterator().next();
    var dueDate = ZonedDateTime.now().plusDays(5);
    requestHelper.createRequestActivityForRequest(request1.getId(), activityId, dueDate);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, dueDate);

    try {
      var response = client.getArmAssignmentSummary("", ARM_TEAM_ROLE, ANY_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      var summaries = body.getAssignmentSummary().getContent();
      assertThat(summaries).isNotEmpty();

      var totals = body.getAssignmentTotals();
      assertThat(totals).isNotNull();

    } finally {
      requestHelper.deleteTestRequests(request1, request2);
      accountHelper.deleteTestAccounts(account1);
    }
  }

  @Test
  void shouldReturnEmptyArmAssignmentSummaryWhenNoData() {
    var response = client.getArmAssignmentSummary("", ARM_TEAM_ROLE, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getAssignmentSummary().getContent()).isEmpty();
  }

  @Test
  void shouldReturnArmAssignmentSummaryWithMultipleAccounts() {
    var account1 = accountHelper.createTestAccountWithType(3);
    var account2 = accountHelper.createTestAccountWithType(3);
    var account3 = accountHelper.createTestAccountWithType(3);

    var request1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1);
    var request2 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            2, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account2);
    var request3 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            3, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account3);

    var activityId = HARMONISATION_OF_FEEDBACK_IDS.iterator().next();
    var dueDate = ZonedDateTime.now().plusDays(5);
    requestHelper.createRequestActivityForRequest(request1.getId(), activityId, dueDate);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, dueDate);
    requestHelper.createRequestActivityForRequest(request3.getId(), activityId, dueDate);

    try {
      var response = client.getArmAssignmentSummary("", ARM_TEAM_ROLE, ANY_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();

      var summaries = body.getAssignmentSummary().getContent();
      assertThat(summaries).isNotEmpty();

      var accountNames = summaries.stream().map(AssignmentSummaryDTO::getAccountName).toList();
      var sortedNames = accountNames.stream().sorted().toList();
      assertThat(accountNames).isEqualTo(sortedNames);

    } finally {
      requestHelper.deleteTestRequests(request1, request2, request3);
      accountHelper.deleteTestAccounts(account1, account2, account3);
    }
  }

  @Test
  void shouldFilterArmAssignmentSummaryByDueDate() {
    var account1 = accountHelper.createTestAccountWithType(3);

    var request1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1);
    var request2 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            2, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1);
    var request3 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            2, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1);

    int activityId = HARMONISATION_OF_FEEDBACK_IDS.stream().findFirst().orElseThrow();
    var overdue = ZonedDateTime.now().minusDays(1);
    var due = ZonedDateTime.now().plusDays(1);
    var notInRange = ZonedDateTime.now().plusDays(10);

    requestHelper.createRequestActivityForRequest(request1.getId(), activityId, overdue);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, due);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, notInRange);

    try {
      var filterDate = ZonedDateTime.now().plusDays(5);
      var filter = "requestDueBefore=" + filterDate;
      var response = client.getArmAssignmentSummary(filter, ARM_TEAM_ROLE, ANY_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();

      var summaries = body.getAssignmentSummary().getContent();
      assertThat(summaries).hasSize(1);
      assertThat(summaries.getFirst().getAccountName()).isEqualTo(account1.getName());
      assertThat(summaries.getFirst().getUpcomingRequests()).isZero();
      assertThat(summaries.getFirst().getDueRequests()).isEqualTo(1);
      assertThat(summaries.getFirst().getOverdueRequests()).isEqualTo(1);

    } finally {
      requestHelper.deleteTestRequests(request1, request2, request3);
      accountHelper.deleteTestAccounts(account1);
    }
  }

  @Test
  void shouldFilterArmAssignmentSummaryByRequestType() {
    var account1 = accountHelper.createTestAccountWithType(3);

    var requestTypesToInclude = List.of(RequestType.PRELIMINARY_REVIEW_ID, 1001);
    var request1 =
        requestHelper.createTestRequestWithRequestTypeStatusAndAssignment(
            ProjectMother.withIdOnly(1),
            IN_ROUND_COMPLETION_ID,
            QAQC_ASSIGNMENT_TYPE_ID,
            account1,
            requestTypesToInclude.getFirst());
    var request2 =
        requestHelper.createTestRequestWithRequestTypeStatusAndAssignment(
            ProjectMother.withIdOnly(2),
            IN_ROUND_COMPLETION_ID,
            QAQC_ASSIGNMENT_TYPE_ID,
            account1,
            requestTypesToInclude.get(1));
    var request3 =
        requestHelper.createTestRequestWithRequestTypeStatusAndAssignment(
            ProjectMother.withIdOnly(3),
            IN_ROUND_COMPLETION_ID,
            QAQC_ASSIGNMENT_TYPE_ID,
            account1,
            1008);

    int activityId = HARMONISATION_OF_FEEDBACK_IDS.stream().findFirst().orElseThrow();
    var due = ZonedDateTime.now().plusDays(1);

    requestHelper.createRequestActivityForRequest(request1.getId(), activityId, due);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, due);
    requestHelper.createRequestActivityForRequest(request3.getId(), activityId, due);

    try {
      var filter = "requestTypeIds=" + toString(requestTypesToInclude);
      var response = client.getArmAssignmentSummary(filter, ARM_TEAM_ROLE, ANY_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();

      var summaries = body.getAssignmentSummary().getContent();
      assertThat(summaries).hasSize(1);
      assertThat(summaries.getFirst().getDueRequests()).isEqualTo(2);
    } finally {
      requestHelper.deleteTestRequests(request1, request2, request3);
      accountHelper.deleteTestAccounts(account1);
    }
  }

  @Test
  void shouldFilterArmAssignmentSummaryByProjectType() {
    var account1 = accountHelper.createTestAccountWithType(3);

    var typeIds = List.of(4, 0);
    var project1 = projectPopulator.populateProject(newProjectWithTypes(typeIds.getFirst()));
    var project2 = projectPopulator.populateProject(newProjectWithTypes(typeIds));
    var project3 = projectPopulator.populateProject(newProjectWithTypes(4242));

    var request1 =
        requestHelper.createTestRequestWithRequestTypeStatusAndAssignment(
            project1, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1, 1001);
    var request2 =
        requestHelper.createTestRequestWithRequestTypeStatusAndAssignment(
            project2, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1, 1001);
    var request3 =
        requestHelper.createTestRequestWithRequestTypeStatusAndAssignment(
            project3, IN_ROUND_COMPLETION_ID, QAQC_ASSIGNMENT_TYPE_ID, account1, 1001);

    int activityId = HARMONISATION_OF_FEEDBACK_IDS.stream().findFirst().orElseThrow();
    var due = ZonedDateTime.now().plusDays(1);

    requestHelper.createRequestActivityForRequest(request1.getId(), activityId, due);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, due);
    requestHelper.createRequestActivityForRequest(request3.getId(), activityId, due);

    try {
      var filter = String.format("projectTypeIds=%s", toString(typeIds));
      var response = client.getArmAssignmentSummary(filter, ARM_TEAM_ROLE, ANY_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();

      var summaries = body.getAssignmentSummary().getContent();
      assertThat(summaries).hasSize(1);
      assertThat(summaries.getFirst().getDueRequests()).isEqualTo(2);
    } finally {
      requestHelper.deleteTestRequests(request1, request2, request3);
      accountHelper.deleteTestAccounts(account1);
      projectPopulator.deleteProjects(project1, project2, project3);
    }
  }

  @Test
  void shouldReturnReviewAssignmentSummary() {
    var account1 = accountHelper.createTestAccountWithType(3);
    var account2 = accountHelper.createTestAccountWithType(3);

    var openRequest1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, IN_PROGRESS_ID, REVIEW_ASSIGNMENT_TYPE_ID, account1);
    var closedRequest1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, COMPLETED_APPROVED_ID, REVIEW_ASSIGNMENT_TYPE_ID, account1);
    var closedRequest2 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            2, COMPLETED_REJECTED_ID, REVIEW_ASSIGNMENT_TYPE_ID, account2);
    try {
      var filter =
          "assignmentTypeId=" + REVIEW_ASSIGNMENT_TYPE_ID + "&qaqcAssignedToCallingAccount=false";
      var response = client.getReviewAssignmentSummary(filter, ARM_TEAM_ROLE, ANY_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      var summaries = body.getAssignmentSummary().getContent();
      assertThat(summaries).hasSize(3);
      assertThat(summaries)
          .map(AssignmentSummaryDTO::getAccountName)
          .containsExactly("Account 2", account1.getName(), account2.getName());
      assertThat(summaries).map(AssignmentSummaryDTO::getOpenRequests).containsExactly(1, 1, 0);
      assertThat(summaries).map(AssignmentSummaryDTO::getClosedRequests).containsExactly(0, 1, 1);
      var totals = body.getAssignmentTotals();
      assertThat(totals.getOpenRequests()).isEqualTo(2);
      assertThat(totals.getClosedRequests()).isEqualTo(2);
    } finally {
      requestHelper.deleteTestRequests(openRequest1, closedRequest1, closedRequest2);
      accountHelper.deleteTestAccounts(account1, account2);
    }
  }

  @Test
  void shouldReturnReviewAssignmentSummaryForArmAssigned() {
    var account1 = accountHelper.createTestAccountWithType(3);
    var armAccount = accountHelper.getArmAccount();

    var openRequest1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, IN_PROGRESS_ID, REVIEW_ASSIGNMENT_TYPE_ID, account1);
    requestHelper.createAssignmentForRequest(
        QAQC_ASSIGNMENT_TYPE_ID, armAccount, openRequest1.getId());
    var closedRequest1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, COMPLETED_APPROVED_ID, REVIEW_ASSIGNMENT_TYPE_ID, account1);
    var closedRequest2 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            2, COMPLETED_REJECTED_ID, REVIEW_ASSIGNMENT_TYPE_ID, account1);
    try {
      var filter =
          "assignmentTypeId=" + REVIEW_ASSIGNMENT_TYPE_ID + "&qaqcAssignedToCallingAccount=true";
      var response = client.getReviewAssignmentSummary(filter, ARM_TEAM_ROLE, ARM_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      var summaries = body.getAssignmentSummary().getContent();
      assertThat(summaries).hasSize(1);
      assertThat(summaries)
          .map(AssignmentSummaryDTO::getAccountName)
          .containsExactly(account1.getName());
      assertThat(summaries).map(AssignmentSummaryDTO::getOpenRequests).containsExactly(1);
      assertThat(summaries).map(AssignmentSummaryDTO::getClosedRequests).containsExactly(0);
      var totals = response.getBody().getAssignmentTotals();
      assertThat(totals.getOpenRequests()).isEqualTo(1);
      assertThat(totals.getClosedRequests()).isZero();
    } finally {
      requestHelper.deleteTestRequests(openRequest1, closedRequest1, closedRequest2);
      accountHelper.deleteTestAccounts(account1);
    }
  }

  @Test
  void shouldFilterRoeAssignmentSummaryWithDueOverdue() {
    var account1 = accountHelper.createTestAccountWithType(REVIEWER_ACCOUNT_TYPE_ID);

    var request1 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            1, IN_ROUND_COMPLETION_ID, REVIEW_ASSIGNMENT_TYPE_ID, account1);
    var request2 =
        requestHelper.createTestRequestWithRequestStatusAndAssignment(
            2, IN_PROGRESS_ID, REVIEW_ASSIGNMENT_TYPE_ID, account1);

    int activityId = CONCLUSION_OF_REVIEW_IDS.stream().findFirst().orElseThrow();
    var overdue = ZonedDateTime.now().minusDays(1);
    var due = ZonedDateTime.now().plusDays(1);
    var notInRange = ZonedDateTime.now().plusDays(10);

    requestHelper.createRequestActivityForRequest(request1.getId(), activityId, overdue);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, due);
    requestHelper.createRequestActivityForRequest(request2.getId(), activityId, notInRange);

    try {
      var filterDate = ZonedDateTime.now().plusDays(5);
      var filter = "requestDueBefore=" + filterDate;
      var response = client.getReviewAssignmentSummary(filter, ARM_TEAM_ROLE, ANY_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();

      var summaries = body.getAssignmentSummary().getContent();
      summaries.stream()
          .filter(summery -> summery.getAccountName().equals(account1.getName()))
          .forEach(
              summery -> {
                assertThat(summery.getDueRequests()).isEqualTo(1);
                assertThat(summery.getOverdueRequests()).isEqualTo(1);
              });

    } finally {
      requestHelper.deleteTestRequests(request1, request2);
      accountHelper.deleteTestAccounts(account1);
    }
  }

  private static String toString(Collection<Integer> params) {
    return params.stream().map(String::valueOf).collect(Collectors.joining(","));
  }
}
