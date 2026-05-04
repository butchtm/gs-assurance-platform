package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class CommonDashboardRequestDTO {
  private final Integer id;
  private final String name;
  private final RequestStatusDTO requestStatus;
  private final ZonedDateTime startDate;
  private final ZonedDateTime closedDate;
  private final Integer currentRoundIndex;
  private final ZonedDateTime firstRoundStartDate;
  private final ZonedDateTime currentRoundEndDate;
  private final String projectTypesString;
  private final CommonDashboardRequestProjectDTO project;
  private final CommonDashboardRequestInvoiceDTO invoice;
  private final CommonDashboardRequestMonitoringPeriodDTO monitoringPeriod;
}
