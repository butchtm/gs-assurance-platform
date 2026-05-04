package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.fixtures.StatusReportMother;
import org.goldstandard.assurance.api.service.StatusReportConverter;
import org.junit.jupiter.api.Test;

class StatusReportConverterTest {

  private final StatusReportConverter converter = new StatusReportConverter();

  @Test
  void shouldPopulateResponseDTOWithStatusReportData() {
    var statusReport = StatusReportMother.build();

    var convertedReport = converter.toResponseDto(statusReport);

    assertThat(convertedReport.getProjects()).hasSize(2);
    assertThat(convertedReport.getRequests()).hasSize(2);
    var responseAllRequests = convertedReport.getAllRequests();
    assertThat(responseAllRequests.getCompletedCount()).isEqualTo(4);
    assertThat(responseAllRequests.getSuccessfulCompletenessChecksCount())
        .isEqualTo(statusReport.getSuccessfulCompletenessCheckRequestsCount());
  }
}
