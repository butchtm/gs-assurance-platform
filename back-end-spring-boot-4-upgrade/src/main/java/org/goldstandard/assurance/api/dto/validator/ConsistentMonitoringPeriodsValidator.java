package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;

public class ConsistentMonitoringPeriodsValidator
    implements ConstraintValidator<ConsistentMonitoringPeriods, List<MonitoringPeriodDTO>> {

  @Override
  public boolean isValid(
      List<MonitoringPeriodDTO> monitoringPeriodDTOS,
      ConstraintValidatorContext constraintValidatorContext) {
    if (monitoringPeriodDTOS == null) {
      // it's ok to not specify any monitoring periods
      return true;
    }
    // check for distinct period ids
    var periodIds =
        monitoringPeriodDTOS.stream().map(MonitoringPeriodDTO::getId).collect(Collectors.toSet());
    return periodIds.size() == monitoringPeriodDTOS.size();
  }
}
