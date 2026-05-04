package org.goldstandard.assurance.api.exception;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String username) {
    super(toMessage(username));
  }

  public UserAlreadyExistsException(String username, Throwable cause) {
    super(toMessage(username), cause);
  }

  private static String toMessage(String username) {
    return String.format("user %s already exists", username);
  }
}
