package org.goldstandard.assurance.api.service.conversation;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationCreator {

  private final ConversationFactory factory;
  private final ConversationTransaction transaction;

  public Integer create(Request request) {
    var conversation = factory.buildConversation(request);
    return transaction.create(conversation);
  }
}
