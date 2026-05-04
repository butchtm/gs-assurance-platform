package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.exception.NotFoundException;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
public class MonitoringPeriodDTO {

  private final Integer id;
  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;
  private final Integer originRequestId;
  private final Integer feeSchedule;

  public MonitoringPeriodDTO(Integer id) {
    this(id, null, null, null, null);
  }

  @JsonIgnore
  public boolean isNewMonitoringPeriod() {
    return Objects.isNull(id) && Objects.nonNull(startDate) && Objects.nonNull(endDate);
  }

  @JsonIgnore
  public boolean isUpdatedMonitoringPeriod(Request request) {
    if (Objects.isNull(id) || Objects.isNull(feeSchedule)) {
      return false;
    }
    var creditingPeriod = getProjectCreditingPeriod(request);
    var projectMonitoringPeriod = getProjectMonitoringPeriod(request, creditingPeriod);
    if (projectMonitoringPeriod.isPresent()) {
      return !Objects.equals(projectMonitoringPeriod.get().getFeeSchedule(), feeSchedule);
    }
    return false;
  }

  @JsonIgnore
  private ProjectCreditingPeriod getProjectCreditingPeriod(Request request) {
    return request.getProject().getCreditingPeriods().stream()
        .filter(period -> Objects.equals(period.getPeriodId(), request.getCreditingPeriod()))
        .findFirst()
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Crediting Period with id %s not found", request.getCreditingPeriod()));
  }

  @JsonIgnore
  private Optional<ProjectCreditingMonitoringPeriod> getProjectMonitoringPeriod(
      Request request, ProjectCreditingPeriod creditingPeriod) {
    return creditingPeriod.getMonitoringPeriods().stream()
        .filter(period -> Objects.equals(period.getId(), request.getMonitoringPeriodId()))
        .findFirst();
  }
}
