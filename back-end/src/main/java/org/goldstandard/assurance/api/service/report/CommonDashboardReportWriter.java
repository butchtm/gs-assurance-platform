package org.goldstandard.assurance.api.service.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.goldstandard.assurance.api.service.CommonDashboardFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class CommonDashboardReportWriter implements FileWriter {

  private final CommonDashboardFinder finder;
  private final CommonDashboardRequestFiltersDTO filters;
  private final Pageable originalPageable;
  private final CommonDashboardReportWorksheetFactory factory;
  private final CommonDashboardReportRowPopulator rowPopulator;

  public CommonDashboardReportWriter(
      CommonDashboardFinder finder, CommonDashboardRequestFiltersDTO filters, Pageable pageable) {
    this(
        finder,
        filters,
        pageable,
        new CommonDashboardReportWorksheetFactory(),
        new CommonDashboardReportRowPopulator());
  }

  @Override
  public void write(OutputStream outputStream) {
    log.info("writing report");
    // the param 100 here specifies the number of rows held in memory
    // before flushing to the sheet
    // https://poi.apache.org/apidocs/dev/org/apache/poi/xssf/streaming/SXSSFWorkbook.html#SXSSFWorkbook-int-
    try (var workbook = new SXSSFWorkbook(100)) {
      var worksheet = factory.buildWorksheet(workbook);
      int row = 1;
      Pageable pageable = PageRequest.of(0, 100, originalPageable.getSort());
      Page<CommonDashboardView> currentPage;
      do {
        currentPage = finder.getCommonDashboardPage(filters, pageable);
        log.debug("writing page {} of {}", currentPage.getNumber(), currentPage.getTotalPages());
        var requests = currentPage.getContent();
        for (var request : requests) {
          rowPopulator.populateDataRow(worksheet.createRow(row++), request);
        }
        pageable = currentPage.nextPageable();
      } while (currentPage.hasNext());
      factory.autoSizeAllColumns(worksheet);
      workbook.writeAvoidingTempFiles(outputStream);
      log.info("report written successfully");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
