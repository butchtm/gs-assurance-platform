package org.goldstandard.assurance.api.service.form;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.entity.DigitalFormType;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormDefinitionVersionCalculator {

  private final FormDefinitionRepository repository;

  public Double calculate(DigitalFormType type, FormDefinitionPublishDTO dto) {
    var latestVersion = repository.findLatestPublishedVersionNumber(type.getId());
    return calculateIncrease(latestVersion, dto);
  }

  private double calculateIncrease(double latestVersion, FormDefinitionPublishDTO dto) {
    if (dto.isIncreaseMajorVersion()) {
      return applyMajorIncrease(latestVersion);
    }
    return applyMinorIncrease(latestVersion);
  }

  private double applyMajorIncrease(double latestVersion) {
    return (int) latestVersion + 1d;
  }

  private double applyMinorIncrease(double latestVersion) {
    return latestVersion + 0.01d;
  }
}
