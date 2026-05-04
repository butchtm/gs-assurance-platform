package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.view.UnreadMessagesView;
import org.goldstandard.assurance.api.fixtures.AccountMother;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.service.conversation.UnreadMessageFinder;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduledConversationNotificationServiceTest {

  @Mock private NotificationService notificationService;
  @Mock private UnreadMessageFinder unreadMessageFinder;
  @Mock private AccountConversationsGrouper accountGrouper;
  @Mock private NotificationModelFactory modelFactory;

  private ScheduledConversationNotificationService service;
  private User systemUser;

  @Captor ArgumentCaptor<Collection<Integer>> captor;

  @BeforeEach
  void setUp() {
    service =
        new ScheduledConversationNotificationService(
            notificationService, unreadMessageFinder, accountGrouper, modelFactory);
    systemUser = User.builder().userName("System").build();
  }

  @Test
  void shouldSendNotificationsForUnreadMessages() {
    var account = AccountMother.withIdAndName(1, "Test Account");
    var request = RequestMother.withIdAndName(10, "Test Request");
    var unreadMessages = List.of(buildUnreadMessage(1, 1, 10));
    var unreadNotificationData = List.of(new UnreadNotificationData(account, List.of(request)));
    var notificationModel = buildNotificationModel(account);

    when(unreadMessageFinder.getUnreadByAllAccountUsersMessages(any(), any()))
        .thenReturn(unreadMessages);
    when(accountGrouper.groupUnreadMessagesByAccount(unreadMessages))
        .thenReturn(unreadNotificationData);
    when(modelFactory.toNotificationModelList(unreadNotificationData))
        .thenReturn(List.of(notificationModel));

    service.sendConversationNotifications(systemUser);

    verify(notificationService)
        .sendAccountNotification(
            ScheduledConversationNotificationService.TEMPLATE_KEY,
            notificationModel,
            account,
            systemUser);
  }

  @Test
  void shouldSendMultipleNotificationsForMultipleAccounts() {
    var account1 = AccountMother.withIdAndName(1, "Account 1");
    var account2 = AccountMother.withIdAndName(2, "Account 2");
    var request1 = RequestMother.withIdAndName(10, "Request 1");
    var request2 = RequestMother.withIdAndName(20, "Request 2");
    var unreadMessages = List.of(buildUnreadMessage(1, 1, 10), buildUnreadMessage(2, 2, 20));
    var unreadNotificationData =
        List.of(
            new UnreadNotificationData(account1, List.of(request1)),
            new UnreadNotificationData(account2, List.of(request2)));
    var notificationModel1 = buildNotificationModel(account1);
    var notificationModel2 = buildNotificationModel(account2);

    when(unreadMessageFinder.getUnreadByAllAccountUsersMessages(any(), any()))
        .thenReturn(unreadMessages);
    when(accountGrouper.groupUnreadMessagesByAccount(unreadMessages))
        .thenReturn(unreadNotificationData);
    when(modelFactory.toNotificationModelList(unreadNotificationData))
        .thenReturn(List.of(notificationModel1, notificationModel2));

    service.sendConversationNotifications(systemUser);

    verify(notificationService, times(2))
        .sendAccountNotification(
            eq(ScheduledConversationNotificationService.TEMPLATE_KEY),
            any(NotificationModel.class),
            any(Account.class),
            eq(systemUser));
  }

  @Test
  void shouldNotSendNotificationsWhenNoUnreadMessages() {
    when(unreadMessageFinder.getUnreadByAllAccountUsersMessages(any(), any()))
        .thenReturn(List.of());
    when(accountGrouper.groupUnreadMessagesByAccount(any())).thenReturn(List.of());
    when(modelFactory.toNotificationModelList(any())).thenReturn(List.of());

    service.sendConversationNotifications(systemUser);

    verifyNoInteractions(notificationService);
  }

  @Test
  void shouldExcludeSpecificAssignmentTypes() {
    when(unreadMessageFinder.getUnreadByAllAccountUsersMessages(any(), any()))
        .thenReturn(List.of());
    when(accountGrouper.groupUnreadMessagesByAccount(any())).thenReturn(List.of());
    when(modelFactory.toNotificationModelList(any())).thenReturn(List.of());

    service.sendConversationNotifications(systemUser);

    verify(unreadMessageFinder).getUnreadByAllAccountUsersMessages(captor.capture(), any());

    var excludedAssignmentTypes = captor.getValue();
    assertThat(excludedAssignmentTypes).containsExactlyInAnyOrder(4, 3, 5);
  }

  private UnreadMessagesView buildUnreadMessage(
      Integer userId, Integer accountId, Integer requestId) {
    return UnreadMessagesView.builder()
        .userId(userId)
        .accountId(accountId)
        .requestId(requestId)
        .unread(5)
        .build();
  }

  private NotificationModel buildNotificationModel(Account account) {
    var unreadModel =
        NotificationModel.UnreadMessageModel.builder()
            .recipient(account)
            .requests(List.of())
            .build();
    return NotificationModel.builder().unread(unreadModel).build();
  }
}
