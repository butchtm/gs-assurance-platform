package org.goldstandard.assurance.api.service.conversation;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConversationMessageFactory {

  private final AuthenticatedUserSupplier userSupplier;
  private final Clock clock;

  public ConversationMessage buildMessage(Integer conversationId, String content) {
    return ConversationMessage.builder()
        .conversationId(conversationId)
        .content(content)
        .sentByUser(userSupplier.forceGetAuthenticatedUser())
        .sentAt(ZonedDateTime.now(clock))
        .build();
  }
}
