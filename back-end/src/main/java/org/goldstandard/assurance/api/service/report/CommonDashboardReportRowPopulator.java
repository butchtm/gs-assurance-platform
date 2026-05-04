package org.goldstandard.assurance.api.service.report;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;

@RequiredArgsConstructor
@Slf4j
public class CommonDashboardReportRowPopulator {

  public void populateDataRow(SXSSFRow row, CommonDashboardView view) {
    int columnIndex = 0;
    stringCell(row, columnIndex++, view.getProjectGsid());
    stringCell(row, columnIndex++, view.getProjectName());
    stringCell(row, columnIndex++, toDescriptionIfPresent(view.getRequestStatus()));
    stringCell(row, columnIndex++, view.getRequestName());
    stringCell(row, columnIndex++, view.getMasterGsid());
    stringCell(row, columnIndex++, view.getParentGsid());
    dateTimeCell(row, columnIndex++, view.getFirstRoundStartDate());
    dateTimeCell(row, columnIndex++, view.getCurrentRoundEndDate());
    numberCell(row, columnIndex++, view.getCurrentRoundIndex());
    dateTimeCell(row, columnIndex++, view.getRequestEndDate());
    stringCell(row, columnIndex++, toDescriptionIfPresent(view.getProjectClassification()));
    stringCell(row, columnIndex++, toDescriptionsString(view.getProjectTypes()));
    stringCell(row, columnIndex++, toDescriptionIfPresent(view.getInvoiceStatus()));
    numberCell(row, columnIndex++, view.getRequestId());
    stringCell(row, columnIndex++, toDescriptionIfPresent(view.getProjectFeeSchedule()));
    stringCell(row, columnIndex++, toDescriptionIfPresent(view.getRequestFeeSchedule()));
    stringCell(row, columnIndex, view.getAccountName());
  }

  private static void stringCell(SXSSFRow row, int columnIndex, String value) {
    var cell = row.createCell(columnIndex);
    cell.setCellValue(Optional.ofNullable(value).orElse(""));
  }

  private static void numberCell(SXSSFRow row, int columnIndex, Integer value) {
    var cell = row.createCell(columnIndex);
    cell.setCellValue(Optional.ofNullable(value).map(v -> Integer.toString(v)).orElse(""));
  }

  private static void dateTimeCell(SXSSFRow row, int columnIndex, ZonedDateTime dateTime) {
    var cell = row.createCell(columnIndex);
    var value =
        Optional.ofNullable(dateTime).map(sd -> sd.format(toDateTimeFormatter(cell))).orElse("");
    cell.setCellValue(value);
  }

  private static DateTimeFormatter toDateTimeFormatter(SXSSFCell cell) {
    var style = cell.getCellStyle();
    return DateTimeFormatter.ofPattern(style.getDataFormatString());
  }

  private static <T extends ReferenceDataType> String toDescriptionsString(Collection<T> types) {
    if (CollectionUtils.isEmpty(types)) {
      return "";
    }
    return types.stream().map(ReferenceDataType::getDescription).collect(Collectors.joining(","));
  }

  private static <T extends ReferenceDataType> String toDescriptionIfPresent(T type) {
    return Optional.ofNullable(type).map(ReferenceDataType::getDescription).orElse("");
  }
}
