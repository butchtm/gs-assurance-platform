package org.goldstandard.assurance.api.fixtures;

import org.goldstandard.assurance.api.service.notification.NotificationRecipient;
import org.goldstandard.assurance.api.service.notification.NotificationRecipientDefault;
import org.goldstandard.assurance.api.service.notification.NotificationRecipientDefault.NotificationRecipientDefaultBuilder;

public class NotificationRecipientMother {

  public static NotificationRecipient build() {
    return build(1);
  }

  public static NotificationRecipient build(int id) {
    return builder(id).build();
  }

  private static NotificationRecipientDefaultBuilder builder(int id) {
    return NotificationRecipientDefault.builder()
        .type("ACCOUNT")
        .id(id)
        .name(String.format("Test Recipient %d", id))
        .emailAddress(String.format("test-recipient-%d@email.com", id));
  }
}
