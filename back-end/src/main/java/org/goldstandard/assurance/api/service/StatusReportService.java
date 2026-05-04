package org.goldstandard.assurance.api.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ExcelStatusReport;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.goldstandard.assurance.api.repository.StatusReportDAO;
import org.goldstandard.assurance.api.service.report.StatusReportFileNameGenerator;
import org.goldstandard.assurance.api.service.report.StatusReportGenerator;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StatusReportService {

  private final StatusReportDAO dao;
  private final StatusReportGenerator reportGenerator;
  private final StatusReportFileNameGenerator fileNameGenerator;

  public StatusReport getStatusReport() {

    LocalDate today = LocalDate.now();
    LocalDate oneMonthAgo = today.minusMonths(1).plusDays(1);
    return getStatusReport(oneMonthAgo, today);
  }

  public StatusReport getStatusReport(LocalDate filterStartDate, LocalDate filterEndDate) {

    return StatusReport.builder()
        .projectsByType(dao.fetchProjectsGroupedByType(filterStartDate, filterEndDate))
        .completedRequests(dao.fetchCompletedRequestGroupedByType(filterStartDate, filterEndDate))
        .successfulCompletenessCheckRequestsCount(
            dao.getSuccessfulCompletenessCheckRequestsCount(filterStartDate, filterEndDate))
        .filterStartDate(filterStartDate)
        .filterEndDate(filterEndDate)
        .build();
  }

  public ExcelStatusReport getExcelStatusReport(LocalDate startDate, LocalDate endDate) {
    var statusReport = getStatusReport(startDate, endDate);
    return reportGenerator.toExcel(statusReport);
  }
}
