package org.goldstandard.assurance.api.service.conversation;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.entity.ConversationMessageReceipt;
import org.goldstandard.assurance.api.entity.User;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConversationMessageReceiptFactory {

  private final Clock clock;

  public Collection<ConversationMessageReceipt> buildMessageReceipts(
      Collection<ConversationMessage> messages, Integer userId) {
    return messages.stream().map(m -> buildMessageReceipt(m, userId)).toList();
  }

  public ConversationMessageReceipt buildMessageReceipt(
      ConversationMessage message, Integer userId) {
    var now = ZonedDateTime.now(clock);
    return ConversationMessageReceipt.builder()
        .message(message)
        .readByUser(new User(userId))
        .readAt(now)
        .build();
  }
}
