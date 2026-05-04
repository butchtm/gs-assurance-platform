package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.dto.AssignmentCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.goldstandard.assurance.api.entity.notification.AssignmentStatusNotification;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.AssignmentCreateUpdateDTOMother;
import org.goldstandard.assurance.api.fixtures.RequestAccountAssignmentMother;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.goldstandard.assurance.api.service.notification.AssignmentNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

  @InjectMocks private AssignmentService assignmentService;

  @Mock private RequestFinder requestFinder;

  @Mock private AssignmentPermissionsService assignmentPermissionsService;

  @Mock private BatchRequestChildValidator batchRequestValidator;

  @Mock private RequestAccountAssignmentRepository repository;

  @Mock private AssignmentNotificationService notificationService;

  @Mock private AssignmentFinder assignmentFinder;

  @Mock private AssignmentFactory assignmentFactory;

  @Captor private ArgumentCaptor<RequestAccountAssignment> captor;

  @Test
  void shouldNotCreateAssignmentIfNotPermitted() {
    var create = AssignmentCreateUpdateDTOMother.build();
    var request = givenRequestExists(create.getRequestId());
    var expectedError = new PermissionException("create-permission-error");
    doThrow(expectedError).when(assignmentPermissionsService).validateCanAssignToRequest(request);

    Throwable error = catchThrowable(() -> assignmentService.createAssignment(create));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldNotCreateAssignmentForChildBatchRequest() {
    var create = AssignmentCreateUpdateDTOMother.build();
    var request = givenRequestExists(create.getRequestId());
    var expectedError = new BatchRequestException("create-child-error");
    doThrow(expectedError).when(batchRequestValidator).validateIsNotBatchChild(request);

    Throwable error = catchThrowable(() -> assignmentService.createAssignment(create));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldNotUpdateAssignmentWhenNotPermitted() {
    var update = AssignmentCreateUpdateDTOMother.build();
    var request = givenRequestExists(update.getRequestId());
    var expectedError = new PermissionException("update-permission-error");
    doThrow(expectedError).when(assignmentPermissionsService).validateCanAssignToRequest(request);
    var assignmentId = 654;

    Throwable error =
        catchThrowable(() -> assignmentService.updateAssignment(assignmentId, update));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldNotUpdateAssignmentForChildBatchRequest() {
    var update = AssignmentCreateUpdateDTOMother.build();
    var request = givenRequestExists(update.getRequestId());
    var expectedError = new BatchRequestException("update-child-error");
    doThrow(expectedError).when(batchRequestValidator).validateIsNotBatchChild(request);
    var assignmentId = 987;

    Throwable error =
        catchThrowable(() -> assignmentService.updateAssignment(assignmentId, update));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldSaveCreatedAssignment() {
    var create = AssignmentCreateUpdateDTOMother.build();
    givenRequestExists(create.getRequestId());
    givenNotificationNotRequired(create);
    var createdAssignment = givenCreatedAssignment(create);

    assignmentService.createAssignment(create);

    verify(repository).save(captor.capture());
    assertThat(captor.getValue()).isEqualTo(createdAssignment);
  }

  @Test
  void createShouldReturnSavedAssignment() {
    var create = AssignmentCreateUpdateDTOMother.build();
    givenRequestExists(create.getRequestId());
    givenNotificationNotRequired(create);
    var savedAssignment = givenCreatedAssignment(create);

    var result = assignmentService.createAssignment(create);

    assertThat(result).isEqualTo(savedAssignment);
  }

  @Test
  void shouldNotSendNotificationOnCreateAssignmentIfNotificationIsNotRequired() {
    var create = AssignmentCreateUpdateDTOMother.build();
    givenRequestExists(create.getRequestId());
    givenNotificationNotRequired(create);
    givenCreatedAssignment(create);

    assignmentService.createAssignment(create);

    verify(notificationService, times(0)).sendAssignmentNotification(any(), anyInt(), anyInt());
  }

  @Test
  void shouldSendNotificationOnAssignmentIfNotificationRequired() {
    var create = AssignmentCreateUpdateDTOMother.build();
    givenRequestExists(create.getRequestId());
    var notification = givenNotificationRequired(create);
    var savedAssignment = givenCreatedAssignment(create);

    assignmentService.createAssignment(create);

    verify(notificationService)
        .sendAssignmentNotification(
            notification, savedAssignment.getAccount().getId(), savedAssignment.getRequestId());
  }

  @Test
  void shouldSaveUpdatedAssignment() {
    var update = AssignmentCreateUpdateDTOMother.build();
    givenRequestExists(update.getRequestId());
    givenNotificationNotRequired(update);
    var existingAssignment = givenExistingAssignment();
    var updatedAssignment = givenUpdatedAssignment(update, existingAssignment);

    assignmentService.updateAssignment(existingAssignment.getId(), update);

    verify(repository).save(captor.capture());
    assertThat(captor.getValue()).isEqualTo(updatedAssignment);
  }

  @Test
  void updateShouldReturnUpdatedAssignment() {
    var update = AssignmentCreateUpdateDTOMother.build();
    givenRequestExists(update.getRequestId());
    givenNotificationNotRequired(update);
    var existingAssignment = givenExistingAssignment();
    var updatedAssignment = givenUpdatedAssignment(update, existingAssignment);

    var result = assignmentService.updateAssignment(existingAssignment.getId(), update);

    assertThat(result).isEqualTo(updatedAssignment);
  }

  private Request givenRequestExists(int id) {
    var request = givenRequest(id);
    when(requestFinder.getRequestById(id)).thenReturn(request);
    return request;
  }

  private Request givenRequest(int id) {
    var request = new Request();
    request.setId(id);
    return request;
  }

  private void givenNotificationNotRequired(AssignmentCreateUpdateDTO dto) {
    when(notificationService.retrieveRelevantAssignmentNotification(
            dto.getAssignmentTypeId(), dto.getAssignmentStatusId()))
        .thenReturn(Optional.empty());
  }

  private AssignmentStatusNotification givenNotificationRequired(AssignmentCreateUpdateDTO dto) {
    var notification = mock(AssignmentStatusNotification.class);
    when(notificationService.retrieveRelevantAssignmentNotification(
            dto.getAssignmentTypeId(), dto.getAssignmentStatusId()))
        .thenReturn(Optional.ofNullable(notification));
    return notification;
  }

  private RequestAccountAssignment givenCreatedAssignment(AssignmentCreateUpdateDTO dto) {
    var createdAssignment = buildAssignment(dto);
    when(assignmentFactory.build(dto)).thenReturn(createdAssignment);
    when(repository.save(createdAssignment)).thenReturn(createdAssignment);
    return createdAssignment;
  }

  private RequestAccountAssignment givenUpdatedAssignment(
      AssignmentCreateUpdateDTO dto, RequestAccountAssignment existingAssignment) {
    var updatedAssignment = buildAssignment(dto);
    when(assignmentFactory.applyUpdates(dto, existingAssignment)).thenReturn(updatedAssignment);
    when(repository.save(updatedAssignment)).thenReturn(updatedAssignment);
    return updatedAssignment;
  }

  private RequestAccountAssignment givenExistingAssignment() {
    var assignment = mock(RequestAccountAssignment.class);
    when(assignmentFinder.forceFindAssignmentById(assignment.getId())).thenReturn(assignment);
    return assignment;
  }

  private static RequestAccountAssignment buildAssignment(AssignmentCreateUpdateDTO dto) {
    return RequestAccountAssignmentMother.builder()
        .id(123)
        .assignmentType(new AssignmentType(dto.getAssignmentTypeId()))
        .assignmentStatus(new AssignmentStatus(dto.getAssignmentStatusId()))
        .account(AccountMother.withIdOnly(dto.getAccountId()))
        .build();
  }
}
