package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchRequestCreateValidator {

  public void validate(BatchRequestCreateDTO dto) {
    if (dto.getNumberOfProjects() < 2) {
      throw new BatchRequestException("a batch request must contain more than one project");
    }
  }
}
