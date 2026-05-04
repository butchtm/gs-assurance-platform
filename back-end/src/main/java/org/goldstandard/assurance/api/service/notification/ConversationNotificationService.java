package org.goldstandard.assurance.api.service.notification;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_ARM_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.GS_FINANCE_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.TAC_ACCOUNT_TYPE_ID;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.service.AssociatedAccountsService;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationNotificationService {

  public static final TemplateKey TEMPLATE_KEY =
      new TemplateKey("REQUEST_MESSAGE_RECEIVED", "v0.0.1");

  private final ConversationMessageNotificationPolicy notificationPolicy;
  private final RequestFinder requestFinder;
  private final AssociatedAccountsService associatedAccountsService;
  private final NotificationService notificationService;
  private final Collection<Integer> accountTypesToExclude;

  @Autowired
  public ConversationNotificationService(
      ConversationMessageNotificationPolicy notificationPolicy,
      RequestFinder requestFinder,
      AssociatedAccountsService associatedAccountsService,
      NotificationService notificationService) {
    this(
        notificationPolicy,
        requestFinder,
        associatedAccountsService,
        notificationService,
        buildAccountTypesToExclude());
  }

  public void sendNotificationIfRequired(Integer requestId, Integer conversationId) {
    if (notificationPolicy.isFirstMessageAfterOpened(conversationId)) {
      sendNotification(requestId);
    }
  }

  private void sendNotification(Integer requestId) throws NotFoundException, DataException {
    var request = requestFinder.getRequestById(requestId);
    var accounts =
        associatedAccountsService.findAccountsAssociatedWithRequest(request, accountTypesToExclude);
    notificationService.sendAccountNotifications(TEMPLATE_KEY, request, accounts);
  }

  private static Collection<Integer> buildAccountTypesToExclude() {
    return List.of(
        GS_ARM_ACCOUNT_TYPE_ID,
        GS_ACCOUNT_TYPE_ID,
        REVIEWER_ACCOUNT_TYPE_ID,
        GS_FINANCE_ACCOUNT_TYPE_ID,
        TAC_ACCOUNT_TYPE_ID);
  }
}
