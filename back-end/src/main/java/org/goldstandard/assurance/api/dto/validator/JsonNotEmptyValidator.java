package org.goldstandard.assurance.api.dto.validator;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class JsonNotEmptyValidator implements ConstraintValidator<JsonNotEmpty, JsonNode> {

  @Override
  public boolean isValid(JsonNode value, ConstraintValidatorContext constraintValidatorContext) {
    return Objects.nonNull(value) && !value.isEmpty();
  }
}
