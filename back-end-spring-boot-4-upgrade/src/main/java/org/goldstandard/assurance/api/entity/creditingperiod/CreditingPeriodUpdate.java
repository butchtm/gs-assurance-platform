package org.goldstandard.assurance.api.entity.creditingperiod;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;

@Builder
@Getter
public class CreditingPeriodUpdate {

  private final Integer id;
  private final ProjectCreditingPeriod original;
  private final ProjectCreditingPeriod updated;
  private final ZonedDateTime updatedAt;
  private final Account updatedBy;
  private final Request request;

  public boolean isDeletion() {
    return Objects.nonNull(original) && Objects.isNull(updated);
  }

  public boolean isUpdate() {
    return Objects.nonNull(original) && Objects.nonNull(updated);
  }

  public boolean isAddition() {
    return Objects.isNull(original) && Objects.nonNull(updated);
  }

  public Integer getProjectId() {
    if (Objects.nonNull(original)) {
      return original.getProjectId();
    }
    return updated.getProjectId();
  }

  public Optional<Integer> getRequestId() {
    return Optional.ofNullable(request).map(Request::getId);
  }

  public Collection<ProjectCreditingPeriodMethodology> getOriginalMethodologies() {
    return toMethodologies(original);
  }

  public Collection<ProjectCreditingPeriodMethodology> getUpdatedMethodologies() {
    return toMethodologies(updated);
  }

  public Collection<ProjectCreditingMonitoringPeriod> getOriginalMonitoringPeriods() {
    return toMonitoringPeriods(original);
  }

  public Collection<ProjectCreditingMonitoringPeriod> getUpdatedMonitoringPeriods() {
    return toMonitoringPeriods(updated);
  }

  private static Collection<ProjectCreditingPeriodMethodology> toMethodologies(
      ProjectCreditingPeriod period) {
    return Optional.ofNullable(period)
        .map(ProjectCreditingPeriod::getMethodologies)
        .orElse(Collections.emptyList());
  }

  private static Collection<ProjectCreditingMonitoringPeriod> toMonitoringPeriods(
      ProjectCreditingPeriod period) {
    return Optional.ofNullable(period)
        .map(ProjectCreditingPeriod::getMonitoringPeriods)
        .orElse(Collections.emptyList());
  }
}
