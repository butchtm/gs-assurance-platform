package org.goldstandard.assurance.api.exception;

public abstract class BaseApiException extends RuntimeException {
  protected BaseApiException(String format, Object... args) {
    super(String.format(format, args));
  }

  protected BaseApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
