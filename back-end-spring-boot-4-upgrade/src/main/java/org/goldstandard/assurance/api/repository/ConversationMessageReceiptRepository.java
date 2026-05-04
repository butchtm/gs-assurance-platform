package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import org.goldstandard.assurance.api.entity.ConversationMessageReceipt;
import org.goldstandard.assurance.api.projection.ConversationMessageReceiptMessageId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMessageReceiptRepository
    extends JpaRepository<ConversationMessageReceipt, Integer> {

  Collection<ConversationMessageReceiptMessageId> findAllByMessageConversationIdAndReadByUserId(
      Integer conversationId, Integer userId);
}
