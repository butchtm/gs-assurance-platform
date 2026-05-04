package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;

@Builder
@Getter
public class AssignedRequestDTO {

  private final int projectId;
  private final String gsid;
  private final String projectName;
  private final int projectAccountId;
  private final String projectAccountName;
  private final Boolean projectRisk;
  private final String projectRiskSummary;
  private final CarbonStream projectCarbonStream;
  private final ProjectSize projectSize;
  private final List<ProjectType> projectTypes;
  private final List<ProjectLocation> projectLocations;

  private final int requestId;
  private final String requestName;
  private Integer batchId;
  private Integer batchSize;
  private final int requestStatusId;
  private final String requestStatus;
  private final Integer requestTypeId;
  private final String requestTypeDescription;
  private final Integer currentRoundIndex;
  private final ZonedDateTime currentRoundStartDate;
  private final ZonedDateTime currentRoundEndDate;
  private final Integer estimatedAnnualEmissionReductionAmount;
  private final Integer invoiceId;
  private final Integer invoiceStatusId;
  private final String invoiceStatus;

  private final List<RequestRound> requestRounds;

  private final int assignmentId;
  private final String assignmentStatus;
  private final ZonedDateTime assignmentCreatedDate;
  private final ZonedDateTime dueDate;

  private final Integer vvbAccountId;
  private final String vvbAccountName;
  private final Integer reviewerAccountId;
  private final String reviewerAccountName;

  private final Integer latestActivityId;
  private final String latestActivityName;
  private final ZonedDateTime latestActivityPlannedEndDate;
  private final Integer latestActivityResponsibleAccountId;
  private final String latestActivityResponsibleAccountName;

  private final Integer submittedById;
  private final ZonedDateTime lastRequestStatusChangeDate;
  private final String submittedByGivenName;
  private final String submittedByFamilyName;
  private final String submittedByEmailAddress;
  private final Integer unreadMessages;
}
