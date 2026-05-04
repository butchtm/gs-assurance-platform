package org.goldstandard.assurance.api.exception;

import java.util.UUID;

public class DocumentNotAssociatedWithRequestException extends RuntimeException {

  public DocumentNotAssociatedWithRequestException(UUID documentId) {
    super(String.format("Document %s not associated with request", documentId));
  }

  public DocumentNotAssociatedWithRequestException(UUID documentId, int requestId) {
    super(String.format("Document %s not associated with request %d", documentId, requestId));
  }
}
