package org.goldstandard.assurance.api.repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import org.goldstandard.assurance.api.entity.ConversationMessage;
import org.goldstandard.assurance.api.projection.ConversationMessageId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConversationMessageRepository
    extends JpaRepository<ConversationMessage, Integer>,
        PagingAndSortingRepository<ConversationMessage, Integer> {

  Page<ConversationMessage> findAllByConversationId(Integer conversationId, Pageable pageable);

  Collection<ConversationMessage> findAllByConversationId(Integer conversationId);

  Collection<ConversationMessageId> findAllIdsByConversationId(Integer conversationId);

  Collection<ConversationMessageId> findAllIdsByConversationIdAndSentByUserId(
      Integer conversationId, Integer userId);

  void deleteAllByConversationId(Integer conversationId);

  int countByConversationIdAndSentAtAfter(Integer conversationId, ZonedDateTime sentAt);
}
