package org.goldstandard.assurance.api.exception;

public class AccountTypeIdNotSupportedException extends RuntimeException {

  public AccountTypeIdNotSupportedException(int accountTypeId) {
    super(String.format("Account type id %d not supported", accountTypeId));
  }
}
