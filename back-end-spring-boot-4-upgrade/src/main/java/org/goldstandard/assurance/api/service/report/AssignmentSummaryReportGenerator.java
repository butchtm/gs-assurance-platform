package org.goldstandard.assurance.api.service.report;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryReport;
import org.goldstandard.assurance.api.exception.ReportGenerationException;
import org.goldstandard.assurance.api.service.AssignmentSummaryFinder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignmentSummaryReportGenerator {

  private final AssignmentSummaryFilenameGenerator filenameGenerator;
  private final AssignmentSummaryReportFactoryProvider reportFactoryProvider;
  private final AssignmentSummaryReportWriterBuilder writerBuilder;

  public AssignmentSummaryReport generate(
      AssignmentSummaryFiltersDTO filters,
      Pageable pageable,
      AssignmentSummaryFinder summaryFinder) {
    log.info("generating report using filters {}", filters);
    try {
      var reportFactory = reportFactoryProvider.getReportFactory(summaryFinder.getType());
      var writerParams =
          AssignmentSummaryReportWriterParams.builder()
              .summaryFinder(summaryFinder)
              .factory(reportFactory)
              .filters(filters)
              .originalPageable(pageable)
              .build();
      var writer = writerBuilder.buildWriter(writerParams);
      var data = writer.writeToByteArray();
      var filename = filenameGenerator.generateFilename(filters);

      return AssignmentSummaryReport.builder().data(data).filename(filename).build();
    } catch (IOException e) {
      throw new ReportGenerationException("Failed to generate report", e);
    }
  }
}
