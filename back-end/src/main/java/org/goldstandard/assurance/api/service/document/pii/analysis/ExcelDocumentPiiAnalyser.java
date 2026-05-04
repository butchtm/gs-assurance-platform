package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.goldstandard.assurance.api.util.DocumentUtils.EXCEL_XLSX;

import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.goldstandard.assurance.api.service.document.DocumentStoreService;
import org.goldstandard.assurance.api.service.document.excel.ExcelSheetExtractor;
import org.goldstandard.assurance.api.service.document.excel.ExcelTextExtractor;
import org.goldstandard.assurance.api.service.document.excel.ExcelWorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelDocumentPiiAnalyser implements DocumentPiiAnalyser {

  @Value("${comprehend.detectEntitiesCharLimit:90000}")
  private int comprehendLimit;

  private final DocumentStoreService documentStoreService;
  private final ExcelWorkbookFactory workbookFactory;
  private final ExcelSheetExtractor sheetExtractor;
  private final ExcelTextExtractor textExtractor;
  private final ComprehendService comprehendService;

  @Override
  public Collection<ProbablePii> analyse(DocumentPiiAnalysisParams params) {
    try (var document = documentStoreService.retrieveDocumentAsStream(params.getS3Key());
        var workbook = workbookFactory.create(document)) {
      return sheetExtractor.extractSheets(workbook).stream()
          .map(sheet -> textExtractor.extractTextChunked(sheet, comprehendLimit))
          .map(comprehendService::detectDocumentEntities)
          .flatMap(Collection::stream)
          .toList();
    } catch (IOException e) {
      throw new DocumentPiiScanException(
          "Error during PII scanning of Excel document: " + e.getMessage());
    }
  }

  @Override
  public AnalysisStartResult startComprehendAnalysis(DocumentPiiAnalysisParams params) {
    log.error("startComprehendAnalysis method not supported for ExcelDocumentPiiAnalyser");
    return null;
  }

  @Override
  public boolean supported(DocumentPiiScanCriteria criteria) {
    return EXCEL_XLSX.equals(criteria.getContentType());
  }

  @Override
  public boolean supportsAsyncComprehend() {
    return false;
  }
}
