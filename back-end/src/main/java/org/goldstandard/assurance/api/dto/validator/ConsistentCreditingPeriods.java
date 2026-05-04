package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = ConsistentCreditingPeriodsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConsistentCreditingPeriods {
  String message() default "crediting periods must be unique";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
