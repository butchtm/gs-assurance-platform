package org.goldstandard.assurance.api.exception;

public class RendererException extends BaseApiException {
  public RendererException(String format, Object... args) {
    super(format, args);
  }
}
