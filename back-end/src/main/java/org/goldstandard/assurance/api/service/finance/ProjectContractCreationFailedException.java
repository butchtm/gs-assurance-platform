package org.goldstandard.assurance.api.service.finance;

public class ProjectContractCreationFailedException extends RuntimeException {

  public ProjectContractCreationFailedException(String message) {
    super(message);
  }

  public ProjectContractCreationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
