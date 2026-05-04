package org.goldstandard.assurance.api.service.report;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportKeyFactory {

  private final Clock clock;
  private final DateTimeFormatter dateTimeFormatter;

  @Autowired
  public ReportKeyFactory(Clock clock) {
    this(clock, DateTimeFormatter.ofPattern("ddMMyyyy-HHmmssSSS"));
  }

  public String buildCommonDashboardKey(User user) {
    var now = ZonedDateTime.now(clock).withZoneSameInstant(ZoneId.of("UTC"));
    var dateTime = dateTimeFormatter.format(now);
    return String.format("%s/common-dashboard-report-%s.xlsx", user.getUserName(), dateTime);
  }
}
