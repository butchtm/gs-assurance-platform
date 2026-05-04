package org.goldstandard.assurance.api.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;

public class RequestMonitoringPeriodValidator
    implements ConstraintValidator<RequestMonitoringPeriod, MonitoringPeriodDTO> {

  @Override
  public boolean isValid(
      MonitoringPeriodDTO period, ConstraintValidatorContext constraintValidatorContext) {
    if (Objects.isNull(period)) {
      return true;
    }
    var id = period.getId();
    if (Objects.nonNull(id)) {
      return true;
    }
    if (period.isNewMonitoringPeriod()) {
      var endDate = excludeTime(period.getEndDate());
      var startDate = excludeTime(period.getStartDate());
      return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }
    return true;
  }

  private static ZonedDateTime excludeTime(ZonedDateTime dateTime) {
    return dateTime.truncatedTo(ChronoUnit.DAYS);
  }
}
