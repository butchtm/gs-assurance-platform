package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@Entity
@ToString
public class RequestMonitoringPeriod implements MonitoringPeriod {

  @Id @With private final Integer requestId;
  private final Integer periodId;
  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;
  private final Integer feeSchedule;

  @Override
  public Integer getId() {
    return periodId;
  }
}
