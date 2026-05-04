package org.goldstandard.assurance.api.service.notification;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.repository.ConversationMessageRepository;
import org.goldstandard.assurance.api.repository.ConversationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationMessageNotificationPolicy {

  private final ConversationRepository conversationRepository;
  private final ConversationMessageRepository messageRepository;

  public boolean isFirstMessageAfterOpened(Integer conversationId) {
    var openStatusAppliedAt = conversationRepository.findStatusAppliedAtById(conversationId);
    if (openStatusAppliedAt.isEmpty()) {
      return false;
    }
    var messageCountAfterLastOpened =
        messageRepository.countByConversationIdAndSentAtAfter(
            conversationId, openStatusAppliedAt.get());
    return messageCountAfterLastOpened <= 1;
  }
}
