package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.ConversationStatus;

@Builder
@Getter
public class RequestConversationDTO {

  private final Integer id;
  private final Integer requestId;
  private final ConversationStatus status;
  private final Integer unreadMessages;
}
