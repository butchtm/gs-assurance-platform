package org.goldstandard.assurance.api.service.conversation;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.ConversationValidator;
import org.goldstandard.assurance.api.service.notification.ConversationNotificationService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationMessageCreator {

  private final ConversationMessageFactory messageFactory;
  private final ConversationTransaction transaction;
  private final ConversationNotificationService notificationService;
  private final ConversationValidator validator;

  public Integer create(Integer requestId, Integer conversationId, String content) {
    validator.validateCanSendMessage(conversationId);
    var message = messageFactory.buildMessage(conversationId, content);
    var id = transaction.create(message);
    notificationService.sendNotificationIfRequired(requestId, conversationId);
    return id;
  }
}
