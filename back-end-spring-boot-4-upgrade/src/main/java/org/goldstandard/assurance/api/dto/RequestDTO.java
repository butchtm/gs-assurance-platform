package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestCancellationReason;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class RequestDTO {

  private final Integer id;
  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;
  private final ZonedDateTime bookingDate;
  private final RequestType requestType;
  private final Integer index;
  private final String reviewName;
  private final Integer sameReviewTypeCount;
  private final Integer reviewDocumentStatus;
  private final CreditingPeriodDTO creditingPeriod;
  private final Integer emissionReductionAmount;
  private final Boolean manuallyAdded;
  private final ProjectDTO project;
  private final RequestStatus requestStatus;
  private final Integer currentRoundIndex;
  private final List<RequestRoundDTO> requestRounds;
  private final Integer batchId;
  private final Integer batchTypeId;
  private final Boolean migrated;
  private final Integer invoiceId;
  private final Collection<RequestStatusHistoryDTO> requestStatusHistory;
  private final boolean inStakeholderConsultation;
  private final List<Integer> legacyBatchProjectIds;
  private final Collection<RequestMethodologyDTO> methodologies;
  private final Boolean fastTracked;
  private final Boolean internal;
  private final RequestActivityDTO latestRequestActivity;
  private final MonitoringPeriodDTO monitoringPeriod;
  private final RequestConversationDTO conversation;
  private final RequestCancellationReason cancellationReason;
  private final String otherCancellationReason;
}
