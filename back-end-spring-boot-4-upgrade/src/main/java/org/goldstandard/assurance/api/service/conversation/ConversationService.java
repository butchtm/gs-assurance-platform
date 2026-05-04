package org.goldstandard.assurance.api.service.conversation;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationService {

  private final ConversationFinder finder;
  private final RequestFinder requestFinder;
  private final ConversationCreator creator;
  private final ConversationUpdater updater;

  public Integer openConversation(Integer requestId) {
    var request = requestFinder.getRequestById(requestId);
    return creator.create(request);
  }

  public void updateConversationStatus(
      Integer conversationId, ConversationStatus conversationStatus) {
    updater.updateConversationStatus(conversationId, conversationStatus);
  }

  public Conversation getConversation(Integer conversationId) {
    return finder.forceFindById(conversationId);
  }
}
