package org.goldstandard.assurance.api.service.report;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusReportFileNameGenerator {

  private final DateTimeFormatter dateFormatter;

  public StatusReportFileNameGenerator() {
    this(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  public String generateFilename(StatusReport report) {
    return String.format("status-report%s.xlsx", toDateRange(report));
  }

  private String toDateRange(StatusReport report) {
    var startDate = report.getFilterStartDate();
    var endDate = report.getFilterEndDate();
    if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
      return "";
    }
    if (Objects.isNull(startDate)) {
      return String.format("-to-%s", endDate.format(dateFormatter));
    }
    if (Objects.isNull(endDate)) {
      return String.format("-from-%s", startDate.format(dateFormatter));
    }
    return String.format(
        "-%s-to-%s", startDate.format(dateFormatter), endDate.format(dateFormatter));
  }
}
