package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationDTO {
  private final Integer id;
  private final String recipientType;
  private final Integer recipientId;
  private final List<String> addressees;
  private final String subject;
  private final String htmlBody;
  private final String textBody;
  private final ZonedDateTime createdTimestamp;
  private final String messagingReference;
}
