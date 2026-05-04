package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchRequestChildValidator {

  public void validateIsNotBatchChild(Request request) {
    if (request.getBatch() != null && !request.getBatch().getParentRequest().equals(request)) {
      throw new BatchRequestException(
          "A batch child request cannot have assignments created against it.");
    }
  }
}
