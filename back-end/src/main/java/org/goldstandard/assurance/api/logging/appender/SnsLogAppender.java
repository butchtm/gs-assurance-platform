package org.goldstandard.assurance.api.logging.appender;

import static org.springframework.util.StringUtils.hasText;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import java.util.Objects;
import java.util.Optional;
import lombok.Setter;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

public class SnsLogAppender extends AppenderBase<ILoggingEvent> {

  @Setter private String topicArn;
  @Setter private String snsRegion;
  @Setter private int maxMessageLength;
  @Setter private String environment;

  private SnsClient snsClient;

  @Override
  public void start() {
    if (hasText(topicArn)) {
      snsClient = buildSnsClient();
    }
    super.start();
  }

  @Override
  public void stop() {
    if (snsClient != null) {
      snsClient.close();
    }
    super.stop();
  }

  @Override
  protected void append(ILoggingEvent event) {
    if (Objects.isNull(snsClient)) {
      return;
    }
    snsClient.publish(
        PublishRequest.builder()
            .topicArn(topicArn)
            .subject(
                String.format(
                    "Assurance Platform %s - Environment: %s", event.getLevel(), environment))
            .message(buildMessage(event))
            .build());
  }

  protected SnsClient buildSnsClient() {
    var credentialsProvider = DefaultCredentialsProvider.builder().build();
    return SnsClient.builder()
        .region(Region.of(snsRegion))
        .credentialsProvider(credentialsProvider)
        .build();
  }

  private String buildMessage(ILoggingEvent event) {
    var messageBuilder =
        new StringBuilder()
            .append(String.format("Environment: %s%n", environment))
            .append(String.format("Logger: %s%n", event.getLoggerName()))
            .append(String.format("Correlation-Id: %s%n", toCorrelationId(event)))
            .append(String.format("Username: %s%n", toUsername(event)))
            .append(String.format("User-Agent: %s%n", toUserAgent(event)))
            .append(String.format("Message: %s%n", event.getFormattedMessage()));

    var throwableProxy = event.getThrowableProxy();
    if (throwableProxy != null) {
      messageBuilder.append(
          String.format("%nStacktrace: %s%n", ThrowableProxyUtil.asString(throwableProxy)));
    }
    var message = messageBuilder.toString();
    if (message.length() > maxMessageLength) {
      message = message.substring(0, maxMessageLength) + "...[truncated]";
    }
    return message;
  }

  private String toUsername(ILoggingEvent event) {
    return extractMdcProperty(event, "username");
  }

  private String toCorrelationId(ILoggingEvent event) {
    return extractMdcProperty(event, "x-correlation-id");
  }

  private String toUserAgent(ILoggingEvent event) {
    return extractMdcProperty(event, "user-agent");
  }

  private String extractMdcProperty(ILoggingEvent event, String key) {
    var mdcProperties = event.getMDCPropertyMap();
    return Optional.ofNullable(mdcProperties.get(key)).orElse("N/A");
  }
}
