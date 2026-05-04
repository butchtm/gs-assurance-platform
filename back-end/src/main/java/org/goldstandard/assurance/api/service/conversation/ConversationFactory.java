package org.goldstandard.assurance.api.service.conversation;

import static org.goldstandard.assurance.api.entity.ConversationStatus.CONVERSATION_OPEN_STATUS_ID;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConversationFactory {

  private final Clock clock;

  public Conversation buildConversation(Request request) {
    return Conversation.builder()
        .requestId(request.getId())
        .status(new ConversationStatus(CONVERSATION_OPEN_STATUS_ID))
        .statusAppliedAt(ZonedDateTime.now(clock))
        .build();
  }
}
