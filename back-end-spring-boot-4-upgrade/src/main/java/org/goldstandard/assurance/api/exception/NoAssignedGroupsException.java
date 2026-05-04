package org.goldstandard.assurance.api.exception;

public class NoAssignedGroupsException extends RuntimeException {
  public NoAssignedGroupsException(String username) {
    super(String.format("User %s has no roles assigned", username));
  }
}
