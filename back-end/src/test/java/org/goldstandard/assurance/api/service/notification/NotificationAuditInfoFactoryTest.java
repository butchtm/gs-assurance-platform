package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.entity.notification.Notification;
import org.junit.jupiter.api.Test;

class NotificationAuditInfoFactoryTest {

  private final NotificationAuditInfoFactory factory = new NotificationAuditInfoFactory();

  @Test
  void shouldPopulateRecipientIdIfPresent() {
    var recipientId = 1;
    var notification = Notification.builder().recipientId(recipientId).build();

    var auditInfo = factory.build(notification, null);

    assertThat(auditInfo).hasSize(1).containsEntry("recipientId", recipientId);
  }

  @Test
  void shouldPopulateRecipientTypeIfPresent() {
    var recipientType = "recipient-type";
    var notification = Notification.builder().recipientType(recipientType).build();

    var auditInfo = factory.build(notification, null);

    assertThat(auditInfo).hasSize(1).containsEntry("recipientType", recipientType);
  }

  @Test
  void shouldPopulateNotificationTypeIfPresent() {
    var notificationType = "notification-type";
    var notification = Notification.builder().build();

    var auditInfo = factory.build(notification, notificationType);

    assertThat(auditInfo).hasSize(1).containsEntry("notificationType", notificationType);
  }

  @Test
  void shouldPopulateNotificationIdIfPresent() {
    var notificationId = 2;
    var notification = Notification.builder().id(notificationId).build();

    var auditInfo = factory.build(notification, null);

    assertThat(auditInfo).hasSize(1).containsEntry("notificationId", notificationId);
  }
}
