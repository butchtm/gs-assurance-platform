package org.goldstandard.assurance.api.exception;

public class NotificationException extends BaseApiException {
  public NotificationException(String format, Object... args) {
    super(format, args);
  }
}
