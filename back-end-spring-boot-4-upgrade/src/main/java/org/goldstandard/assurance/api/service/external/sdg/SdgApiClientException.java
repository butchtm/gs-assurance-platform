package org.goldstandard.assurance.api.service.external.sdg;

import lombok.Getter;
import org.goldstandard.assurance.api.service.external.ExternalApiClientException;
import org.springframework.http.HttpStatusCode;

@Getter
public class SdgApiClientException extends ExternalApiClientException {

  public SdgApiClientException(Throwable cause) {
    super(cause);
  }

  public SdgApiClientException(String message, HttpStatusCode statusCode) {
    super(message, statusCode);
  }

  public SdgApiClientException(String message) {
    super(message);
  }
}
