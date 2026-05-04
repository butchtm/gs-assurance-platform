package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.ConversationStatus;

@Builder
@RequiredArgsConstructor
@Getter
public class ConversationDTO {

  private final Integer id;
  private final Integer requestId;
  private final ConversationStatus status;
}
