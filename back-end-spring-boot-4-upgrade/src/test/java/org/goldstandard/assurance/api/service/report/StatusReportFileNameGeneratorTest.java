package org.goldstandard.assurance.api.service.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.junit.jupiter.api.Test;

class StatusReportFileNameGeneratorTest {

  private final StatusReportFileNameGenerator generator = new StatusReportFileNameGenerator();

  @Test
  void shouldReturnFilenameWithToAndFromDates() {
    var report =
        StatusReport.builder()
            .filterStartDate(LocalDate.of(2025, 9, 1))
            .filterEndDate(LocalDate.of(2025, 10, 30))
            .build();

    var filename = generator.generateFilename(report);

    assertThat(filename).isEqualTo("status-report-2025-09-01-to-2025-10-30.xlsx");
  }

  @Test
  void shouldReturnFilenameWithOnlyToDate() {
    var report = StatusReport.builder().filterEndDate(LocalDate.of(2024, 7, 5)).build();

    var filename = generator.generateFilename(report);

    assertThat(filename).isEqualTo("status-report-to-2024-07-05.xlsx");
  }

  @Test
  void shouldReturnFilenameWithOnlyFromDate() {
    var report = StatusReport.builder().filterStartDate(LocalDate.of(2023, 1, 2)).build();

    var filename = generator.generateFilename(report);

    assertThat(filename).isEqualTo("status-report-from-2023-01-02.xlsx");
  }

  @Test
  void shouldReturnFilenameWithNoDates() {
    var report = StatusReport.builder().build();

    var filename = generator.generateFilename(report);

    assertThat(filename).isEqualTo("status-report.xlsx");
  }
}
