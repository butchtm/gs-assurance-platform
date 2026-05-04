package org.goldstandard.assurance.api.exception;

public class RefreshTokenFailedException extends RuntimeException {

  public RefreshTokenFailedException(Throwable cause) {
    super("Refresh Token Failed", cause);
  }
}
