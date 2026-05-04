package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;

public class RequestCreditingPeriodValidator
    implements ConstraintValidator<RequestCreditingPeriod, CreditingPeriodDTO> {

  @Override
  public boolean isValid(
      CreditingPeriodDTO period, ConstraintValidatorContext constraintValidatorContext) {
    if (Objects.isNull(period)) {
      return true;
    }
    var id = period.getPeriodId();
    if (Objects.nonNull(id)) {
      return true;
    }
    if (period.isNewCreditingPeriod()) {
      return period.getEndDate().isAfter(period.getStartDate());
    }
    return false;
  }
}
