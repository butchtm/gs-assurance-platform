package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ARM_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_FINANCE_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.fixtures.RequestAccountAssignmentMother.armCompletenessCheckAssignment;
import static org.goldstandard.assurance.api.fixtures.RequestAccountAssignmentMother.completenessCheckAssignment;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;
import static org.goldstandard.assurance.api.populator.UserPopulator.ANY_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.dto.AccountDTO;
import org.goldstandard.assurance.api.dto.AssignedRequestDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.populator.AssignmentPopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class AssignedRequestApiIntegrationTest extends AbstractApiTest {

  @Autowired AssignmentPopulator assignmentPopulator;

  @Autowired ApiClient client;

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, PROJECT_DEVELOPER_ROLE, TAC_ROLE})
  void shouldReturnForbiddenForNonPermittedUsers(String role) {
    var response = client.getAssignedRequestsForUser(role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNull();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ARM_TEAM_ROLE,
        GS_REVIEWER_ROLE,
        VVB_ROLE,
        FINANCE_ROLE,
        GS_REGISTRY_ROLE,
      })
  void shouldBeAccessibleByPermittedUsers(String role) {
    var response = client.getAssignedRequestsForUser(role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(2);
  }

  @Test
  void shouldNotReturnRequestsNotAssignedToUsersAccount() {
    int otherAccountId = 4;
    var otherAccountAssignment =
        assignmentPopulator.populate(completenessCheckAssignment(1, otherAccountId));
    try {
      var response = client.getAssignedRequests();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent()).isEmpty();
    } finally {
      assignmentPopulator.delete(otherAccountAssignment);
    }
  }

  @Test
  void shouldReturnRequestsAssignedToUsersAccount() {
    var assignment = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var project = TEST_PROJECTS.getFirst();
    try {
      var response = client.getAssignedRequests();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var assignedRequests = response.getBody().getContent();
      assertThat(assignedRequests).hasSize(1);
      var assignedRequest = assignedRequests.getFirst();

      assertThat(assignedRequest)
          .usingRecursiveComparison()
          .isEqualTo(expectedAssignedRequestDTO(assignment, project));
    } finally {
      assignmentPopulator.delete(assignment);
    }
  }

  @ParameterizedTest
  @MethodSource("filtersAndExpectedRequestCounts")
  void shouldFilterAssignedRequests(String filter, int expectedCount) {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestsWithQuery(filter);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(expectedCount);
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"sort=assignmentCreatedDate,asc", "sort=assignmentDueDate,asc"})
  void shouldSortAssignedRequestsByDateAscending(String sortCriteria) {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestsWithQuery(sortCriteria);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var assignedRequests = response.getBody().getContent();
      assertThat(assignedRequests)
          .map(AssignedRequestDTO::getAssignmentCreatedDate)
          .containsExactly(
              assignment1.getCreatedTime(),
              assignment2.getCreatedTime(),
              assignment3.getCreatedTime());
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"sort=assignmentCreatedDate,desc", "sort=assignmentDueDate,desc"})
  void shouldSortAssignedRequestsByDateDescending(String sortCriteria) {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestsWithQuery(sortCriteria);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      var assignedRequests = response.getBody().getContent();
      assertThat(assignedRequests)
          .map(AssignedRequestDTO::getAssignmentCreatedDate)
          .containsExactly(
              assignment3.getCreatedTime(),
              assignment2.getCreatedTime(),
              assignment1.getCreatedTime());
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @Test
  void shouldReturnArmAssignedRequestProjects() {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestProjects();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .map(ProjectDTO::getName)
          .containsExactly("Project 1", "Project 2");
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @Test
  void shouldReturnArmAssignedRequestStatuses() {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestStatuses();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .map(ExtendedReferenceDataType::getDescription)
          .containsExactly("Booked", "In Progress");
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @Test
  void shouldReturnArmAssignedInvoiceStatuses() {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestInvoiceStatuses();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .map(ExtendedReferenceDataType::getDescription)
          .containsExactly("Generated");
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @Test
  void shouldReturnArmAssignedRequestTypes() {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestTypes();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .map(ExtendedReferenceDataType::getDescription)
          .containsExactly("Preliminary Review", "Design Certification Project");
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @ParameterizedTest
  @MethodSource("accountTypeIdsAndAccountNames")
  void shouldReturnArmAssignedRequestAccounts(
      int accountTypeId, List<String> expectedAccountNames) {
    var assignment1 = assignmentPopulator.populate(armCompletenessCheckAssignment(1));
    var assignment2 = assignmentPopulator.populate(armCompletenessCheckAssignment(2));
    var assignment3 = assignmentPopulator.populate(armCompletenessCheckAssignment(3));
    try {
      var response = client.getAssignedRequestAccounts(accountTypeId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .map(AccountDTO::getName)
          .containsExactlyElementsOf(expectedAccountNames);
    } finally {
      assignmentPopulator.delete(assignment1, assignment2, assignment3);
    }
  }

  @ParameterizedTest
  @ValueSource(
      ints = {
        GS_ACCOUNT_TYPE_ID,
        GS_ARM_ACCOUNT_TYPE_ID,
        GS_FINANCE_ACCOUNT_TYPE_ID,
        TAC_ACCOUNT_TYPE_ID
      })
  void shouldReturnErrorForUnsupportedAccountTypeIds(int accountTypeId) {
    var response = client.getAssignedRequestAccountsError(accountTypeId);

    var expectedDetail = String.format("Account type id %d not supported", accountTypeId);
    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .ignoringFields("instance")
        .isEqualTo(ProblemDetail.forStatusAndDetail(BAD_REQUEST, expectedDetail));
  }

  private static Stream<Arguments> filtersAndExpectedRequestCounts() {
    return Stream.of(
        Arguments.of("requestStatusIds=10", 1),
        Arguments.of("projectIds=1", 2),
        Arguments.of("projectAccountIds=1", 2),
        Arguments.of("reviewerAccountIds=1", 0),
        Arguments.of("vvbAccountIds=1", 1),
        Arguments.of("invoiceStatusIds=2", 3),
        Arguments.of("requestTypeIds=1000", 2),
        Arguments.of("projectSearch=GS1001", 2),
        Arguments.of("projectSearch=PROJECT 1", 2));
  }

  private static AssignedRequestDTO expectedAssignedRequestDTO(
      RequestAccountAssignment assignment, Project project) {
    return AssignedRequestDTO.builder()
        .projectId(project.getId())
        .gsid(project.getGsid())
        .projectName(project.getName())
        .projectAccountId(project.getAccountId())
        .projectAccountName("Account 1")
        .projectCarbonStream(project.getCarbonStream())
        .projectSize(project.getProjectSize())
        .projectTypes(project.getProjectTypes())
        .projectLocations(project.getProjectLocations())
        .requestId(assignment.getRequestId())
        .requestName("review name 1")
        .batchSize(1)
        .requestTypeId(1001)
        .requestTypeDescription("Design Certification Project")
        .requestStatusId(10)
        .requestStatus("In Progress")
        .currentRoundIndex(null)
        .currentRoundEndDate(null)
        .estimatedAnnualEmissionReductionAmount(10)
        .invoiceId(2)
        .invoiceStatusId(2)
        .invoiceStatus("Generated")
        .requestRounds(Collections.emptyList())
        .assignmentId(assignment.getId())
        .assignmentStatus("Pending")
        .assignmentCreatedDate(assignment.getCreatedTime())
        .dueDate(assignment.getDueDate())
        .reviewerAccountId(null)
        .reviewerAccountName(null)
        .latestActivityResponsibleAccountName("Account 1")
        .latestActivityResponsibleAccountId(1)
        .unreadMessages(0)
        .build();
  }

  private static Stream<Arguments> accountTypeIdsAndAccountNames() {
    return Stream.of(
        Arguments.of(REVIEWER_ACCOUNT_TYPE_ID, List.of("Account 2")),
        Arguments.of(VVB_ACCOUNT_TYPE_ID, List.of("Account 1")),
        Arguments.of(PROJECT_DEVELOPER_TYPE_ID, List.of("Account 1", "Account 2")));
  }
}
