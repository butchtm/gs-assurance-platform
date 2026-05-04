package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = RequestCreditingPeriodValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestCreditingPeriod {
  String message() default "request crediting period is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
