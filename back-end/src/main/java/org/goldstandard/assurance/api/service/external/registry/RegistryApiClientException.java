package org.goldstandard.assurance.api.service.external.registry;

import lombok.Getter;
import org.goldstandard.assurance.api.service.external.ExternalApiClientException;
import org.springframework.http.HttpStatusCode;

@Getter
public class RegistryApiClientException extends ExternalApiClientException {

  public RegistryApiClientException(Throwable cause) {
    super(cause);
  }

  public RegistryApiClientException(String message, HttpStatusCode statusCode) {
    super(message, statusCode);
  }

  public RegistryApiClientException(String message) {
    super(message);
  }
}
