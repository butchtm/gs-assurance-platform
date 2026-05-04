package org.goldstandard.assurance.api.service.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.service.AssignmentSummaryFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class AssignmentSummaryReportWriter {

  private final AssignmentSummaryFinder summaryFinder;
  private final AssignmentSummaryReportFactory factory;
  private final AssignmentSummaryFiltersDTO filters;
  private final Pageable originalPageable;

  public byte[] writeToByteArray() throws IOException {
    try (var workbook = new SXSSFWorkbook();
        var outputStream = new ByteArrayOutputStream()) {
      var worksheetFactory = factory.worksheetFactory();
      var rowPopulator = factory.rowPopulator();

      var worksheet = worksheetFactory.buildWorkSheet(workbook);
      int row = 1;
      Pageable pageable = PageRequest.of(0, 100, originalPageable.getSort());
      Page<AssignmentSummaryDTO> currentPage;
      do {
        currentPage = summaryFinder.getAssignmentSummaryPage(filters, pageable);
        log.debug("writing page {} of {}", currentPage.getNumber(), currentPage.getTotalPages());
        var summaries = currentPage.getContent();
        for (var summary : summaries) {
          rowPopulator.populateSummaryRow(worksheet.createRow(row++), summary);
        }
        pageable = currentPage.nextPageable();
      } while (currentPage.hasNext());

      rowPopulator.populateTotalRow(
          worksheet.createRow(row), summaryFinder.getAssignmentsSummaryTotals(filters));

      worksheetFactory.autoSizeAllColumns(worksheet);
      workbook.writeAvoidingTempFiles(outputStream);
      log.info("report written successfully");

      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
