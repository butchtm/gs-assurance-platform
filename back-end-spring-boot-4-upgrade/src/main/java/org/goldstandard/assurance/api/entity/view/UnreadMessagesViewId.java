package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Embeddable
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@Getter
public class UnreadMessagesViewId implements Serializable {

  @Column(name = "user_id", insertable = false, updatable = false)
  private final Integer userId;

  @Column(name = "conversation_id", insertable = false, updatable = false)
  private final Integer conversationId;
}
