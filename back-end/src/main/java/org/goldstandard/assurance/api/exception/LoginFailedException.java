package org.goldstandard.assurance.api.exception;

public class LoginFailedException extends RuntimeException {

  public LoginFailedException(Throwable cause) {
    super("Login Failed", cause);
  }
}
