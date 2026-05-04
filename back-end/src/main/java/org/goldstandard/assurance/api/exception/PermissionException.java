package org.goldstandard.assurance.api.exception;

public class PermissionException extends BaseApiException {
  public PermissionException(String format, Object... args) {
    super(String.format(format, args));
  }
}
