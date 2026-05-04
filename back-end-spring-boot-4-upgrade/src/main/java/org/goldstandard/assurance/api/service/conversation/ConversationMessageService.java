package org.goldstandard.assurance.api.service.conversation;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.MessageCreateDTO;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationMessageService {

  private final ConversationMessageCreator messageCreator;
  private final ConversationMessageFinder messageFinder;
  private final ConversationMessageReceiptCreator messageReceiptCreator;
  private final AuthenticatedUserSupplier userSupplier;
  private final UnreadMessageFinder unreadMessageFinder;

  public Integer saveMessage(Integer requestId, Integer conversationId, MessageCreateDTO message) {
    return messageCreator.create(requestId, conversationId, message.getMessage());
  }

  public Page<ConversationMessage> getMessages(Integer conversationId, Pageable pageable) {
    return messageFinder.getMessages(conversationId, pageable);
  }

  public void markReadConversation(Integer conversationId) {
    var user = userSupplier.forceGetAuthenticatedUser();
    var unreadMessages = unreadMessageFinder.getUnreadMessagesForUser(conversationId, user.getId());
    messageReceiptCreator.createMessageReceipt(unreadMessages, user.getId());
  }

  public ConversationMessage getMessage(Integer messageId) {
    return messageFinder.getMessage(messageId);
  }
}
