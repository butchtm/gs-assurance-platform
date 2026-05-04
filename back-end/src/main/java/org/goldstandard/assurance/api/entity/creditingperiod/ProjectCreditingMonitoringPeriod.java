package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Entity
@IdClass(ProjectCreditingMonitoringPeriodId.class)
public class ProjectCreditingMonitoringPeriod implements MonitoringPeriod {

  @Id
  @Column(name = "id")
  private final Integer id;

  @Id
  @Column(name = "project_id")
  @With
  private final Integer projectId;

  @Id
  @Column(name = "period_id")
  private final Integer periodId;

  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;

  private final Integer originRequestId;

  @With private final Integer feeSchedule;
}
