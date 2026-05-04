package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Entity(name = "v_unread_messages")
@IdClass(UnreadMessagesViewId.class)
public class UnreadMessagesView {

  @Id private final Integer userId;
  private final Integer accountId;
  private final Integer accountTypeId;
  private final Integer requestId;
  @Id private final Integer conversationId;
  private final Integer unread;
  private final Integer assignmentTypeId;

  public boolean hasAny() {
    return Optional.ofNullable(unread).map(value -> value > 0).orElse(false);
  }
}
