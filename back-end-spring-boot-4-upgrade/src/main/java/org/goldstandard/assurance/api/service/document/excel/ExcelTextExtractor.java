package org.goldstandard.assurance.api.service.document.excel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.springframework.stereotype.Component;

@Component
public class ExcelTextExtractor {

  public List<ExtractedText> extractTextChunked(Sheet sheet, int chunkSize) {
    var extractedTexts = new ArrayList<ExtractedText>();

    var chunker = new SheetTextChunker();
    for (Row row : sheet) {
      for (Cell cell : row) {
        if (chunker.addingNewCellWillExceedChunkSize(cell, chunkSize)) {
          extractedTexts.add(chunker.completeChunk());
        }
        chunker.addCell(cell);
      }
    }
    if (chunker.hasChunk()) {
      extractedTexts.add(chunker.completeChunk());
    }
    return extractedTexts;
  }
}
