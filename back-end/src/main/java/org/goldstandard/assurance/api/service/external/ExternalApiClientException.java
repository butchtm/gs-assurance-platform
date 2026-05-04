package org.goldstandard.assurance.api.service.external;

import java.util.Optional;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

@Getter
public class ExternalApiClientException extends RuntimeException {

  private final HttpStatusCode statusCode;

  public ExternalApiClientException(Throwable cause) {
    super(cause);
    this.statusCode = null;
  }

  public ExternalApiClientException(HttpClientErrorException cause) {
    super(cause);
    this.statusCode = cause.getStatusCode();
  }

  public ExternalApiClientException(String message, HttpStatusCode statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public ExternalApiClientException(String message) {
    super(message);
    this.statusCode = null;
  }

  public Optional<HttpStatusCode> getStatusCode() {
    return Optional.ofNullable(statusCode);
  }
}
