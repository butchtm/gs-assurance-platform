package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.goldstandard.assurance.api.dto.ExcelStatusReport;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.goldstandard.assurance.api.fixtures.StatusReportMother;
import org.goldstandard.assurance.api.repository.StatusReportDAO;
import org.goldstandard.assurance.api.service.report.StatusReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatusReportServiceTest {

  @InjectMocks private StatusReportService service;
  @Mock private StatusReportDAO dao;
  @Mock private StatusReportGenerator reportGenerator;

  @Test
  void shouldReturnWrappedStatusReportDaoData() {
    var projectGroups = StatusReportMother.buildProjectGroups();
    var requestGroups = StatusReportMother.buildRequestGroups();
    var successfulCompletenessChecksCount = 5;

    when(dao.fetchProjectsGroupedByType(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(projectGroups);
    when(dao.fetchCompletedRequestGroupedByType(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(requestGroups);
    when(dao.getSuccessfulCompletenessCheckRequestsCount(
            any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(successfulCompletenessChecksCount);

    var statusReportData = service.getStatusReport();

    assertThat(statusReportData.getProjectsByType()).isEqualTo(projectGroups);
    assertThat(statusReportData.getCompletedRequests()).isEqualTo(requestGroups);
    assertThat(statusReportData.getSuccessfulCompletenessCheckRequestsCount())
        .isEqualTo(successfulCompletenessChecksCount);

    verify(dao).fetchProjectsGroupedByType(any(LocalDate.class), any(LocalDate.class));
    verify(dao).fetchCompletedRequestGroupedByType(any(LocalDate.class), any(LocalDate.class));
    verify(dao)
        .getSuccessfulCompletenessCheckRequestsCount(any(LocalDate.class), any(LocalDate.class));
  }

  @Test
  void shouldReturnWrappedStatusReportDaoDataWithCustomDates() {
    var projectGroups = StatusReportMother.buildProjectGroups();
    var requestGroups = StatusReportMother.buildRequestGroups();
    var successfulCompletenessChecksCount = 5;
    var startDate = LocalDate.of(2023, 1, 1);
    var endDate = LocalDate.of(2023, 12, 31);

    when(dao.fetchProjectsGroupedByType(startDate, endDate)).thenReturn(projectGroups);
    when(dao.fetchCompletedRequestGroupedByType(startDate, endDate)).thenReturn(requestGroups);
    when(dao.getSuccessfulCompletenessCheckRequestsCount(startDate, endDate))
        .thenReturn(successfulCompletenessChecksCount);

    var statusReportData = service.getStatusReport(startDate, endDate);

    assertThat(statusReportData.getProjectsByType()).isEqualTo(projectGroups);
    assertThat(statusReportData.getCompletedRequests()).isEqualTo(requestGroups);
    assertThat(statusReportData.getSuccessfulCompletenessCheckRequestsCount())
        .isEqualTo(successfulCompletenessChecksCount);

    verify(dao).fetchProjectsGroupedByType(startDate, endDate);
    verify(dao).fetchCompletedRequestGroupedByType(startDate, endDate);
    verify(dao).getSuccessfulCompletenessCheckRequestsCount(startDate, endDate);
  }

  @Test
  void shouldGetExcelStatusReport() {
    var projectGroups = StatusReportMother.buildProjectGroups();
    var requestGroups = StatusReportMother.buildRequestGroups();
    var successfulCompletenessChecksCount = 5;
    var startDate = LocalDate.of(2023, 1, 1);
    var endDate = LocalDate.of(2023, 12, 31);
    var expectedExcelReport =
        ExcelStatusReport.builder()
            .data(new byte[] {1, 2, 3, 4, 5})
            .filename("status_report_2023-01-01_to_2023-12-31.xlsx")
            .build();

    when(dao.fetchProjectsGroupedByType(startDate, endDate)).thenReturn(projectGroups);
    when(dao.fetchCompletedRequestGroupedByType(startDate, endDate)).thenReturn(requestGroups);
    when(dao.getSuccessfulCompletenessCheckRequestsCount(startDate, endDate))
        .thenReturn(successfulCompletenessChecksCount);

    when(reportGenerator.toExcel(any(StatusReport.class))).thenReturn(expectedExcelReport);

    var result = service.getExcelStatusReport(startDate, endDate);

    assertThat(result.getData()).isEqualTo(expectedExcelReport.getData());
    assertThat(result.getFilename()).isEqualTo(expectedExcelReport.getFilename());

    verify(dao).fetchProjectsGroupedByType(startDate, endDate);
    verify(dao).fetchCompletedRequestGroupedByType(startDate, endDate);
    verify(dao).getSuccessfulCompletenessCheckRequestsCount(startDate, endDate);

    verify(reportGenerator).toExcel(any(StatusReport.class));
  }
}
