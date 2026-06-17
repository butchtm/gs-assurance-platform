package org.goldstandard.assurance.api.service.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.junit.jupiter.api.Test;

class CommonDashboardReportRowPopulatorTest {

  private final CommonDashboardReportRowPopulator populator =
      new CommonDashboardReportRowPopulator();

  @Test
  void shouldPopulateCrossLinkClassificationAsCanonicalDisplayLabel() {
    var view =
        CommonDashboardView.builder()
            .projectClassification(
                classification(
                    ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
                    ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE,
                    "Real Case VPA - Cross Linking"))
            .build();

    var cells = new HashMap<Integer, SXSSFCell>();
    var row = mock(SXSSFRow.class);
    when(row.createCell(anyInt()))
        .thenAnswer(
            invocation -> {
              var columnIndex = invocation.getArgument(0, Integer.class);
              var cell = mock(SXSSFCell.class);
              cells.put(columnIndex, cell);
              return cell;
            });

    populator.populateDataRow(row, view);

    assertThat(cells).containsKey(10);
    verify(cells.get(10)).setCellValue(ProjectClassification.REAL_CASE_VPA_DESCRIPTION);
  }

  private static ProjectClassification classification(Integer id, String code, String description) {
    var classification = new ProjectClassification();
    classification.setId(id);
    classification.setCode(code);
    classification.setDescription(description);
    return classification;
  }
}
