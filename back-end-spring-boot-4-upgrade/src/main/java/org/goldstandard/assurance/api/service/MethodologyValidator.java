package org.goldstandard.assurance.api.service;

import java.util.Objects;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class MethodologyValidator {

  public void validate(MethodologyManagementDTO methodologyDTO) {
    if (Objects.isNull(methodologyDTO.getDescription())) {
      throw new BadRequestException("Description must not be null.");
    }
    if (Objects.isNull(methodologyDTO.getActive())) {
      throw new BadRequestException("Active flag must not be null.");
    }
  }
}
