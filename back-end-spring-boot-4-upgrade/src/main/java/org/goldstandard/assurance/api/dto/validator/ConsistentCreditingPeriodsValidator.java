package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;

public class ConsistentCreditingPeriodsValidator
    implements ConstraintValidator<ConsistentCreditingPeriods, List<CreditingPeriodDTO>> {

  @Override
  public boolean isValid(
      List<CreditingPeriodDTO> creditingPeriodDTOS,
      ConstraintValidatorContext constraintValidatorContext) {
    if (creditingPeriodDTOS == null) {
      // it's ok to not specify any crediting periods
      return true;
    }
    // check for distinct period ids
    var periodIds =
        creditingPeriodDTOS.stream()
            .map(CreditingPeriodDTO::getPeriodId)
            .collect(Collectors.toSet());
    return periodIds.size() == creditingPeriodDTOS.size();
  }
}
