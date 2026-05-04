package org.goldstandard.assurance.api.exception;

public class MfaException extends RuntimeException {
  public MfaException(String message, Throwable cause) {
    super(message, cause);
  }
}
