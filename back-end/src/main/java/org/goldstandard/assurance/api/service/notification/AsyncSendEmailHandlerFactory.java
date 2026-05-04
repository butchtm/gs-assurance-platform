package org.goldstandard.assurance.api.service.notification;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.notification.model.NotificationParams;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AsyncSendEmailHandlerFactory {

  private final SaveNotificationTransaction transaction;

  public AsyncSendEmailHandler build(NotificationParams params) {
    return AsyncSendEmailHandler.builder()
        .transaction(transaction)
        .template(params.getTemplate())
        .recipientAddress(params.getRecipientAddress())
        .authenticatedUser(params.getAuthenticatedUser().orElse(null))
        .build();
  }
}
