package org.goldstandard.assurance.api.entity.creditingperiod;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreditingPeriodUpdates {

  private final Collection<CreditingPeriodUpdate> deletions;
  private final Collection<CreditingPeriodUpdate> updates;
  private final Collection<CreditingPeriodUpdate> additions;
}
