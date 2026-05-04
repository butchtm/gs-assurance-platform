package org.goldstandard.assurance.api.service.conversation;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.ConversationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationFinder {

  private final ConversationRepository repository;

  public Optional<Conversation> findByRequestId(Integer requestId) {
    return repository.findByRequestId(requestId);
  }

  public Conversation forceFindById(Integer conversationId) {
    return repository
        .findById(conversationId)
        .orElseThrow(
            () -> new NotFoundException("Conversation with id %d not found", conversationId));
  }
}
