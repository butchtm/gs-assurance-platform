package org.goldstandard.assurance.api.service.notification;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationRecipientDefault implements NotificationRecipient {
  private final String type;
  private final Integer id;
  private final String name;
  private final String emailAddress;
}
