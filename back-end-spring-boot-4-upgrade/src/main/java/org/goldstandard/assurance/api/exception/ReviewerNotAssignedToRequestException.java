package org.goldstandard.assurance.api.exception;

public class ReviewerNotAssignedToRequestException extends RuntimeException {

  public ReviewerNotAssignedToRequestException(int requestId) {
    super(String.format("No reviewer or registry account assigned to request %d", requestId));
  }
}
