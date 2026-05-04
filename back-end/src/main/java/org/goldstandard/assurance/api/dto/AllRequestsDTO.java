package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;

@Builder
@Getter
public class AllRequestsDTO {

  private final int projectId;
  private final String gsid;
  private final String projectName;
  private final CarbonStream projectCarbonStream;
  private final ProjectSize projectSize;
  private final int projectAccountId;
  private final String projectAccountName;
  private final Boolean projectRisk;
  private final String projectRiskSummary;

  private final int requestId;
  private final String requestName;
  private final int requestTypeId;
  private final String requestTypeDescription;
  private Integer batchId;
  private Integer batchSize;
  private final int requestStatusId;
  private final String requestStatus;
  private final Integer currentRoundIndex;
  private final ZonedDateTime currentRoundStartDate;
  private final ZonedDateTime currentRoundEndDate;
  private final Integer estimatedAnnualEmissionReductionAmount;
  private final Integer invoiceId;
  private final Integer invoiceStatusId;
  private final String invoiceStatus;

  private final Integer vvbAccountId;
  private final String vvbAccountName;
  private final Integer reviewerAccountId;
  private final String reviewerAccountName;
  private final Integer qaqcAccountId;
  private final String qaqcAccountName;
  private final Integer qaqcAccountUnreadMessages;

  private final Integer activityId;
  private final String activityName;
  private final ZonedDateTime plannedEndDate;
  private final Integer responsibleAccountId;
  private final String responsibleAccountName;

  private final Integer submittedById;
  private final ZonedDateTime lastRequestStatusChangeDate;
  private final String submittedByGivenName;
  private final String submittedByFamilyName;
  private final String submittedByEmailAddress;
}
