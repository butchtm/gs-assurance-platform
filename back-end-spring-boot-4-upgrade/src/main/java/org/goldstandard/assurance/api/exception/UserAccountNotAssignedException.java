package org.goldstandard.assurance.api.exception;

public class UserAccountNotAssignedException extends RuntimeException {

  public UserAccountNotAssignedException(String username) {
    super(String.format("User %s has no platform account assigned", username));
  }

  public UserAccountNotAssignedException() {
    super("User has no platform account assigned");
  }
}
