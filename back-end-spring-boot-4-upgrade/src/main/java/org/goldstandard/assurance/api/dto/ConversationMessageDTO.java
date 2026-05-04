package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class ConversationMessageDTO {

  private final Integer id;
  private final Integer conversationId;
  private final String content;
  private final String senderUsername;
  private final String senderAccountType;
  private final ZonedDateTime sentTimestamp;
  private final Collection<String> readByAccountTypes;
  private final Collection<String> readByUsers;
}
