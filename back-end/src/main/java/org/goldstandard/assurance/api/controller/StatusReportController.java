package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.util.DocumentUtils.EXCEL_XLSX;

import jakarta.annotation.security.RolesAllowed;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.StatusReportDTO;
import org.goldstandard.assurance.api.service.StatusReportConverter;
import org.goldstandard.assurance.api.service.StatusReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatusReportController {

  private final StatusReportService service;
  private final StatusReportConverter converter;
  private final DownloadResponseHeaderPopulator headerPopulator;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @GetMapping("/status-report")
  public ResponseEntity<StatusReportDTO> getStatusReport(
      @RequestParam(name = "startDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDate filterStartDate,
      @RequestParam(name = "endDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDate filterEndDate) {
    var statusReportData = service.getStatusReport(filterStartDate, filterEndDate);
    return ResponseEntity.ok(converter.toResponseDto(statusReportData));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @GetMapping("/status-report/excel")
  public ResponseEntity<byte[]> getStatusReportExcel(
      @RequestParam(name = "startDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDate filterStartDate,
      @RequestParam(name = "endDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDate filterEndDate) {

    var excelReport = service.getExcelStatusReport(filterStartDate, filterEndDate);

    return headerPopulator
        .populate(ResponseEntity.ok(), excelReport.getFilename())
        .contentType(MediaType.parseMediaType(EXCEL_XLSX))
        .body(excelReport.getData());
  }
}
