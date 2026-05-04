package org.goldstandard.assurance.api.service.notification;

import java.util.Objects;
import java.util.function.BiConsumer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;

@Builder
@Slf4j
public class AsyncSendEmailHandler implements BiConsumer<Notification, Throwable> {

  private final SaveNotificationTransaction transaction;
  private final NotificationTemplate template;
  private final String recipientAddress;
  private final User authenticatedUser;

  @Override
  public void accept(Notification notification, Throwable error) {
    if (Objects.nonNull(notification)) {
      recordNotificationSent(notification);
      return;
    }
    log.error("failed to send email to {} {}", recipientAddress, error.getMessage(), error);
  }

  private void recordNotificationSent(Notification notification) {
    try {
      transaction.recordNotificationSent(notification, template, authenticatedUser);
    } catch (Exception e) {
      log.error("failed to record notification sent {}", notification.getId(), e);
    }
  }
}
