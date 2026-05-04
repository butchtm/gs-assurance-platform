package org.goldstandard.assurance.api.service.notification;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@RequiredArgsConstructor
@Slf4j
@Component
public class StubEmailSender implements EmailSender {

  private final AtomicInteger id;

  public StubEmailSender() {
    this(new AtomicInteger());
  }

  @Override
  public CompletableFuture<String> sendEmailAsync(String toEmailAddress, RenderedMessage message) {
    return CompletableFuture.completedFuture(sendEmail(toEmailAddress, message));
  }

  private String sendEmail(String toEmailAddress, RenderedMessage message) {
    String messageId = String.format("stub-message-id-%d", id.getAndIncrement());
    log.info(
        "would send email to {} with subject {} html body {} and text body {} for message id {}",
        toEmailAddress,
        message.subject(),
        message.htmlBody(),
        message.textBody(),
        messageId);
    return messageId;
  }
}
