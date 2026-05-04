package org.goldstandard.assurance.api.service.notification;

import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.AUDIT_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.ISSUANCE;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.configuration.AccountConfiguration;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.entity.notification.RequestStatusAssigneeNotification;
import org.goldstandard.assurance.api.entity.notification.RequestStatusNotification;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.notification.RequestStatusNotificationRepository;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestNotificationService {

  private final NotificationService notificationService;
  private final AccountConfiguration accountConfiguration;
  private final AccountFinder accountFinder;
  private final AssignmentFinder assignmentFinder;
  private final RequestStatusNotificationRepository requestStatusNotificationRepository;

  @Transactional
  public void sendRequestNotification(Request request, Integer previousRequestStatusId) {
    final RequestStatusNotification notification =
        getNotificationForTransition(
            previousRequestStatusId,
            request.getCurrentRequestStatus().getId(),
            request.getCurrentRoundIndex() != null ? request.getCurrentRoundIndex() : 0);

    if (notification != null) {
      if (shouldSendRequestOwnerNotification(request, notification)) {
        Optional.ofNullable(notification.getRequestOwnerTemplate())
            .ifPresent(
                notificationTemplate ->
                    sendAccountNotification(
                        notificationTemplate, request, request.getProject().getAccountId()));
      }
      Optional.ofNullable(notification.getArmTeamTemplate())
          .ifPresent(
              notificationTemplate ->
                  sendAccountNotification(
                      notificationTemplate, request, accountConfiguration.getArmTeamAccountId()));
      Optional.ofNullable(notification.getRegistryTeamTemplate())
          .ifPresent(
              notificationTemplate ->
                  sendAccountNotification(
                      notificationTemplate,
                      request,
                      accountConfiguration.getRegistryTeamAccountId()));
      if (assignmentFinder.hasAssignmentOfType(request.getId(), AUDIT_ASSIGNMENT_TYPE_ID)) {
        Optional.ofNullable(notification.getVvbTemplate())
            .ifPresent(
                notificationTemplate ->
                    sendAccountNotification(
                        notification.getVvbTemplate(), request, getAssignedVVB(request).getId()));
      }

      if (request.getInvoice() == null) {
        Optional.ofNullable(notification.getFinanceTeamTemplate())
            .ifPresent(
                notificationTemplate ->
                    sendAccountNotification(
                        notificationTemplate,
                        request,
                        accountConfiguration.getFinanceTeamAccountId()));
      }

      notification
          .getAssigneeNotifications()
          .forEach(assigneeNotification -> sendAssigneeNotification(assigneeNotification, request));
    }
  }

  private boolean shouldSendRequestOwnerNotification(
      Request request, RequestStatusNotification notification) {
    if (notification.getRequestOwnerTemplate() == null) {
      return false;
    }

    if (request.getRequestType().getId() == ISSUANCE) {
      return "REQUEST_CLOSED".equals(notification.getRequestOwnerTemplate().getName());
    }

    return true;
  }

  private void sendAssigneeNotification(
      RequestStatusAssigneeNotification assigneeNotification, Request request) {
    var template = assigneeNotification.getAssigneeTemplate();
    var assignees =
        assignmentFinder.findAssignedAccountsByRequestIdAndAssignmentTypeId(
            request.getId(), assigneeNotification.getAssignmentTypeId());
    assignees.forEach(
        assignee -> notificationService.sendAccountNotification(template, request, assignee));
  }

  private void sendAccountNotification(
      NotificationTemplate template, Request request, Integer accountId) {
    Account account = accountFinder.forceGetAccountById(accountId);
    notificationService.sendAccountNotification(template, request, account);
  }

  private RequestStatusNotification getNotificationForTransition(
      Integer fromStatusId, Integer toStatusId, Integer roundIndex) {
    final List<RequestStatusNotification> candidateNotifications =
        requestStatusNotificationRepository.findNotificationsForTransition(
            fromStatusId, toStatusId, roundIndex);
    if (candidateNotifications.isEmpty()) {
      return null;
    }

    if (candidateNotifications.size() > 2) {
      throw new DataException(
          "Cannot determine notification for request state transition (%d)->(%d) with round index %d",
          fromStatusId, toStatusId, roundIndex);
    }
    // 1 or 2 rows, and if 2 rows, the second is the less specific source status (null)
    return candidateNotifications.getFirst();
  }

  private Account getAssignedVVB(Request request) {
    return assignmentFinder
        .findAssignedAccountsByRequestIdAndAssignmentTypeId(
            request.getId(), AUDIT_ASSIGNMENT_TYPE_ID)
        .stream()
        .findFirst()
        .orElseThrow(() -> new NotFoundException("Assigned VVB not found to send notification"));
  }
}
