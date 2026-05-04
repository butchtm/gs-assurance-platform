package org.goldstandard.assurance.api.service.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.AssignmentStatus;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.goldstandard.assurance.api.entity.notification.AssignmentStatusNotification;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.repository.notification.AssignmentNotificationRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssignmentNotificationServiceTest {

  @InjectMocks AssignmentNotificationService assignmentNotificationService;

  @Mock AssignmentNotificationRepository assignmentNotificationRepository;

  @Mock private NotificationService notificationService;

  @Mock private NotificationModelFactory notificationModelFactory;

  @Mock private AccountFinder accountFinder;

  @Mock private RequestFinder requestFinder;

  @Captor ArgumentCaptor<NotificationTemplate> notificationTemplateArgumentCaptor;

  @Captor ArgumentCaptor<Account> accountArgumentCaptor;

  @Captor ArgumentCaptor<Request> requestArgumentCaptor;

  private static final Integer ASSIGNMENT_TYPE_ID = 0;

  private static final Integer ASSIGNMENT_STATUS_ID = 1;

  private static final Integer ACCOUNT_ID = 2;

  private static final Integer REQUEST_ID = 3;

  private static final Integer TEMPLATE_ID = 4;

  private static AssignmentStatus setupStatus() {
    var status = new AssignmentStatus();
    status.setId(ASSIGNMENT_STATUS_ID);
    return status;
  }

  private static AssignmentType setupType() {
    var type = new AssignmentType();
    type.setId(ASSIGNMENT_TYPE_ID);
    return type;
  }

  private static NotificationTemplate setupTemplate() {
    var template = new NotificationTemplate();
    template.setId(TEMPLATE_ID);
    return template;
  }

  private static Account setupAccount() {
    var account = new Account();
    account.setId(ACCOUNT_ID);
    return account;
  }

  private static Request setupRequest() {
    var request = new Request();
    request.setId(ACCOUNT_ID);
    return request;
  }

  private static AssignmentStatusNotification getAssignmentNotifications() {
    var assignmentStatusNotification = new AssignmentStatusNotification();
    assignmentStatusNotification.setAssignmentStatus(setupStatus());
    assignmentStatusNotification.setAssignmentType(setupType());
    assignmentStatusNotification.setTemplate(setupTemplate());
    return assignmentStatusNotification;
  }

  @Test
  void findNotificationByAssignment() {
    var assignmentStatusNotification = getAssignmentNotifications();
    when(assignmentNotificationRepository.findNotificationByAssignmentTypeIdAndAssignmentStatusId(
            ASSIGNMENT_TYPE_ID, ASSIGNMENT_STATUS_ID))
        .thenReturn(Optional.of(assignmentStatusNotification));
    var result =
        assignmentNotificationService.retrieveRelevantAssignmentNotification(
            ASSIGNMENT_TYPE_ID, ASSIGNMENT_STATUS_ID);
    assertEquals(assignmentStatusNotification, result.get());
  }

  @Test
  void sendAssignmentNotifications() {
    var assignmentStatusNotification = getAssignmentNotifications();
    var request = setupRequest();
    var account = setupAccount();
    when(accountFinder.forceGetAccountById(ACCOUNT_ID)).thenReturn(account);
    when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);

    assignmentNotificationService.sendAssignmentNotification(
        assignmentStatusNotification, ACCOUNT_ID, REQUEST_ID);

    verify(notificationService, times(1))
        .sendAccountNotification(
            notificationTemplateArgumentCaptor.capture(),
            requestArgumentCaptor.capture(),
            accountArgumentCaptor.capture());

    var templateCapture = notificationTemplateArgumentCaptor.getValue();
    var accountCapture = accountArgumentCaptor.getValue();
    var requestCapture = requestArgumentCaptor.getValue();

    assertEquals(assignmentStatusNotification.getTemplate().getId(), templateCapture.getId());
    assertEquals(ACCOUNT_ID, accountCapture.getId());
    assertEquals(request.getId(), requestCapture.getId());
  }
}
