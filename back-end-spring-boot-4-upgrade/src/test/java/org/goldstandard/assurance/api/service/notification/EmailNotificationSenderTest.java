package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.exception.NotificationException;
import org.goldstandard.assurance.api.fixtures.NotificationParamsMother;
import org.goldstandard.assurance.api.service.notification.model.NotificationModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailNotificationSenderTest {

  private static final String MESSAGE_ID = "test-message -id";
  private static final RenderedMessage RENDERED_MESSAGE =
      new RenderedMessage("Test subject", "Test html body", "Test text body");

  @InjectMocks EmailNotificationSender notificationSender;

  @Mock RendererService rendererService;

  @Mock Clock clock;

  @Mock AsyncSendEmailHandlerFactory handlerFactory;

  @Mock AsyncSendEmailHandler handler;

  @Mock EmailSender emailSender;

  @Captor ArgumentCaptor<Notification> notificationCaptor;

  @Captor ArgumentCaptor<Throwable> errorCaptor;

  @Test
  void shouldPopulateRecipientNameOnModelBeforeSending() {
    mockTimeNow();
    var params = NotificationParamsMother.build();
    when(rendererService.renderMessage(eq(params.getTemplate()), any(NotificationModel.class)))
        .thenReturn(RENDERED_MESSAGE);
    when(handlerFactory.build(params)).thenReturn(handler);
    when(emailSender.sendEmailAsync(params.getRecipientAddress(), RENDERED_MESSAGE))
        .thenReturn(CompletableFuture.completedFuture(MESSAGE_ID));

    notificationSender.sendNotification(params);

    var modelCaptor = ArgumentCaptor.forClass(NotificationModel.class);
    verify(rendererService).renderMessage(eq(params.getTemplate()), modelCaptor.capture());
    var model = modelCaptor.getValue();
    assertThat(model.getRecipient()).isEqualTo(params.getRecipient().getName());
  }

  @Test
  void shouldSendEmailNotification() {
    var now = mockTimeNow();
    var params = NotificationParamsMother.build();
    when(rendererService.renderMessage(eq(params.getTemplate()), any(NotificationModel.class)))
        .thenReturn(RENDERED_MESSAGE);
    when(handlerFactory.build(params)).thenReturn(handler);
    when(emailSender.sendEmailAsync(params.getRecipientAddress(), RENDERED_MESSAGE))
        .thenReturn(CompletableFuture.completedFuture(MESSAGE_ID));

    notificationSender.sendNotification(params);

    verify(handler).accept(notificationCaptor.capture(), eq(null));
    var notification = notificationCaptor.getValue();
    assertThat(notification.getId()).isNull();
    var recipient = params.getRecipient();
    assertThat(notification.getRecipientId()).isEqualTo(recipient.getId());
    assertThat(notification.getRecipientType()).isEqualTo(recipient.getType());
    assertThat(notification.getAddressees()).containsExactly(params.getRecipientAddress());
    assertThat(notification.getSubject()).isEqualTo(RENDERED_MESSAGE.subject());
    assertThat(notification.getHtmlBody()).isEqualTo(RENDERED_MESSAGE.htmlBody());
    assertThat(notification.getTextBody()).isEqualTo(RENDERED_MESSAGE.textBody());
    assertThat(notification.getCreatedTimestamp()).isEqualTo(now);
    assertThat(notification.getMessagingReference()).isEqualTo(MESSAGE_ID);
  }

  @Test
  void shouldReturnErrorIfSendingEmailFails() {
    mockTimeNow();
    var params = NotificationParamsMother.build();
    when(rendererService.renderMessage(eq(params.getTemplate()), any(NotificationModel.class)))
        .thenReturn(RENDERED_MESSAGE);
    when(handlerFactory.build(params)).thenReturn(handler);
    var expectedError = new NotificationException("boom");
    when(emailSender.sendEmailAsync(params.getRecipientAddress(), RENDERED_MESSAGE))
        .thenReturn(CompletableFuture.failedFuture(expectedError));

    notificationSender.sendNotification(params);

    verify(handler).accept(eq(null), errorCaptor.capture());
    assertThat(errorCaptor.getValue())
        .isInstanceOf(CompletionException.class)
        .hasCause(expectedError);
  }

  private ZonedDateTime mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }
}
