package org.goldstandard.assurance.api.service.notification;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.notification.Notification;
import org.goldstandard.assurance.api.entity.notification.NotificationTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncSendEmailHandlerTest {

  @InjectMocks AsyncSendEmailHandler handler;

  @Mock SaveNotificationTransaction transaction;

  @Mock NotificationTemplate template;

  @Mock User authenticatedUser;

  @Test
  void shouldLogError() throws Exception {
    Throwable error = new Exception("some error");

    String output =
        tapSystemOut(
            () -> {
              handler.accept(null, error);
            });

    assertThat(output).contains(error.getMessage());
  }

  @Test
  void shouldSaveNotificationIfSent() {
    var notification = new Notification();

    handler.accept(notification, null);

    verify(transaction).recordNotificationSent(notification, template, authenticatedUser);
  }
}
