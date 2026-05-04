package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UnreadMessagesDTO {
  private final Integer requestId;
  private final Integer conversationId;
  private final Integer unreadCount;
}
