package org.goldstandard.assurance.api.service.document.excel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelSheetExtractor {

  public List<Sheet> extractSheets(Workbook workbook) {
    var sheets = new ArrayList<Sheet>();
    for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
      sheets.add(workbook.getSheetAt(sheetIndex));
    }
    return sheets;
  }
}
