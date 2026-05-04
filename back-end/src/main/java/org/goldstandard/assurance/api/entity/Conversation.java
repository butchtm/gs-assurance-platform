package org.goldstandard.assurance.api.entity;

import static org.goldstandard.assurance.api.entity.ConversationStatus.CONVERSATION_CLOSED_STATUS_ID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Entity
public class Conversation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Integer id;

  private final Integer requestId;

  @With @ManyToOne private final ConversationStatus status;

  private final ZonedDateTime statusAppliedAt;

  public boolean hasOpenStatus() {
    return hasStatus(ConversationStatus::isOpen);
  }

  public boolean hasClosedStatus() {
    return hasStatus(ConversationStatus::isClosed);
  }

  public Conversation close() {
    return withStatus(new ConversationStatus(CONVERSATION_CLOSED_STATUS_ID));
  }

  private boolean hasStatus(Function<ConversationStatus, Boolean> function) {
    return Optional.ofNullable(status).map(function).orElse(false);
  }
}
