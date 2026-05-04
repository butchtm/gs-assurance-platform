package org.goldstandard.assurance.api.repository;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

  Optional<Conversation> findByRequestId(Integer requestId);

  @Query("SELECT c.statusAppliedAt FROM Conversation c WHERE c.id = :conversationId")
  Optional<ZonedDateTime> findStatusAppliedAtById(@Param("conversationId") Integer conversationId);
}
