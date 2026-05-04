package org.goldstandard.assurance.api.exception;

public class DataException extends BaseApiException {
  public DataException(String format, Object... args) {
    super(format, args);
  }
}
