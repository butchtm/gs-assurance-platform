package org.goldstandard.assurance.api.service.finance;

public class SalesOrderCreationFailedException extends RuntimeException {

  public SalesOrderCreationFailedException(Integer requestId, Throwable cause) {
    super(Integer.toString(requestId), cause);
  }
}
