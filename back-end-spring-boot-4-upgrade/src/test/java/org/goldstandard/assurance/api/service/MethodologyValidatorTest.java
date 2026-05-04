package org.goldstandard.assurance.api.service;

import static org.junit.jupiter.api.Assertions.*;

import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MethodologyValidatorTest {

  @InjectMocks MethodologyValidator validator;

  @Test
  void shouldDoNothingIfValid() {
    var dto = MethodologyManagementDTO.builder().description("test").active(true).build();
    assertDoesNotThrow(() -> validator.validate(dto));
  }

  @Test
  void shouldThrowExceptionIfNullDescription() {
    var dto = MethodologyManagementDTO.builder().active(true).build();
    assertThrows(BadRequestException.class, () -> validator.validate(dto));
  }

  @Test
  void shouldThrowExceptionIfNullActiveFlag() {
    var dto = MethodologyManagementDTO.builder().description("test").build();
    assertThrows(BadRequestException.class, () -> validator.validate(dto));
  }
}
