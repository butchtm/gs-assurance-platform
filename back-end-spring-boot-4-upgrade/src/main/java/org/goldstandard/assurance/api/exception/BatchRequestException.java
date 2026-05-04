package org.goldstandard.assurance.api.exception;

public class BatchRequestException extends RuntimeException {

  public BatchRequestException(String format, Object... args) {
    super(String.format(format, args));
  }
}
