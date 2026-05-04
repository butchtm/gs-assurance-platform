package org.goldstandard.assurance.api.dto.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserBatchFileValidatorTest {

  private final UserBatchFileValidator validator = new UserBatchFileValidator();

  @Mock MultipartFile file;

  @Test
  void shouldReturnTrue() {
    when(file.getContentType()).thenReturn("text/csv");
    assertTrue(validator.isValid(file, null));
  }

  @Test
  void shouldReturnFalseWhenContentTypeIsNull() {
    when(file.getContentType()).thenReturn(null);
    assertFalse(validator.isValid(file, null));
  }

  @Test
  void shouldReturnFalseWhenContentTypeIsNotCsv() {
    when(file.getContentType()).thenReturn("image/png");
    assertFalse(validator.isValid(file, null));
  }
}
