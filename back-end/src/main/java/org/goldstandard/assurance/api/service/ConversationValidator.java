package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.exception.ConflictException;
import org.goldstandard.assurance.api.service.conversation.ConversationFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationValidator {

  private final ConversationFinder finder;

  public void validateCanSendMessage(Integer conversationId) {
    var conversation = finder.forceFindById(conversationId);
    if (conversation.hasClosedStatus()) {
      throw new ConflictException(
          "message cannot be sent because conversation %d is closed", conversationId);
    }
  }
}
