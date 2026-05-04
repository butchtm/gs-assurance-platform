package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.RequiredDocumentStatus;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;

@Setter
@Getter
public class ProjectDeveloperRequestDTO {
  private Integer id;
  private String gsid;
  private String projectName;
  private List<ProjectType> projectTypes;
  private List<ProjectLocation> projectLocations;
  private CarbonStream carbonStream;
  private String requestName;
  private InvoiceStatus invoiceStatus;
  private RequestStatus currentRequestStatus;
  private Integer currentRoundIndex;
  private ZonedDateTime currentRoundEndDate;
  private boolean isBatched;
  private RequiredDocumentStatus requiredDocumentStatus;
  private Integer vvbAccountId;
  private String vvbAccountName;
  private String activityName;
  private ZonedDateTime plannedDeadlineDate;
  private ZonedDateTime lastRequestStatusChangeDate;
  private Integer unreadMessages;
}
