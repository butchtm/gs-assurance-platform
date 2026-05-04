package org.goldstandard.assurance.api.exception;

public class CannotCompleteActivityException extends RuntimeException {

  public CannotCompleteActivityException(int id) {
    super(String.format("Cannot manually complete activity %d", id));
  }
}
