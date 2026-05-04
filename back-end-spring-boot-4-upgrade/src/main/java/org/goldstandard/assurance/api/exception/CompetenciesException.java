package org.goldstandard.assurance.api.exception;

public class CompetenciesException extends RuntimeException {

  public CompetenciesException(String format, Object... args) {
    super(String.format(format, args));
  }
}
