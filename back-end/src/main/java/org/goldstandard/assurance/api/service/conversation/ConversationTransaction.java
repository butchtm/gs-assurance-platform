package org.goldstandard.assurance.api.service.conversation;

import jakarta.transaction.Transactional;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.entity.ConversationMessageReceipt;
import org.goldstandard.assurance.api.repository.ConversationMessageReceiptRepository;
import org.goldstandard.assurance.api.repository.ConversationMessageRepository;
import org.goldstandard.assurance.api.repository.ConversationRepository;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConversationTransaction {

  private final ConversationRepository repository;
  private final ConversationMessageRepository messageRepository;
  private final ConversationMessageReceiptRepository messageReceiptRepository;

  @Transactional
  public Integer create(Conversation conversation) {
    var savedConversation = repository.save(conversation);
    return savedConversation.getId();
  }

  @Transactional
  public void update(Conversation conversation) {
    repository.save(conversation);
  }

  @Transactional
  public Integer create(ConversationMessage message) {
    var savedMessage = messageRepository.save(message);
    return savedMessage.getId();
  }

  @Transactional
  public void create(Collection<ConversationMessageReceipt> receipts) {
    messageReceiptRepository.saveAll(receipts);
  }
}
