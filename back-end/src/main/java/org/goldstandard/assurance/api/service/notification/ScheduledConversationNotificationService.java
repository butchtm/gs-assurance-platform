package org.goldstandard.assurance.api.service.notification;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.COLLECT_FEE_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.STAKEHOLDER_CONSULTATION_ASSIGNMENT_TYPE_ID;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.conversation.UnreadMessageFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledConversationNotificationService {

  public static final TemplateKey TEMPLATE_KEY = new TemplateKey("UNREAD_MESSAGES", "v0.0.1");

  private final NotificationService notificationService;
  private final UnreadMessageFinder unreadMessageFinder;
  private final AccountConversationsGrouper accountGrouper;
  private final NotificationModelFactory modelFactory;
  private final Collection<Integer> assignmentTypesToExclude;
  private final Collection<Integer> accountTypesToExclude;

  @Autowired
  public ScheduledConversationNotificationService(
      NotificationService notificationService,
      UnreadMessageFinder unreadMessageFinder,
      AccountConversationsGrouper accountGrouper,
      NotificationModelFactory modelFactory) {
    this(
        notificationService,
        unreadMessageFinder,
        accountGrouper,
        modelFactory,
        buildAssignmentTypesToExclude(),
        List.of(REVIEWER_ACCOUNT_TYPE_ID));
  }

  public void sendConversationNotifications(User authenticatedUser) {
    var unreadMessages =
        unreadMessageFinder.getUnreadByAllAccountUsersMessages(
            assignmentTypesToExclude, accountTypesToExclude);
    var unreadNotifications = accountGrouper.groupUnreadMessagesByAccount(unreadMessages);
    var notificationModels = modelFactory.toNotificationModelList(unreadNotifications);
    notificationModels.forEach(
        model ->
            notificationService.sendAccountNotification(
                TEMPLATE_KEY, model, model.getUnread().getRecipient(), authenticatedUser));
  }

  private static Collection<Integer> buildAssignmentTypesToExclude() {
    return List.of(
        COMPLETENESS_ASSIGNMENT_TYPE_ID,
        COLLECT_FEE_ASSIGNMENT_TYPE_ID,
        STAKEHOLDER_CONSULTATION_ASSIGNMENT_TYPE_ID);
  }
}
