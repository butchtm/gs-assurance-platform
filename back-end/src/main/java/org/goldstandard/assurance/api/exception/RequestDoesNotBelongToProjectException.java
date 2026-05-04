package org.goldstandard.assurance.api.exception;

public class RequestDoesNotBelongToProjectException extends RuntimeException {
  public RequestDoesNotBelongToProjectException(int requestId, int projectId) {
    super(
        String.format(
            "Request with id %d does not belong to project with id %d", requestId, projectId));
  }
}
