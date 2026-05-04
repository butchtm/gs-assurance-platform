package org.goldstandard.assurance.api.entity;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.view.StatusReportProjectsGrouping;
import org.goldstandard.assurance.api.entity.view.StatusReportRequestsGrouping;

@Builder
@Getter
public class StatusReport {
  private final List<StatusReportProjectsGrouping> projectsByType;
  private final List<StatusReportRequestsGrouping> completedRequests;
  private final Integer successfulCompletenessCheckRequestsCount;
  private final LocalDate filterStartDate;
  private final LocalDate filterEndDate;
}
