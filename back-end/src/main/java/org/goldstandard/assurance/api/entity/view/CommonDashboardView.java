package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.creditingperiod.MonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "v_common_dashboard")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
public class CommonDashboardView {
  @Id private final Integer requestId;
  private final String requestName;
  private final Integer requestTypeId;
  private final Integer requestStatusId;
  private final ZonedDateTime requestStartDate;
  private final ZonedDateTime requestEndDate;
  private final Integer currentRoundIndex;
  private final ZonedDateTime firstRoundStartDate;
  private final ZonedDateTime currentRoundEndDate;
  private final Integer requestCreditingPeriodId;
  private final Integer requestMonitoringPeriodId;
  private final Integer requestMonitoringPeriodFeeScheduleId;
  private final Integer invoiceStatusId;
  private final Integer projectId;

  @Column(name = "gsid")
  private final String projectGsid;

  private final String projectName;
  private final Integer projectStatusId;
  private final Integer projectClassificationId;
  private final String masterGsid;
  private final Integer parentId;
  private final String parentGsid;
  private final Integer parentClassificationId;
  private final String projectTypesString;
  private final Integer projectFeeScheduleId;
  private final Integer projectCreditingMonitoringPeriodFeeScheduleId;
  private final String accountName;

  @ManyToOne
  @JoinColumn(
      name = "requestTypeId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final RequestType requestType;

  @ManyToOne
  @JoinColumn(
      name = "requestStatusId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final RequestStatus requestStatus;

  @ManyToOne
  @JoinColumn(
      name = "invoiceStatusId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final InvoiceStatus invoiceStatus;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_type",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "project_type_id", referencedColumnName = "id"))
  private final Collection<ProjectType> projectTypes;

  @ManyToOne
  @JoinColumn(
      name = "projectStatusId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final ProjectStatus projectStatus;

  @ManyToOne
  @JoinColumn(
      name = "projectClassificationId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final ProjectClassification projectClassification;

  @ManyToOne
  @JoinColumn(
      name = "parentClassificationId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final ProjectClassification parentClassification;

  @ManyToOne
  @JoinColumn(
      name = "projectFeeScheduleId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final FeeSchedule projectFeeSchedule;

  @ManyToOne
  @JoinColumn(
      name = "requestId",
      referencedColumnName = "requestId",
      insertable = false,
      updatable = false)
  private final RequestMonitoringPeriod requestMonitoringPeriod;

  @ManyToOne
  @JoinColumn(
      name = "projectId",
      referencedColumnName = "project_id",
      insertable = false,
      updatable = false)
  @JoinColumn(
      name = "requestCreditingPeriodId",
      referencedColumnName = "period_id",
      insertable = false,
      updatable = false)
  @JoinColumn(
      name = "requestMonitoringPeriodId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final ProjectCreditingMonitoringPeriod projectCreditingMonitoringPeriod;

  @ManyToOne
  @JoinColumn(
      name = "requestMonitoringPeriodFeeScheduleId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final FeeSchedule requestMonitoringPeriodFeeSchedule;

  @ManyToOne
  @JoinColumn(
      name = "projectCreditingMonitoringPeriodFeeScheduleId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final FeeSchedule projectCreditingMonitoringPeriodFeeSchedule;

  public String getRealCaseGsid() {
    if (parentClassification != null && parentClassification.isRealCaseVpa()) {
      return parentGsid;
    }
    return null;
  }

  public CommonDashboardView removeDummyNullDates(UnaryOperator<ZonedDateTime> filter) {
    var builder = toBuilder();
    Optional.ofNullable(requestStartDate).ifPresent(d -> builder.requestStartDate(filter.apply(d)));
    Optional.ofNullable(requestEndDate).ifPresent(d -> builder.requestEndDate(filter.apply(d)));
    Optional.ofNullable(firstRoundStartDate)
        .ifPresent(d -> builder.firstRoundStartDate(filter.apply(d)));
    Optional.ofNullable(currentRoundEndDate)
        .ifPresent(d -> builder.currentRoundEndDate(filter.apply(d)));
    return builder.build();
  }

  public Collection<ProjectType> getProjectTypes() {
    return Optional.ofNullable(projectTypes).orElse(Collections.emptyList());
  }

  public MonitoringPeriod getMonitoringPeriod() {
    if (Objects.nonNull(requestMonitoringPeriod)) {
      return requestMonitoringPeriod;
    }
    return projectCreditingMonitoringPeriod;
  }

  public FeeSchedule getRequestFeeSchedule() {
    if (Objects.nonNull(requestMonitoringPeriodFeeSchedule)) {
      return requestMonitoringPeriodFeeSchedule;
    }
    return projectCreditingMonitoringPeriodFeeSchedule;
  }
}
