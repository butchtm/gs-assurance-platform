package org.goldstandard.assurance.api.exception;

public class NotFoundException extends BaseApiException {
  public NotFoundException(String format, Object... args) {
    super(format, args);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
