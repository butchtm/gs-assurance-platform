package org.goldstandard.assurance.api.entity.creditingperiod;

import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.enumeration.MethodologyOperation;

@Builder
@Getter
public class MethodologyUpdate {
  private final Integer projectId;
  private final Integer periodId;
  private final Integer methodologyId;
  private final Double originalVersion;
  private final Double updatedVersion;
  private final MethodologyOperation operation;

  public boolean isDeletion() {
    return hasOperation(MethodologyOperation.DELETE);
  }

  public boolean hasOperation(MethodologyOperation otherOperation) {
    return operation == otherOperation;
  }
}
