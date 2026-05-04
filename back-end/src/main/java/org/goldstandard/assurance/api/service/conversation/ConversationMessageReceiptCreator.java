package org.goldstandard.assurance.api.service.conversation;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationMessageReceiptCreator {

  private final ConversationMessageReceiptFactory factory;
  private final ConversationTransaction transaction;

  public void createMessageReceipt(Collection<ConversationMessage> messages, Integer userId) {
    var receipts = factory.buildMessageReceipts(messages, userId);
    transaction.create(receipts);
  }
}
