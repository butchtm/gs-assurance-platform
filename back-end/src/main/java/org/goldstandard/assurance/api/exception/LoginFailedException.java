package org.goldstandard.assurance.api.exception;

public class LoginFailedException extends RuntimeException {

  public LoginFailedException() {
    super("Login Failed");
  }

  public LoginFailedException(Throwable cause) {
    super("Login Failed", cause);
  }
}
