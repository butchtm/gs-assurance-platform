package org.goldstandard.assurance.api.service.document.excel;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class ExcelWorkbookFactory {

  public Workbook create(InputStream input) throws IOException {
    return WorkbookFactory.create(input);
  }
}
