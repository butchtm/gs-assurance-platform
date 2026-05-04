package org.goldstandard.assurance.api.service.notification;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.service.AssociatedAccountsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentNotificationService {

  public static final TemplateKey TEMPLATE_KEY = new TemplateKey("COMMENT_RECEIVED", "v0.0.3");

  private final AssociatedAccountsService associatedAccountsService;
  private final NotificationService notificationService;

  public void sendCommentNotification(Comment comment, Request request)
      throws NotFoundException, DataException {
    var accountTypesToExclude = toAccountTypesToExclude(comment);
    var accounts =
        associatedAccountsService.findAccountsAssociatedWithRequest(request, accountTypesToExclude);
    notificationService.sendAccountNotifications(TEMPLATE_KEY, request, accounts);
  }

  private static Collection<Integer> toAccountTypesToExclude(Comment comment) {
    if (comment.isTacComment()) {
      return List.of(PROJECT_DEVELOPER_TYPE_ID, VVB_ACCOUNT_TYPE_ID);
    }
    return Collections.emptyList();
  }
}
