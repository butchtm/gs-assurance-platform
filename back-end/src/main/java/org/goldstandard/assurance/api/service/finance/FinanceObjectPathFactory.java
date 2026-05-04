package org.goldstandard.assurance.api.service.finance;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinanceObjectPathFactory {

  private final Clock clock;
  private final String folderName;
  private final DateTimeFormatter dateFormatter;

  @Autowired
  public FinanceObjectPathFactory(Clock clock) {
    this(clock, "Inprocess", buildDateFormatter());
  }

  public String toSalesOrderPath(Integer requestId) {
    var timestamp = buildTimestamp();
    return String.format("%s/sales-order-%d-%s.xml", folderName, requestId, timestamp);
  }

  public String toProjectContractPath(String gsId) {
    var timestamp = buildTimestamp();
    return String.format("%s/project-contract-%s-%s.xml", folderName, gsId, timestamp);
  }

  private String buildTimestamp() {
    return dateFormatter.format(ZonedDateTime.now(clock));
  }

  private static DateTimeFormatter buildDateFormatter() {
    return DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss-SSSX").withZone(ZoneId.of("UTC"));
  }
}
