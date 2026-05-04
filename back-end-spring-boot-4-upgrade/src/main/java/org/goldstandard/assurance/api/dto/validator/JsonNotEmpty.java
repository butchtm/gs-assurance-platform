package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {JsonNotEmptyValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonNotEmpty {
  Class<? extends Payload>[] payload() default {};

  Class<?>[] groups() default {};

  String message() default "must not be empty";
}
