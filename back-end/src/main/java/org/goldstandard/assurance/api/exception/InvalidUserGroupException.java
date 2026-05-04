package org.goldstandard.assurance.api.exception;

public class InvalidUserGroupException extends RuntimeException {

  public InvalidUserGroupException(String group) {
    super(group);
  }
}
