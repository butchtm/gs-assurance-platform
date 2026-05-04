package org.goldstandard.assurance.api.service.notification;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.service.notification.model.NotificationParams;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationSender {

  private final RendererService rendererService;
  private final Clock clock;
  private final AsyncSendEmailHandlerFactory handlerFactory;
  private final EmailSender emailSender;

  public void sendNotification(NotificationParams params) {
    var message = toMessage(params);
    var notification = toNotification(params, message);
    var handler = handlerFactory.build(params);
    emailSender
        .sendEmailAsync(params.getRecipientAddress(), message)
        .thenApply(notification::withMessagingReference)
        .whenComplete(handler);
  }

  private RenderedMessage toMessage(NotificationParams params) {
    var recipient = params.getRecipient();
    var model = params.getModel().withRecipient(recipient.getName());
    return rendererService.renderMessage(params.getTemplate(), model);
  }

  private Notification toNotification(NotificationParams params, RenderedMessage message) {
    var recipient = params.getRecipient();
    return Notification.builder()
        .recipientId(recipient.getId())
        .recipientType(recipient.getType())
        .addressees(List.of(params.getRecipientAddress()))
        .subject(message.subject())
        .htmlBody(message.htmlBody())
        .textBody(message.textBody())
        .createdTimestamp(ZonedDateTime.now(clock))
        .build();
  }
}
