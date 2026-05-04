package org.goldstandard.assurance.api.exception;

import lombok.Getter;

@Getter
public class UserUpsertException extends RuntimeException {

  private final String accountName;

  public UserUpsertException(String message, String accountName, Throwable cause) {
    super(message, cause);
    this.accountName = accountName;
  }

  public UserUpsertException(String message, Throwable cause) {
    super(message, cause);
    this.accountName = null;
  }

  public UserUpsertException(String message, String accountName) {
    super(message);
    this.accountName = accountName;
  }
}
