package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.AUDIT_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.DESIGN_CERTIFICATION_RENEWAL_PROJECT;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.ISSUANCE;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.PRELIMINARY_REVIEW_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.goldstandard.assurance.api.configuration.AccountConfiguration;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.goldstandard.assurance.api.entity.notification.RequestStatusAssigneeNotification;
import org.goldstandard.assurance.api.entity.notification.RequestStatusNotification;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.repository.notification.RequestStatusNotificationRepository;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestNotificationServiceTest {
  private static final String TEST_VERSION = "v0.0.1";

  private static final Integer TEST_PREVIOUS_REQUEST_STATUS_ID = 0;
  private static final Integer TEST_CURRENT_REQUEST_STATUS_ID = 1;

  private static final Integer TEST_PD_ACCOUNT_ID = 5336;

  private static final Integer TEST_ARM_TEAM_ACCOUNT_ID = 324;

  private static final Integer TEST_REGISTRY_TEAM_ACCOUNT_ID = 7548;

  private static final Integer TEST_VVB_ACCOUNT_ID = 789;

  private static final Integer TEST_REQUEST_ID = 123;

  @InjectMocks RequestNotificationService requestNotificationService;

  @Mock RequestStatusNotificationRepository requestStatusNotificationRepository;

  @Mock NotificationService notificationService;

  @Mock AccountFinder accountFinder;

  @Mock AccountConfiguration accountConfiguration;

  @Mock AssignmentFinder assignmentFinder;

  @Captor ArgumentCaptor<NotificationTemplate> templateCaptor;

  @Captor ArgumentCaptor<Account> accountCaptor;

  @Captor ArgumentCaptor<Request> requestArgumentCaptor;

  @Test
  void testSendRequestNotification() {
    var testRequest = createTestRequest();

    when(requestStatusNotificationRepository.findNotificationsForTransition(
            anyInt(), anyInt(), anyInt()))
        .thenReturn(
            List.of(createTestRequestStatusNotification(), new RequestStatusNotification()));

    when(accountConfiguration.getArmTeamAccountId()).thenReturn(TEST_ARM_TEAM_ACCOUNT_ID);
    when(accountConfiguration.getRegistryTeamAccountId()).thenReturn(TEST_REGISTRY_TEAM_ACCOUNT_ID);
    var vvbAccount = mock(Account.class);
    when(assignmentFinder.hasAssignmentOfType(testRequest.getId(), AUDIT_ASSIGNMENT_TYPE_ID))
        .thenReturn(true);
    when(assignmentFinder.findAssignedAccountsByRequestIdAndAssignmentTypeId(
            testRequest.getId(), AUDIT_ASSIGNMENT_TYPE_ID))
        .thenReturn(List.of(vvbAccount));
    when(vvbAccount.getId()).thenReturn(TEST_VVB_ACCOUNT_ID);

    when(accountFinder.forceGetAccountById(TEST_PD_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_PD_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_ARM_TEAM_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_ARM_TEAM_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_REGISTRY_TEAM_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_REGISTRY_TEAM_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_VVB_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_VVB_ACCOUNT_ID));

    requestNotificationService.sendRequestNotification(
        testRequest, TEST_PREVIOUS_REQUEST_STATUS_ID);

    verify(notificationService, times(5))
        .sendAccountNotification(
            templateCaptor.capture(), requestArgumentCaptor.capture(), accountCaptor.capture());

    assertThat(requestArgumentCaptor.getValue().getId()).isEqualTo(TEST_REQUEST_ID);
    assertThat(templateCaptor.getAllValues().stream().map(NotificationTemplate::getName))
        .containsExactly(
            "pd_template",
            "arm_template",
            "registry_template",
            "vvb_template",
            "assignment_type_0_template");
    assertThat(accountCaptor.getAllValues().stream().map(Account::getId))
        .containsExactly(
            TEST_PD_ACCOUNT_ID,
            TEST_ARM_TEAM_ACCOUNT_ID,
            TEST_REGISTRY_TEAM_ACCOUNT_ID,
            TEST_VVB_ACCOUNT_ID,
            TEST_VVB_ACCOUNT_ID);
  }

  @Test
  void testNoNotificationIfStatusTransitionUnmapped() {
    when(requestStatusNotificationRepository.findNotificationsForTransition(
            anyInt(), anyInt(), anyInt()))
        .thenReturn(List.of());

    requestNotificationService.sendRequestNotification(
        Request.builder().currentRequestStatus(new RequestStatus(1)).build(), 0);

    verifyNoInteractions(notificationService);
  }

  @Test
  void testSendNotificationErrorsIfAmbiguousMappings() {
    final RequestStatusNotification testNotification = new RequestStatusNotification();
    when(requestStatusNotificationRepository.findNotificationsForTransition(
            anyInt(), anyInt(), anyInt()))
        .thenReturn(List.of(testNotification, testNotification, testNotification));

    final DataException exception =
        assertThrows(
            DataException.class,
            () ->
                requestNotificationService.sendRequestNotification(
                    Request.builder().currentRequestStatus(new RequestStatus(1)).build(), 0));

    assertThat(exception.getMessage())
        .startsWith("Cannot determine notification for request state transition");
    verifyNoInteractions(notificationService);
  }

  @Test
  void testRequestOwnerNotificationsSkippedForIssuanceRequestTypesWhenTemplateIsNotRequestClosed() {
    var testRequest = createTestRequest();
    testRequest.setRequestType(new RequestType(ISSUANCE));

    when(requestStatusNotificationRepository.findNotificationsForTransition(
            anyInt(), anyInt(), anyInt()))
        .thenReturn(List.of(createTestRequestStatusNotification()));

    when(accountConfiguration.getArmTeamAccountId()).thenReturn(TEST_ARM_TEAM_ACCOUNT_ID);
    when(accountConfiguration.getRegistryTeamAccountId()).thenReturn(TEST_REGISTRY_TEAM_ACCOUNT_ID);

    var vvbAccount = mock(Account.class);
    when(assignmentFinder.hasAssignmentOfType(testRequest.getId(), AUDIT_ASSIGNMENT_TYPE_ID))
        .thenReturn(true);

    when(assignmentFinder.findAssignedAccountsByRequestIdAndAssignmentTypeId(
            testRequest.getId(), AUDIT_ASSIGNMENT_TYPE_ID))
        .thenReturn(List.of(vvbAccount));
    when(vvbAccount.getId()).thenReturn(TEST_VVB_ACCOUNT_ID);

    when(accountFinder.forceGetAccountById(TEST_ARM_TEAM_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_ARM_TEAM_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_REGISTRY_TEAM_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_REGISTRY_TEAM_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_VVB_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_VVB_ACCOUNT_ID));

    requestNotificationService.sendRequestNotification(
        testRequest, TEST_PREVIOUS_REQUEST_STATUS_ID);

    verify(notificationService, times(4))
        .sendAccountNotification(
            templateCaptor.capture(), requestArgumentCaptor.capture(), accountCaptor.capture());

    assertThat(requestArgumentCaptor.getValue().getId()).isEqualTo(TEST_REQUEST_ID);
    assertThat(templateCaptor.getAllValues().stream().map(NotificationTemplate::getName))
        .containsExactly(
            "arm_template", "registry_template", "vvb_template", "assignment_type_0_template");
    assertThat(accountCaptor.getAllValues().stream().map(Account::getId))
        .containsExactly(
            TEST_ARM_TEAM_ACCOUNT_ID,
            TEST_REGISTRY_TEAM_ACCOUNT_ID,
            TEST_VVB_ACCOUNT_ID,
            TEST_VVB_ACCOUNT_ID);
  }

  @Test
  void testRequestOwnerNotificationsSentForNonIssuanceRequestTypes() {
    var testRequest = createTestRequest();
    testRequest.setRequestType(new RequestType(DESIGN_CERTIFICATION_RENEWAL_PROJECT));

    when(requestStatusNotificationRepository.findNotificationsForTransition(
            anyInt(), anyInt(), anyInt()))
        .thenReturn(List.of(createTestRequestStatusNotification()));

    when(accountConfiguration.getArmTeamAccountId()).thenReturn(TEST_ARM_TEAM_ACCOUNT_ID);
    when(accountConfiguration.getRegistryTeamAccountId()).thenReturn(TEST_REGISTRY_TEAM_ACCOUNT_ID);

    var vvbAccount = mock(Account.class);
    when(assignmentFinder.hasAssignmentOfType(testRequest.getId(), AUDIT_ASSIGNMENT_TYPE_ID))
        .thenReturn(true);

    when(assignmentFinder.findAssignedAccountsByRequestIdAndAssignmentTypeId(
            testRequest.getId(), AUDIT_ASSIGNMENT_TYPE_ID))
        .thenReturn(List.of(vvbAccount));
    when(vvbAccount.getId()).thenReturn(TEST_VVB_ACCOUNT_ID);

    when(accountFinder.forceGetAccountById(TEST_PD_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_PD_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_ARM_TEAM_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_ARM_TEAM_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_REGISTRY_TEAM_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_REGISTRY_TEAM_ACCOUNT_ID));
    when(accountFinder.forceGetAccountById(TEST_VVB_ACCOUNT_ID))
        .thenReturn(createTestAccount(TEST_VVB_ACCOUNT_ID));

    requestNotificationService.sendRequestNotification(
        testRequest, TEST_PREVIOUS_REQUEST_STATUS_ID);

    verify(notificationService, times(5))
        .sendAccountNotification(
            templateCaptor.capture(), requestArgumentCaptor.capture(), accountCaptor.capture());

    assertThat(requestArgumentCaptor.getValue().getId()).isEqualTo(TEST_REQUEST_ID);
    assertThat(templateCaptor.getAllValues().stream().map(NotificationTemplate::getName))
        .containsExactly(
            "pd_template",
            "arm_template",
            "registry_template",
            "vvb_template",
            "assignment_type_0_template");
    assertThat(accountCaptor.getAllValues().stream().map(Account::getId))
        .containsExactly(
            TEST_PD_ACCOUNT_ID,
            TEST_ARM_TEAM_ACCOUNT_ID,
            TEST_REGISTRY_TEAM_ACCOUNT_ID,
            TEST_VVB_ACCOUNT_ID,
            TEST_VVB_ACCOUNT_ID);
  }

  private RequestStatusNotification createTestRequestStatusNotification() {
    final RequestStatusNotification notification = new RequestStatusNotification();
    notification.setRequestOwnerTemplate(createTestTemplate("pd_template"));
    notification.setArmTeamTemplate(createTestTemplate("arm_template"));
    notification.setRegistryTeamTemplate(createTestTemplate("registry_template"));
    notification.setVvbTemplate(createTestTemplate("vvb_template"));
    notification.setAssigneeNotifications(
        List.of(createAssigneeNotification(0), createAssigneeNotification(1)));
    return notification;
  }

  private NotificationTemplate createTestTemplate(String name) {
    final NotificationTemplate template = new NotificationTemplate();
    template.setName(name);
    template.setVersion(TEST_VERSION);
    return template;
  }

  private RequestStatusAssigneeNotification createAssigneeNotification(Integer assignmentTypeId) {
    final RequestStatusAssigneeNotification assigneeNotification =
        new RequestStatusAssigneeNotification();
    assigneeNotification.setAssignmentTypeId(assignmentTypeId);
    assigneeNotification.setAssigneeTemplate(
        createTestTemplate(String.format("assignment_type_%d_template", assignmentTypeId)));
    return assigneeNotification;
  }

  private Request createTestRequest() {
    final Project project = new Project();
    project.setAccountId(TEST_PD_ACCOUNT_ID);
    return Request.builder()
        .id(TEST_REQUEST_ID)
        .project(project)
        .requestType(new RequestType(PRELIMINARY_REVIEW_ID))
        .requestRounds(new ArrayList<>())
        .currentRequestStatus(new RequestStatus(TEST_CURRENT_REQUEST_STATUS_ID))
        .build();
  }

  private Account createTestAccount(Integer accountId) {
    final Account account = new Account();
    account.setId(accountId);
    return account;
  }
}
