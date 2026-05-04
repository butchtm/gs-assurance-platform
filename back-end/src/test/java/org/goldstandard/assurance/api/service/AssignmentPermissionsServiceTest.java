package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.CANCELLED_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.COMPLETED_APPROVED_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.COMPLETED_REJECTED_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_AUDIT_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_PREPARATION_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_PROGRESS_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.NOT_REQUESTED_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.OMITTED_ID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignmentPermissionsServiceTest {

  @InjectMocks AssignmentPermissionsService assignmentPermissionsService;

  @Mock AuthenticatedUserSupplier userSupplier;

  @Mock RequestRepository repository;

  @Mock AssignmentFinder assignmentFinder;

  @Captor ArgumentCaptor<Integer> requestId;

  @Captor ArgumentCaptor<Integer> accountId;

  @ParameterizedTest
  @ValueSource(
      ints = {
        COMPLETED_APPROVED_ID,
        COMPLETED_REJECTED_ID,
        OMITTED_ID,
        CANCELLED_ID,
        NOT_REQUESTED_ID
      })
  void shouldNotLookUpAssignmentsIfRequestIsInInactiveState(int statusId) {
    var expectedRequest = givenRequestWithStatusIds(statusId);
    givenUserAccountId();
    givenProjectContainsRequest(expectedRequest);

    var result =
        assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(
            expectedRequest.getProject());

    assertThat(result).isFalse();
    verifyNoInteractions(assignmentFinder);
  }

  @ParameterizedTest
  @ValueSource(ints = {IN_PREPARATION_ID, IN_PROGRESS_ID, IN_AUDIT_ID})
  void shouldLookUpAssignmentsIfRequestIsNotInInactiveState(int activeIds) {
    var expectedRequest = givenRequestWithStatusIds(activeIds);
    var expectedAccountId = givenUserAccountId();
    givenProjectContainsRequest(expectedRequest);

    assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(
        expectedRequest.getProject());

    verify(assignmentFinder).isRequestAssignedToAccount(requestId.capture(), accountId.capture());
    var actualRequestId = requestId.getValue();
    var actualAccountId = accountId.getValue();

    assertThat(actualRequestId).isEqualTo(expectedRequest.getId());
    assertThat(actualAccountId).isEqualTo(expectedAccountId);
  }

  @Test
  void shouldNotLookUpAssignmentsIfNoUserLinkedToAccount() {
    var expectedRequest = givenRequestWithStatusIds(IN_PREPARATION_ID);

    var result =
        assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(
            expectedRequest.getProject());

    assertThat(result).isFalse();
    verifyNoInteractions(assignmentFinder);
  }

  private void givenProjectContainsRequest(Request request) {
    when(repository.findByProjectId(request.getProject().getId())).thenReturn(List.of(request));
  }

  private Integer givenUserAccountId() {
    Integer accountId = 99;
    when(userSupplier.getAuthenticatedUserAccountId()).thenReturn(Optional.of(accountId));
    return accountId;
  }

  private static Request givenRequestWithStatusIds(int statusId) {
    var project = Project.builder().id(1).gsid("GS123").build();
    var status = new RequestStatus();
    status.setId(statusId);
    return Request.builder().id(989).currentRequestStatus(status).project(project).build();
  }
}
