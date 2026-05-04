package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import tools.jackson.databind.JsonNode;

public class JsonNotEmptyValidator implements ConstraintValidator<JsonNotEmpty, JsonNode> {

  @Override
  public boolean isValid(JsonNode value, ConstraintValidatorContext constraintValidatorContext) {
    return Objects.nonNull(value) && !value.isEmpty();
  }
}
