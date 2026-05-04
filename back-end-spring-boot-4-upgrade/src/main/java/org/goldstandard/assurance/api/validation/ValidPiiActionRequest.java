package org.goldstandard.assurance.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PiiActionRequestValidator.class)
public @interface ValidPiiActionRequest {
  String message() default "Invalid PII action request";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
