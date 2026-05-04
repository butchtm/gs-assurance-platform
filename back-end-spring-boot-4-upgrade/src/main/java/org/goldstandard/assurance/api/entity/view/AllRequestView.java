package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.DummyNullDateFilter;

@Entity(name = "v_all_requests")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
public class AllRequestView {

  private final int projectId;
  private final String projectGsid;
  private final String projectName;
  private final Integer projectCarbonStreamId;
  private final Integer projectSizeId;
  private final int projectAccountId;
  private final String projectAccountName;
  private final Boolean projectRisk;
  private final String projectRiskSummary;

  @Id private final int requestId;
  private final String requestName;
  private final int requestTypeId;
  private final String requestTypeDescription;
  private final Integer batchId;
  private final Integer batchSize;
  private final int requestStatusId;
  private final String requestStatusDescription;
  private final Integer requestCurrentRoundIndex;
  private final ZonedDateTime requestRoundStartDate;
  private final ZonedDateTime requestRoundEndDate;
  private final Integer requestEmissionReductionAmount;

  private final Integer invoiceId;
  private final Integer invoiceStatusId;
  private final String invoiceStatus;

  private final Integer vvbAccountId;
  private final String vvbAccountName;
  private final Integer reviewerAccountId;
  private final String reviewerAccountName;

  private final Integer latestActivityId;
  private final String latestActivityName;
  private final ZonedDateTime latestActivityPlannedEndDate;
  private final Integer latestActivityResponsibleAccountId;
  private final String latestActivityResponsibleAccountName;

  private final ZonedDateTime lastRequestStatusChangeDate;

  private final Integer submittedById;
  private final String submittedByGivenName;
  private final String submittedByFamilyName;
  private final String submittedByEmailAddress;

  private final Integer qaqcAccountId;
  private final String qaqcAccountName;

  public AllRequestView removeDummyNullDates(DummyNullDateFilter filter) {
    var builder = toBuilder();
    Optional.ofNullable(requestRoundStartDate)
        .ifPresent(d -> builder.requestRoundStartDate(filter.apply(d)));
    Optional.ofNullable(requestRoundEndDate)
        .ifPresent(d -> builder.requestRoundEndDate(filter.apply(d)));
    Optional.ofNullable(latestActivityPlannedEndDate)
        .ifPresent(d -> builder.latestActivityPlannedEndDate(filter.apply(d)));
    Optional.ofNullable(lastRequestStatusChangeDate)
        .ifPresent(d -> builder.lastRequestStatusChangeDate(filter.apply(d)));
    return builder.build();
  }
}
