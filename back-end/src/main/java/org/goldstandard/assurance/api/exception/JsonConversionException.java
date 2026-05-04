package org.goldstandard.assurance.api.exception;

public class JsonConversionException extends RuntimeException {

  public JsonConversionException(Throwable cause) {
    super("Failed to convert between object and json", cause);
  }
}
