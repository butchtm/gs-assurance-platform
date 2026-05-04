package org.goldstandard.assurance.api.entity.creditingperiod;

import java.time.ZonedDateTime;

public interface MonitoringPeriod {

  Integer getId();

  ZonedDateTime getStartDate();

  ZonedDateTime getEndDate();

  Integer getFeeSchedule();
}
