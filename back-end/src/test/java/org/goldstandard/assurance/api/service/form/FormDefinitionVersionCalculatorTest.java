package org.goldstandard.assurance.api.service.form;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.entity.DigitalFormType;
import org.goldstandard.assurance.api.repository.FormDefinitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FormDefinitionVersionCalculatorTest {

  @InjectMocks FormDefinitionVersionCalculator calculator;

  @Mock FormDefinitionRepository repository;

  @Test
  void shouldIncrementMinorVersion() {
    var type = buildType();
    givenLatestVersion(type, 1.11);
    var dto = FormDefinitionPublishDTO.builder().increaseMajorVersion(false).build();

    var newVersion = calculator.calculate(type, dto);

    assertThat(newVersion).isEqualTo(1.12);
  }

  @Test
  void shouldDropDecimalsWhenIncrementingMajorVersion() {
    var type = buildType();
    givenLatestVersion(type, 2.22);
    var dto = FormDefinitionPublishDTO.builder().increaseMajorVersion(true).build();

    var newVersion = calculator.calculate(type, dto);

    assertThat(newVersion).isEqualTo(3.0);
  }

  private DigitalFormType buildType() {
    return new DigitalFormType(0);
  }

  private void givenLatestVersion(DigitalFormType type, double latestVersion) {
    when(repository.findLatestPublishedVersionNumber(type.getId())).thenReturn(latestVersion);
  }
}
