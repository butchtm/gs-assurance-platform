package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = RequestMonitoringPeriodValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMonitoringPeriod {
  String message() default "a monitoring period must be selected OR created on a request, not both";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
