package org.goldstandard.assurance.api.service.report;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ExcelStatusReport;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.goldstandard.assurance.api.exception.ReportGenerationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusReportGenerator {

  private final StatusReportFileNameGenerator fileNameGenerator;

  public ExcelStatusReport toExcel(StatusReport statusReport) {
    log.info(
        "Generating Status Report Excel with date range: {} to {}",
        statusReport.getFilterStartDate(),
        statusReport.getFilterEndDate());

    try {
      var writer = new StatusReportWriter(statusReport);
      var data = writer.writeToByteArray();
      var filename = fileNameGenerator.generateFilename(statusReport);

      return ExcelStatusReport.builder().data(data).filename(filename).build();
    } catch (IOException e) {
      throw new ReportGenerationException("Failed to generate Excel report", e);
    }
  }
}
