package org.goldstandard.assurance.api.service.external;

public class ExternalUpdateAbortException extends RuntimeException {

  public ExternalUpdateAbortException(ExternalSystem system, Throwable cause) {
    super(system.name(), cause);
  }
}
