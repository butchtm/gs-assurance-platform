package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentStatus.ACCEPTED;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentStatus.CANCELLED;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentStatus.PENDING;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NON_VVB_ACCOUNT_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.populator.UserPopulator.VVB_ACCOUNT_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.configuration.AccountConfiguration;
import org.goldstandard.assurance.api.dto.AssignmentStatusUpdate;
import org.goldstandard.assurance.api.dto.RequestAccountAssignmentDTO;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.AssignmentCreateUpdateDTOMother;
import org.goldstandard.assurance.api.fixtures.RequestAccountAssignmentMother;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AssignmentApiIntegrationTest extends AbstractApiTest {

  private static final int VVB_ACCOUNT_ID = 1;
  private static final int NON_VVB_ACCOUNT_ID = 2;

  private static final int VVB_ASSIGNMENT_ID = 1;
  private static final int NON_VVB_ASSIGNMENT_ID = 2;

  private static final int REQUEST_ID_1 = 1;
  private static final int REQUEST_ID_2 = 2;

  @Autowired RequestAccountAssignmentRepository repository;

  @Autowired AccountConfiguration accountConfiguration;

  @Autowired ApiClient client;

  @ParameterizedTest
  @ValueSource(
      strings = {"", "?requestId=1&assignmentTypeId=0", "?requestId=1", "?assignmentTypeId=0"})
  void testGetAssignments(String query) {
    var accountId = 1;

    var response = client.getAssignments(accountId, query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var assignments = response.getBody().getContent();
    assertThat(assignments.size()).isPositive();

    var assignment = assignments.getFirst();
    assertThat(assignment.getAssignmentStatus().getId()).isZero();
    assertThat(assignment.getUpdatedTime()).isNull();
    assertThat(assignment.getCreatedTime()).isNotNull();
    assertThat(assignment.getRequestId()).isEqualTo(1);
    assertThat(assignment.getAccount().getId()).isEqualTo(1);
    assertThat(assignment.getAssignmentType().getId()).isZero();
  }

  @Getter
  @AllArgsConstructor
  public enum GetAccountAssignmentsTestCase {
    ARM_TEAM_CAN_SEE_ANY_ASSIGNMENTS(
        ARM_TEAM_ROLE, NO_ACCOUNT_USER_ID1, VVB_ACCOUNT_ID, 2, HttpStatus.OK),
    FINANCE_CAN_SEE_ANY_ASSIGNMENTS(
        FINANCE_ROLE, NO_ACCOUNT_USER_ID1, VVB_ACCOUNT_ID, 2, HttpStatus.OK),
    REVIEWER_CAN_SEE_OWN_ASSIGNMENTS(
        GS_REVIEWER_ROLE, VVB_ACCOUNT_USER_ID, VVB_ACCOUNT_ID, 2, HttpStatus.OK),
    VVB_CANNOT_SEE_OTHER_ASSIGNMENTS(
        VVB_ROLE, NO_ACCOUNT_USER_ID1, VVB_ACCOUNT_ID, null, HttpStatus.UNAUTHORIZED);
    private final String role;
    private final UUID userId;
    private final int accountId;
    private final Integer expectedNumberOfAssignments;
    private final HttpStatus expectedStatus;
  }

  @ParameterizedTest
  @EnumSource(GetAccountAssignmentsTestCase.class)
  void testGetAssignmentsForAccount(GetAccountAssignmentsTestCase testCase) {
    var response =
        client.getAssignments(testCase.getRole(), testCase.getUserId(), testCase.getAccountId());

    assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      assertThat(response.getBody()).isNotNull();
      var assignments = response.getBody().getContent();
      assertThat(assignments).hasSize(testCase.getExpectedNumberOfAssignments());
    }
  }

  @Test
  void testGetAssignmentById() {
    var id = 1;

    var response = client.getAssignment(id);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var assignment = response.getBody();
    assertThat(assignment.getId()).isEqualTo(1);
    assertThat(assignment.getAssignmentStatus().getId()).isZero();
    assertThat(assignment.getUpdatedTime()).isNull();
    assertThat(assignment.getCreatedTime()).isNotNull();
    assertThat(assignment.getRequestId()).isEqualTo(1);
    assertThat(assignment.getAccount().getId()).isEqualTo(1);
    assertThat(assignment.getAssignmentType().getId()).isZero();
  }

  @Getter
  @AllArgsConstructor
  public enum GetAssignmentTestCase {
    ARM_TEAM_CAN_SEE_ANY_ASSIGNMENTS(
        ARM_TEAM_ROLE, NO_ACCOUNT_USER_ID1, VVB_ASSIGNMENT_ID, HttpStatus.OK),
    FINANCE_CAN_SEE_ANY_ASSIGNMENTS(
        FINANCE_ROLE, NO_ACCOUNT_USER_ID1, VVB_ASSIGNMENT_ID, HttpStatus.OK),
    REVIEWER_CAN_SEE_OWN_ASSIGNMENTS(
        GS_REVIEWER_ROLE, VVB_ACCOUNT_USER_ID, VVB_ASSIGNMENT_ID, HttpStatus.OK),
    VVB_CANNOT_SEE_OTHER_ASSIGNMENTS(
        VVB_ROLE, NO_ACCOUNT_USER_ID1, VVB_ASSIGNMENT_ID, HttpStatus.UNAUTHORIZED),
    PROJECT_DEVELOPER_CANNOT_SEE_OTHER_PROJECTS(
        PROJECT_DEVELOPER_ROLE, NO_ACCOUNT_USER_ID1, VVB_ASSIGNMENT_ID, HttpStatus.UNAUTHORIZED),
    PROJECT_DEVELOPER_CANNOT_SEE_NON_VVB_ASSIGNMENTS(
        PROJECT_DEVELOPER_ROLE,
        NON_VVB_ACCOUNT_USER_ID,
        NON_VVB_ASSIGNMENT_ID,
        HttpStatus.UNAUTHORIZED),
    PROJECT_DEVELOPER_CAN_SEE_OWN_PROJECT_VVB_ASSIGNMENTS(
        PROJECT_DEVELOPER_ROLE, VVB_ACCOUNT_USER_ID, VVB_ASSIGNMENT_ID, HttpStatus.OK);
    private final String role;
    private final UUID userId;
    private final int assignmentId;
    private final HttpStatus expectedHttpStatus;
  }

  @ParameterizedTest
  @EnumSource(GetAssignmentTestCase.class)
  void testGetAssignment(GetAssignmentTestCase testCase) {
    var response =
        client.getAssignment(testCase.getRole(), testCase.getUserId(), testCase.getAssignmentId());

    assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedHttpStatus());
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE})
  void testCreateAssignment(String role) {
    var create = AssignmentCreateUpdateDTOMother.build();

    var response = client.createAssignment(role, create);

    var assignment = response.getBody();
    assertThat(assignment).isNotNull();
    try {
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(assignment.getId()).isPositive();

      var createdAssignment = repository.findById(assignment.getId()).orElseThrow();
      assertThat(createdAssignment.getAssignmentStatus().getId())
          .isEqualTo(create.getAssignmentStatusId());
      assertThat(createdAssignment.getAssignmentType().getId())
          .isEqualTo(create.getAssignmentTypeId());
      assertThat(createdAssignment.getCreatedTime()).isNotNull();
      assertThat(createdAssignment.getUpdatedTime()).isNotNull();
      assertThat(createdAssignment.getId()).isEqualTo(assignment.getId());
    } finally {
      repository.deleteById(assignment.getId());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE})
  void testUpdateAssignment(String role) {
    var entity =
        RequestAccountAssignmentMother.builder()
            .createdTime(ZonedDateTime.now().minusSeconds(60))
            .build();
    var saved = repository.save(entity);
    var assignmentId = saved.getId();
    var update = AssignmentCreateUpdateDTOMother.build();

    try {
      var response = client.updateAssignment(role, assignmentId, update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var assignment = response.getBody();
      assertThat(assignment).isNotNull();
      assertThat(assignment.getId()).isEqualTo(assignmentId);

      var updated = repository.findById(assignment.getId()).orElseThrow();
      assertThat(updated.getCreatedTime().toEpochSecond())
          .isEqualTo(entity.getCreatedTime().toEpochSecond());
      assertThat(updated.getUpdatedTime()).isNotNull();
      assertThat(updated.getUpdatedTime().toEpochSecond())
          .isGreaterThan(updated.getCreatedTime().toEpochSecond());
      assertThat(updated.getAssignmentStatus().getId()).isEqualTo(update.getAssignmentStatusId());
      assertThat(updated.getAssignmentType().getId()).isEqualTo(update.getAssignmentTypeId());
    } finally {
      repository.deleteById(saved.getId());
    }
  }

  @Test
  void testCreateAssignmentToCompletenessCheckSavesArmTeam() {
    var create =
        AssignmentCreateUpdateDTOMother.builder()
            .assignmentTypeId(COMPLETENESS_ASSIGNMENT_TYPE_ID)
            .build();

    var response = client.createAssignment(ADMIN_ROLE, create);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var assignment = response.getBody();
    assertThat(assignment).isNotNull();
    try {
      assertThat(assignment.getId()).isPositive();

      var createdAssignment = repository.findById(assignment.getId()).orElseThrow();
      assertThat(createdAssignment.getAssignmentStatus().getId())
          .isEqualTo(create.getAssignmentStatusId());
      assertThat(createdAssignment.getAssignmentType().getId())
          .isEqualTo(create.getAssignmentTypeId());
      assertThat(createdAssignment.getCreatedTime()).isNotNull();
      assertThat(createdAssignment.getUpdatedTime()).isNotNull();
      assertThat(createdAssignment.getId()).isEqualTo(assignment.getId());
      assertThat(createdAssignment.getAccount().getId())
          .isEqualTo(accountConfiguration.getArmTeamAccountId());
    } finally {
      repository.deleteById(assignment.getId());
    }
  }

  @Getter
  @AllArgsConstructor
  public enum GetRequestAssignmentsTestCase {
    ARM_TEAM_CAN_SEE_ANY_ASSIGNMENTS(
        ARM_TEAM_ROLE, NO_ACCOUNT_USER_ID1, REQUEST_ID_1, 2, HttpStatus.OK),
    FINANCE_CAN_SEE_ANY_ASSIGNMENTS(
        FINANCE_ROLE, NO_ACCOUNT_USER_ID1, REQUEST_ID_1, 2, HttpStatus.OK),
    VVB_CAN_SEE_OWN_ASSIGNMENTS(VVB_ROLE, VVB_ACCOUNT_USER_ID, REQUEST_ID_1, 1, HttpStatus.OK),
    PROJECT_DEVELOPER_CANNOT_SEE_OTHER_PROJECTS(
        PROJECT_DEVELOPER_ROLE, NO_ACCOUNT_USER_ID1, REQUEST_ID_2, 0, HttpStatus.UNAUTHORIZED),
    PROJECT_DEVELOPER_CAN_ONLY_SEE_OWN_PROJECT_AUDIT_ASSIGNMENTS(
        PROJECT_DEVELOPER_ROLE, VVB_ACCOUNT_USER_ID, REQUEST_ID_1, 1, HttpStatus.OK);
    private final String role;
    private final UUID userId;
    private final int requestId;
    private final int expectedAssignmentCount;
    private final HttpStatus expectedHttpStatus;
  }

  @ParameterizedTest
  @EnumSource(GetRequestAssignmentsTestCase.class)
  void testGetAssignmentsForRequest(GetRequestAssignmentsTestCase testCase) {
    var response =
        client.getRequestAssignments(
            testCase.getRole(), testCase.getUserId(), testCase.getRequestId());

    assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedHttpStatus());
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      assertThat(response.getBody()).isNotNull();
      var assignments = response.getBody();
      assertThat(assignments).hasSize(testCase.getExpectedAssignmentCount());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2})
  void testGetAssignmentsForRequestFilteredByAssignmentStatus(Integer expectedAssignmentStatusId) {
    var query = String.format("?assignmentStatusId=%d", expectedAssignmentStatusId);

    var response =
        client.getRequestAssignments(FINANCE_ROLE, NO_ACCOUNT_USER_ID1, REQUEST_ID_1, query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var assignments = response.getBody();
    assertThat(assignments)
        .map(RequestAccountAssignmentDTO::getAssignmentStatus)
        .map(ReferenceDataType::getId)
        .allMatch(
            assignmentStatusId -> Objects.equals(assignmentStatusId, expectedAssignmentStatusId));
  }

  @Test
  void testUpdateAssignmentStatus() {
    var assignmentId = 1;
    var update = new AssignmentStatusUpdate(CANCELLED);

    var response =
        client.updateAssignmentStatus(GS_REVIEWER_ROLE, VVB_ACCOUNT_USER_ID, assignmentId, update);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testUpdateAssignmentStatusErrorOnUnauthenticated() {
    var assignmentId = 1;
    var update = new AssignmentStatusUpdate(CANCELLED);

    var response = client.updateAssignmentStatusUnauthenticated(assignmentId, update);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testUpdateAssignmentStatusErrorOnWrongRole() {
    var assignmentId = 1;
    var update = new AssignmentStatusUpdate(CANCELLED);

    var response = client.updateAssignmentStatus(PROJECT_DEVELOPER_ROLE, assignmentId, update);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testUpdateAssignmentStatusErrorOnRestrictedCurrentStatus() {
    var assignmentId = 2;
    var update = new AssignmentStatusUpdate(CANCELLED);

    var response =
        client.updateAssignmentStatus(GS_REVIEWER_ROLE, VVB_ACCOUNT_USER_ID, assignmentId, update);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testCreateAssignmentErrorOnUnauthenticated() {
    var create = AssignmentCreateUpdateDTOMother.build();

    var response = client.createAssignmentUnauthenticated(create);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testCreateAssignmentErrorOnWrongRole() {
    var create = AssignmentCreateUpdateDTOMother.build();

    var response = client.createAssignment(GS_REVIEWER_ROLE, create);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testUpdateAssignmentErrorOnUnauthenticated() {
    var assignmentId = 42;
    var update = AssignmentCreateUpdateDTOMother.build();

    var response = client.updateAssignmentUnauthenticated(assignmentId, update);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testUpdateAssignmentErrorOnWrongRole() {
    var assignmentId = 42;
    var update = AssignmentCreateUpdateDTOMother.build();

    var response = client.updateAssignment(GS_REVIEWER_ROLE, assignmentId, update);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Getter
  @AllArgsConstructor
  public enum CreateTestCase {
    NOT_MY_PROJECT(NO_ACCOUNT_USER_ID1, VVB_ACCOUNT_ID, HttpStatus.UNAUTHORIZED),
    OWN_PROJECT_ASSIGN_VVB(VVB_ACCOUNT_USER_ID, VVB_ACCOUNT_ID, HttpStatus.OK),
    OWN_PROJECT_ASSIGN_NON_VVB(VVB_ACCOUNT_USER_ID, NON_VVB_ACCOUNT_ID, HttpStatus.OK);
    private final UUID userId;
    private final Integer accountId;
    private final HttpStatus expectedStatus;
  }

  @ParameterizedTest
  @EnumSource(CreateTestCase.class)
  void testCreateAssignment(CreateTestCase testCase) {
    var create =
        AssignmentCreateUpdateDTOMother.builder()
            .accountId(testCase.getAccountId())
            .assignmentStatusId(PENDING)
            .assignmentTypeId(2)
            .build();

    var response = client.createAssignment(PROJECT_DEVELOPER_ROLE, testCase.getUserId(), create);

    var assignment = response.getBody();

    try {
      assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
      if (response.getStatusCode().is2xxSuccessful()) {
        assertThat(assignment).isNotNull();
      }
    } finally {
      if (Objects.nonNull(assignment) && Objects.nonNull(assignment.getId())) {
        repository.deleteById(assignment.getId());
      }
    }
  }

  @Getter
  @AllArgsConstructor
  public enum UpdateTestCase {
    NOT_MY_PROJECT(
        NO_ACCOUNT_USER_ID1, VVB_ACCOUNT_ID, PENDING, VVB_ACCOUNT_ID, HttpStatus.UNAUTHORIZED),
    OWN_PROJECT_PENDING_ASSIGN_FROM_VVB_TO_VVB(
        VVB_ACCOUNT_USER_ID, VVB_ACCOUNT_ID, PENDING, VVB_ACCOUNT_ID, HttpStatus.OK),
    OWN_PROJECT_ASSIGNED_VVB_TO_VVB(
        VVB_ACCOUNT_USER_ID, VVB_ACCOUNT_ID, ACCEPTED, VVB_ACCOUNT_ID, HttpStatus.OK),
    OWN_PROJECT_PENDING_ASSIGN_FROM_VVB_TO_NON_VVB(
        VVB_ACCOUNT_USER_ID, VVB_ACCOUNT_ID, PENDING, NON_VVB_ACCOUNT_ID, HttpStatus.OK),
    OWN_PROJECT_PENDING_ASSIGN_FROM_NON_VVB_TO_VVB(
        VVB_ACCOUNT_USER_ID, NON_VVB_ACCOUNT_ID, PENDING, VVB_ACCOUNT_ID, HttpStatus.OK);
    private final UUID userId;
    private final Integer currentAccountId;
    private final Integer currentAssignmentStatus;
    private final Integer newAccountId;
    private final HttpStatus expectedStatus;
  }

  @ParameterizedTest
  @EnumSource(UpdateTestCase.class)
  void testUpdateAssignment(UpdateTestCase testCase) {
    var assignment =
        repository.save(
            RequestAccountAssignmentMother.builder()
                .account(AccountMother.armAccount())
                .requestId(2)
                .assignmentType(new AssignmentType(1))
                .assignmentStatus(new AssignmentStatus(testCase.currentAssignmentStatus))
                .build());
    var assignmentUpdate =
        AssignmentCreateUpdateDTOMother.builder()
            .accountId(testCase.getNewAccountId())
            .assignmentStatusId(PENDING)
            .assignmentTypeId(3)
            .build();

    var response =
        client.updateAssignment(
            PROJECT_DEVELOPER_ROLE, testCase.getUserId(), assignment.getId(), assignmentUpdate);

    try {
      assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
    } finally {
      repository.deleteById(assignment.getId());
    }
  }
}
