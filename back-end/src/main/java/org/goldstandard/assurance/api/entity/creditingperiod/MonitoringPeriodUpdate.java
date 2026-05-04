package org.goldstandard.assurance.api.entity.creditingperiod;

import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.enumeration.MonitoringPeriodOperation;

@Builder
@Getter
public class MonitoringPeriodUpdate {
  private final Integer projectId;
  private final Integer periodId;
  private final Integer id;
  private final ProjectCreditingMonitoringPeriod originalPeriod;
  private final ProjectCreditingMonitoringPeriod updatedPeriod;
  private final MonitoringPeriodOperation operation;

  public boolean isDeletion() {
    return hasOperation(MonitoringPeriodOperation.DELETE);
  }

  private boolean hasOperation(MonitoringPeriodOperation otherOperation) {
    return operation == otherOperation;
  }
}
