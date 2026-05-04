package org.goldstandard.assurance.api.service;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestDraftMonitoringPeriodFactory {

  public Optional<RequestMonitoringPeriod> build(Request request, MonitoringPeriodDTO dto) {
    if (!containsMonitoringPeriodChanges(request, dto)) {
      return Optional.empty();
    }

    return Optional.of(
        RequestMonitoringPeriod.builder()
            .requestId(request.getId())
            .periodId(dto.getId())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .feeSchedule(dto.getFeeSchedule())
            .build());
  }

  private boolean containsMonitoringPeriodChanges(Request request, MonitoringPeriodDTO dto) {
    if (Objects.isNull(dto)) {
      return false;
    }
    if (dto.isNewMonitoringPeriod()) {
      return true;
    }
    return dto.isUpdatedMonitoringPeriod(request);
  }
}
