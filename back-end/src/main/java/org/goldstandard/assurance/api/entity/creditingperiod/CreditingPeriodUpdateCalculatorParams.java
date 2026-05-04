package org.goldstandard.assurance.api.entity.creditingperiod;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;

@Builder
@Getter
public class CreditingPeriodUpdateCalculatorParams {

  private final ZonedDateTime updatedAt;
  private final Account updatedBy;
  private final Collection<ProjectCreditingPeriod> originalPeriods;
  private final Collection<ProjectCreditingPeriod> updatedPeriods;
  private final Request request;

  public Collection<ProjectCreditingPeriod> getOriginalPeriods() {
    return emptyIfNull(originalPeriods);
  }

  public Collection<ProjectCreditingPeriod> getUpdatedPeriods() {
    return emptyIfNull(updatedPeriods);
  }

  private static Collection<ProjectCreditingPeriod> emptyIfNull(
      Collection<ProjectCreditingPeriod> periods) {
    return Optional.ofNullable(periods).orElse(Collections.emptyList());
  }
}
