package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.goldstandard.assurance.api.dto.StatusReportDTO;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.goldstandard.assurance.api.service.StatusReportConverter;
import org.goldstandard.assurance.api.service.StatusReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatusReportControllerTest {

  @InjectMocks private StatusReportController controller;
  @Mock private StatusReportService service;
  @Mock private StatusReportConverter converter;

  @Test
  void shouldReturnConvertedStatusReport() {
    var statusReportData = mock(StatusReport.class);
    var statusReportResponse = mock(StatusReportDTO.class);

    when(service.getStatusReport(null, null)).thenReturn(statusReportData);
    when(converter.toResponseDto(statusReportData)).thenReturn(statusReportResponse);

    assertThat(controller.getStatusReport(null, null).getBody()).isEqualTo(statusReportResponse);

    verify(service).getStatusReport(null, null);
    verify(converter).toResponseDto(statusReportData);
  }

  @Test
  void shouldReturnConvertedStatusReportWithDateFilters() {
    var startDate = LocalDate.of(2024, 8, 1);
    var endDate = LocalDate.of(2024, 8, 31);
    var statusReportData = mock(StatusReport.class);
    var statusReportResponse = mock(StatusReportDTO.class);

    when(service.getStatusReport(startDate, endDate)).thenReturn(statusReportData);
    when(converter.toResponseDto(statusReportData)).thenReturn(statusReportResponse);

    assertThat(controller.getStatusReport(startDate, endDate).getBody())
        .isEqualTo(statusReportResponse);

    verify(service).getStatusReport(startDate, endDate);
    verify(converter).toResponseDto(statusReportData);
  }
}
