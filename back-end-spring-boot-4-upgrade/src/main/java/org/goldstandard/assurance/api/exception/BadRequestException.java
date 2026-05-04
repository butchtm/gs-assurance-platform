package org.goldstandard.assurance.api.exception;

public class BadRequestException extends BaseApiException {
  public BadRequestException(String format, Object... args) {
    super(format, args);
  }
}
