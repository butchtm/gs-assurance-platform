package org.goldstandard.assurance.api.exception;

public class InvalidNumberOfWeekdaysException extends RuntimeException {

  public InvalidNumberOfWeekdaysException(int workingDays) {
    super(Integer.toString(workingDays));
  }
}
