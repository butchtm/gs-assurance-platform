package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MethodologyFactoryTest {

  @InjectMocks MethodologyFactory factory;

  @Test
  void shouldCreateNewMethodology() {
    var dto = MethodologyManagementDTO.builder().description("test").active(true).build();
    var result = factory.create(dto);
    assertThat(result.getDescription()).isEqualTo(dto.getDescription());
    assertThat(result.getCode()).isEqualTo(dto.getDescription().toUpperCase());
    assertThat(result.getActive()).isEqualTo(dto.getActive());
  }

  @Test
  void shouldUpdateExistingToNewValues() {
    var dto = MethodologyManagementDTO.builder().description("test").active(true).build();
    var existing =
        Methodology.builder()
            .id(1)
            .description("existing")
            .code("EXISTING")
            .isActive(false)
            .build();
    var result = factory.update(existing, dto);
    assertThat(result.getId()).isEqualTo(existing.getId());
    assertThat(result.getDescription()).isEqualTo(dto.getDescription());
    assertThat(result.getCode()).isEqualTo(dto.getDescription().toUpperCase());
    assertThat(result.getActive()).isEqualTo(dto.getActive());
  }

  @Test
  void shouldNotUpdateIfNoValuesInDTO() {
    var dto = MethodologyManagementDTO.builder().build();
    var existing =
        Methodology.builder()
            .id(1)
            .description("existing")
            .code("EXISTING")
            .isActive(false)
            .build();
    var result = factory.update(existing, dto);
    assertThat(result.getId()).isEqualTo(existing.getId());
    assertThat(result.getDescription()).isEqualTo(existing.getDescription());
    assertThat(result.getCode()).isEqualTo(existing.getCode());
    assertThat(result.getActive()).isEqualTo(existing.getActive());
  }
}
