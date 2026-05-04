package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.MethodologyUpdateConverter.toMethodologyAdditions;
import static org.goldstandard.assurance.api.service.MethodologyUpdateConverter.toMethodologyDeletions;
import static org.goldstandard.assurance.api.service.MethodologyUpdateConverter.toPeriodMethodologyUpdates;
import static org.goldstandard.assurance.api.service.MonitoringPeriodUpdateConverter.toCreditingPeriodMonitoringPeriodUpdates;
import static org.goldstandard.assurance.api.service.MonitoringPeriodUpdateConverter.toMonitoringPeriodAdditions;
import static org.goldstandard.assurance.api.service.MonitoringPeriodUpdateConverter.toMonitoringPeriodDeletions;

import java.util.Collection;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MethodologyUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MonitoringPeriodUpdate;
import org.springframework.stereotype.Component;

@Component
public class CreditingPeriodUpdateConverter {

  public Collection<MethodologyUpdate> toMethodologyUpdates(CreditingPeriodUpdate periodUpdate) {
    if (periodUpdate.isAddition()) {
      return toMethodologyAdditions(periodUpdate.getUpdated());
    }
    if (periodUpdate.isDeletion()) {
      return toMethodologyDeletions(periodUpdate.getOriginal());
    }
    return toPeriodMethodologyUpdates(periodUpdate);
  }

  public Collection<MonitoringPeriodUpdate> toMonitoringPeriodUpdates(
      CreditingPeriodUpdate creditingPeriodUpdate) {
    if (creditingPeriodUpdate.isAddition()) {
      return toMonitoringPeriodAdditions(creditingPeriodUpdate.getUpdated());
    }
    if (creditingPeriodUpdate.isDeletion()) {
      return toMonitoringPeriodDeletions(creditingPeriodUpdate.getOriginal());
    }
    return toCreditingPeriodMonitoringPeriodUpdates(creditingPeriodUpdate);
  }
}
