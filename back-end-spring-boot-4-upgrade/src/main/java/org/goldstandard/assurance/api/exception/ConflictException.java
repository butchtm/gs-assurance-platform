package org.goldstandard.assurance.api.exception;

public class ConflictException extends BaseApiException {
  public ConflictException(String format, Object... args) {
    super(format, args);
  }
}
