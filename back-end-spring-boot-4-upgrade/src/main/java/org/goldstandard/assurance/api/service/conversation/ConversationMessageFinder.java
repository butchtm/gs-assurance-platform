package org.goldstandard.assurance.api.service.conversation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.ConversationMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConversationMessageFinder {

  private final ConversationMessageRepository repository;

  public ConversationMessage getMessage(Integer messageId) {
    return repository
        .findById(messageId)
        .orElseThrow(() -> new NotFoundException("message with id %s not found", messageId));
  }

  public Page<ConversationMessage> getMessages(Integer conversationId, Pageable pageable) {
    return repository.findAllByConversationId(conversationId, pageable);
  }
}
