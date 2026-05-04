package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.goldstandard.assurance.api.service.notification.AssignmentNotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentService {

  private final RequestFinder requestFinder;
  private final AssignmentPermissionsService assignmentPermissionsService;
  private final BatchRequestChildValidator batchRequestValidator;
  private final RequestAccountAssignmentRepository repository;
  private final AssignmentNotificationService notificationService;
  private final Clock clock;
  private final AssignmentFinder assignmentFinder;
  private final AssignmentFactory assignmentFactory;

  public RequestAccountAssignment createAssignment(AssignmentCreateUpdateDTO create) {
    var request = requestFinder.getRequestById(create.getRequestId());
    assignmentPermissionsService.validateCanAssignToRequest(request);
    batchRequestValidator.validateIsNotBatchChild(request);
    var assignment = assignmentFactory.build(create);
    return saveAndSendNotificationIfRequired(assignment);
  }

  public RequestAccountAssignment updateAssignment(
      Integer assignmentId, AssignmentCreateUpdateDTO update) {
    var request = requestFinder.getRequestById(update.getRequestId());
    assignmentPermissionsService.validateCanAssignToRequest(request);
    batchRequestValidator.validateIsNotBatchChild(request);
    var assignment = assignmentFinder.forceFindAssignmentById(assignmentId);
    var updatedAssignment = assignmentFactory.applyUpdates(update, assignment);
    return saveAndSendNotificationIfRequired(updatedAssignment);
  }

  public void updateAssignmentStatus(Integer assignmentId, Integer assignmentStatusId) {
    var assignment = assignmentFinder.forceFindAssignmentById(assignmentId);
    assignmentPermissionsService.validateCanUpdateStatus(assignment);
    repository.updateAssignmentStatus(assignmentId, assignmentStatusId, ZonedDateTime.now(clock));
  }

  private RequestAccountAssignment saveAndSendNotificationIfRequired(
      RequestAccountAssignment assignment) {
    var savedAssignment = repository.save(assignment);
    sendNotificationIfAssociationExists(savedAssignment);
    return savedAssignment;
  }

  private void sendNotificationIfAssociationExists(RequestAccountAssignment assignment) {
    notificationService
        .retrieveRelevantAssignmentNotification(
            assignment.getAssignmentType().getId(), assignment.getAssignmentStatus().getId())
        .ifPresent(
            notification ->
                notificationService.sendAssignmentNotification(
                    notification, assignment.getAccount().getId(), assignment.getRequestId()));
  }
}
