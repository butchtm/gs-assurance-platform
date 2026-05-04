package org.goldstandard.assurance.api.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.PiiActionRequestDTO;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PiiActionRequestValidatorTest {

  private PiiActionRequestValidator validator;
  @Mock private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    validator = new PiiActionRequestValidator();
  }

  @Test
  void shouldPassValidation_WhenOnlyProjectIdProvided() {
    var request = PiiActionRequestDTO.builder().projectId(1).piiAction(new PiiAction(1)).build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isTrue();
  }

  @Test
  void shouldFailValidation_WhenNoIdsProvided() {
    var request = PiiActionRequestDTO.builder().piiAction(new PiiAction(1)).build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isFalse();
  }

  @Test
  void shouldPassValidation_WhenContainsProjectIdRequestIdAndDocumentId() {
    var request =
        PiiActionRequestDTO.builder()
            .projectId(1)
            .requestId(2)
            .documentId(UUID.randomUUID())
            .piiAction(new PiiAction(1))
            .build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isTrue();
  }

  @Test
  void shouldFailValidation_WhenBothCommentAndDocumentProvided() {
    var request =
        PiiActionRequestDTO.builder()
            .projectId(1)
            .requestId(2)
            .documentId(UUID.randomUUID())
            .commentId(3)
            .piiAction(new PiiAction(1))
            .build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isFalse();
  }

  @Test
  void shouldFailValidation_WhenRequestIdProvidedWithoutDocumentOrComment() {
    var request =
        PiiActionRequestDTO.builder().projectId(1).requestId(2).piiAction(new PiiAction(1)).build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isFalse();
  }

  @Test
  void shouldFailValidation_WhenDocumentProvidedWithoutRequest() {
    var request =
        PiiActionRequestDTO.builder()
            .projectId(1)
            .documentId(UUID.randomUUID())
            .piiAction(new PiiAction(1))
            .build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isFalse();
  }

  @Test
  void shouldFailValidation_WhenCommentProvidedWithoutRequest() {
    var request =
        PiiActionRequestDTO.builder().projectId(1).commentId(3).piiAction(new PiiAction(1)).build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isFalse();
  }

  @Test
  void shouldPassValidation_WhenRequestIdAndCommentProvided() {
    var request =
        PiiActionRequestDTO.builder()
            .projectId(1)
            .requestId(2)
            .commentId(3)
            .piiAction(new PiiAction(1))
            .build();

    boolean isValid = validator.isValid(request, context);

    assertThat(isValid).isTrue();
  }
}
