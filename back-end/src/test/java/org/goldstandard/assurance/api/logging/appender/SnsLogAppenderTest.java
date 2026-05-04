package org.goldstandard.assurance.api.logging.appender;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@ExtendWith(MockitoExtension.class)
class SnsLogAppenderTest {

  private static final String TOPIC_ARN = "arn:aws:sns:eu-central-1:123456789012:errors-topic";
  private static final String ENVIRONMENT = "TEST";
  private static final String USERNAME = "mruocco";
  private static final String USER_AGENT = "Test User Agent";
  private static final String CORRELATION_ID = "123";
  private static final int MAX_MESSAGE_LENGTH = 140;

  private SnsLogAppender appender;

  @Mock private SnsClient snsClient;

  @Captor private ArgumentCaptor<PublishRequest> captor;

  @BeforeEach
  void setup() {
    appender =
        new SnsLogAppender() {
          @Override
          protected SnsClient buildSnsClient() {
            return snsClient;
          }
        };
    appender.setSnsRegion("eu-central-1");
    appender.setMaxMessageLength(MAX_MESSAGE_LENGTH);
    appender.setEnvironment(ENVIRONMENT);
  }

  @Test
  void shouldNotSendMessageIfTopicArnIsNotSet() {
    appender.start();
    var event = mock(ILoggingEvent.class);

    appender.append(event);

    verifyNoInteractions(snsClient);
  }

  @Test
  void shouldSendMessageIfLogLevelIsErrorAndTopicArnIsPresent() {
    appender.setTopicArn(TOPIC_ARN);
    appender.start();
    var event = buildEvent();
    appender.append(event);

    verify(snsClient).publish(captor.capture());

    var request = captor.getValue();
    assertThat(request.topicArn()).isEqualTo(TOPIC_ARN);
    var expectedSubject =
        String.format("Assurance Platform %s - Environment: %s", event.getLevel(), ENVIRONMENT);
    assertThat(request.subject()).isEqualTo(expectedSubject);
    assertThat(request.message()).contains(String.format("Correlation-Id: %s", CORRELATION_ID));
    assertThat(request.message()).contains(String.format("Username: %s", USERNAME));
    assertThat(request.message()).contains(String.format("User-Agent: %s", USER_AGENT));
    assertThat(request.message()).contains(event.getFormattedMessage());
  }

  @Test
  void shouldTruncatePublishedMessageIfGreaterThanMaxLength() {
    appender.setTopicArn(TOPIC_ARN);
    appender.start();
    var event = buildEvent();
    when(event.getFormattedMessage())
        .thenReturn("Something went wrong and this message is too long");

    appender.append(event);

    verify(snsClient).publish(captor.capture());
    var request = captor.getValue();
    assertThat(request.message()).contains(String.format("Correlation-Id: %s", CORRELATION_ID));
    assertThat(request.message()).contains(String.format("Username: %s", USERNAME));
    assertThat(request.message()).contains(String.format("User-Agent: %s", USER_AGENT));
    assertThat(request.message()).contains("Something went wrong and thi...[truncated]");
  }

  private ILoggingEvent buildEvent() {
    var mdcProperties =
        Map.of("username", USERNAME, "x-correlation-id", CORRELATION_ID, "user-agent", USER_AGENT);
    var event = mock(ILoggingEvent.class);
    when(event.getLevel()).thenReturn(Level.ERROR);
    when(event.getLoggerName()).thenReturn("TestLogger");
    when(event.getMDCPropertyMap()).thenReturn(mdcProperties);
    when(event.getFormattedMessage()).thenReturn("Something went wrong");
    return event;
  }
}
