package org.goldstandard.assurance.api.service.notification;

import java.util.concurrent.CompletableFuture;
import org.goldstandard.assurance.api.configuration.NotificationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;

@Profile("!local")
@Component
public class SesEmailSender implements EmailSender {

  private final SesV2AsyncClient asyncClient;
  private final NotificationConfiguration configuration;

  @Autowired
  public SesEmailSender(SesV2AsyncClient asyncClient, NotificationConfiguration configuration) {
    this.asyncClient = asyncClient;
    this.configuration = configuration;
  }

  @Override
  public CompletableFuture<String> sendEmailAsync(String toEmailAddress, RenderedMessage message) {
    var request = toSendEmailRequest(toEmailAddress, message);
    var future = asyncClient.sendEmail(request);
    return future.thenApply(SendEmailResponse::messageId);
  }

  private SendEmailRequest toSendEmailRequest(String toEmailAddress, RenderedMessage message) {
    return SendEmailRequest.builder()
        .destination(toDestination(toEmailAddress))
        .content(toEmailContent(message))
        .fromEmailAddress(configuration.getNotificationSource())
        .build();
  }

  private static Destination toDestination(String emailAddress) {
    return Destination.builder().toAddresses(emailAddress).build();
  }

  private static EmailContent toEmailContent(RenderedMessage renderedMessage) {
    return EmailContent.builder().simple(toMessage(renderedMessage)).build();
  }

  private static Message toMessage(RenderedMessage renderedMessage) {
    return Message.builder()
        .subject(toContent(renderedMessage.subject()))
        .body(toBody(renderedMessage))
        .build();
  }

  private static Body toBody(RenderedMessage renderedMessage) {
    return Body.builder()
        .html(toContent(renderedMessage.htmlBody()))
        .text(toContent(renderedMessage.textBody()))
        .build();
  }

  private static Content toContent(String content) {
    return Content.builder().data(content).build();
  }
}
