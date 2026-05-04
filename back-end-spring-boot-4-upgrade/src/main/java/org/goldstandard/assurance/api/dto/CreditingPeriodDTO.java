package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.validator.ConsistentMonitoringPeriods;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class CreditingPeriodDTO {
  @Min(value = 1)
  @Max(value = 10)
  private final Integer periodId;

  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;

  private final Collection<CreditingPeriodMethodologyDTO> methodologies;
  @ConsistentMonitoringPeriods private final List<@Valid MonitoringPeriodDTO> monitoringPeriods;

  @JsonIgnore
  public boolean isNewCreditingPeriod() {
    return Objects.isNull(periodId) && Objects.nonNull(startDate) && Objects.nonNull(endDate);
  }
}
