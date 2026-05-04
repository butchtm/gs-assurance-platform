package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.service.DummyNullDateFilter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "v_assigned_requests")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
public class AssignedRequestView {

  private final int projectId;
  private final String projectGsid;
  private final String projectName;
  private final int projectAccountId;
  private final String projectAccountName;
  private final Boolean projectRisk;
  private final String projectRiskSummary;
  @ManyToOne private final ProjectSize projectSize;
  @ManyToOne private final CarbonStream projectCarbonStream;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_type",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "project_type_id", referencedColumnName = "id"))
  private final List<ProjectType> projectTypes;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_location",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "project_location_id", referencedColumnName = "id"))
  private final List<ProjectLocation> projectLocations;

  @Fetch(FetchMode.JOIN)
  @OneToMany
  @JoinColumn(name = "requestId", updatable = false, insertable = false)
  private final List<RequestRound> requestRounds;

  private final int requestId;
  private final String requestName;
  private final int requestTypeId;
  private final String requestTypeDescription;
  private final Integer batchId;
  private final Integer batchSize;
  private final int requestStatusId;
  private final String requestStatusDescription;
  private final Integer currentRoundIndex;
  private final ZonedDateTime currentRoundStartDate;
  private final ZonedDateTime currentRoundEndDate;
  private final Integer requestEmissionReductionAmount;

  private final Integer invoiceId;
  private final Integer invoiceStatusId;
  private final String invoiceStatusDescription;

  @Id private final int assignmentId;
  private final int assignedAccountId;
  private final int assignmentTypeId;
  private final int assignmentStatusId;
  private final String assignmentStatusDescription;
  private final ZonedDateTime assignmentCreatedDate;
  private final ZonedDateTime assignmentDueDate;

  private final Integer vvbAccountId;
  private final String vvbAccountName;
  private final Integer reviewerAccountId;
  private final String reviewerAccountName;

  private final String latestActivityName;
  private final ZonedDateTime latestActivityPlannedEndDate;
  private final Integer latestActivityResponsibleAccountId;
  private final String latestActivityResponsibleAccountName;

  private final ZonedDateTime lastRequestStatusChangeDate;

  private final Integer submittedById;
  private final String submittedByGivenName;
  private final String submittedByFamilyName;
  private final String submittedByEmailAddress;

  public int getBatchSize() {
    return Optional.ofNullable(batchSize).orElse(1);
  }

  public AssignedRequestView removeDummyNullDates(DummyNullDateFilter filter) {
    var builder = toBuilder();
    Optional.ofNullable(currentRoundEndDate)
        .ifPresent(d -> builder.currentRoundEndDate(filter.apply(d)));
    Optional.ofNullable(assignmentCreatedDate)
        .ifPresent(d -> builder.assignmentCreatedDate(filter.apply(d)));
    Optional.ofNullable(latestActivityPlannedEndDate)
        .ifPresent(d -> builder.latestActivityPlannedEndDate(filter.apply(d)));
    Optional.ofNullable(lastRequestStatusChangeDate)
        .ifPresent(d -> builder.lastRequestStatusChangeDate(filter.apply(d)));
    return builder.build();
  }
}
