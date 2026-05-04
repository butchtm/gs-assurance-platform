package org.goldstandard.assurance.api.service.notification;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.notification.AssignmentStatusNotification;
import org.goldstandard.assurance.api.repository.notification.AssignmentNotificationRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentNotificationService {

  private final NotificationService notificationService;
  private final AssignmentNotificationRepository assignmentNotificationRepository;
  private final AccountFinder accountFinder;
  private final RequestFinder requestFinder;

  public Optional<AssignmentStatusNotification> retrieveRelevantAssignmentNotification(
      Integer assignmentType, Integer assignmentStatus) {
    return assignmentNotificationRepository.findNotificationByAssignmentTypeIdAndAssignmentStatusId(
        assignmentType, assignmentStatus);
  }

  public void sendAssignmentNotification(
      AssignmentStatusNotification notification, Integer accountId, Integer requestId) {
    var account = accountFinder.forceGetAccountById(accountId);
    var request = requestFinder.getRequestById(requestId);
    var template = notification.getTemplate();

    notificationService.sendAccountNotification(template, request, account);
    log.info(
        "Notification sent out to account [{}] for requestId: [{}], "
            + "where assignment has status [{}] and assignment type [{}], "
            + "using templateId [{}].",
        account.getName(),
        request.getId(),
        notification.getAssignmentStatus().getCode(),
        notification.getAssignmentType().getCode(),
        notification.getTemplate().getId());
  }
}
