package org.goldstandard.assurance.api.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.dto.RequestRoundDTO;
import org.goldstandard.assurance.api.dto.RequestStatusHistoryDTO;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestMethodologyRepository;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.RequestFieldAccessPolicy;
import org.goldstandard.assurance.api.service.StakeholderConsultationFinder;
import org.goldstandard.assurance.api.service.activity.RequestActivityFinder;
import org.goldstandard.assurance.api.service.conversation.ConversationFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestConverter {

  private final StakeholderConsultationFinder stakeholderConsultationFinder;
  private final RequestStatusHistoryRepository requestStatusHistoryRepository;
  private final RequestMethodologyRepository requestMethodologyRepository;
  private final BatchRequestFinder batchRequestFinder;
  private final RequestFieldAccessPolicy policy;
  private final RequestActivityFinder activityFinder;
  private final RequestActivityConverter activityMapper;
  private final ProjectConverter projectConverter;
  private final ProjectCreditingPeriodRepository projectCreditingPeriodRepository;
  private final ProjectCreditingPeriodConverter projectCreditingPeriodConverter;
  private final MethodologyConverter methodologyConverter;
  private final ProjectCreditingMonitoringPeriodConverter monitoringPeriodConverter;
  private final ConversationFinder conversationFinder;
  private final ConversationConverter conversationConverter;

  public RequestDTO toRequestOverviewDTO(Request request) {
    return toRequestOverviewBuilder(request).build();
  }

  public RequestDTO toRequestDetailDTO(Request request, Map<Integer, Integer> unreadMessageCounts) {
    var count = Optional.ofNullable(unreadMessageCounts.get(request.getId())).orElse(0);
    return toRequestDetailDTO(request, count);
  }

  public RequestDTO toRequestDetailDTO(Request request, Integer unreadMessageCount) {
    var builder = toRequestOverviewBuilder(request);
    Optional.ofNullable(request.getInvoice())
        .ifPresent(invoice -> builder.invoiceId(invoice.getId()));
    builder.requestStatusHistory(getRequestHistoryDTO(request));
    builder.inStakeholderConsultation(stakeholderConsultationFinder.isInConsultation(request));
    builder.legacyBatchProjectIds(batchRequestFinder.getLegacyBatchProjectIds(request.getId()));
    builder.methodologies(getRequestMethodologies(request));
    builder.monitoringPeriod(monitoringPeriodConverter.toDto(request));
    if (policy.canViewFastTrackedFlag(request.getProject())) {
      builder.fastTracked(request.getFastTracked());
    }
    builder.internal(request.getInternal());
    builder.paa(request.isPaa());
    activityFinder
        .getLatestRequestActivity(request)
        .ifPresent(
            requestActivity ->
                builder.latestRequestActivity(activityMapper.toDto(requestActivity)));
    conversationFinder
        .findByRequestId(request.getId())
        .ifPresent(
            conversation ->
                builder.conversation(
                    conversationConverter.toRequestDto(conversation, unreadMessageCount)));
    if (request.getCancellationReason() != null) {
      builder.cancellationReason(request.getCancellationReason());
      if (request.getOtherCancellationReason() != null) {
        builder.otherCancellationReason(request.getOtherCancellationReason());
      }
    }
    return builder.build();
  }

  private List<RequestStatusHistoryDTO> getRequestHistoryDTO(Request request) {
    var history =
        requestStatusHistoryRepository.findByRequestIdOrderByChangedAtDesc(request.getId());
    if (CollectionUtils.isEmpty(history)) {
      return Collections.emptyList();
    }
    return history.stream().map(RequestStatusHistoryDTO::fromRequestStatusHistory).toList();
  }

  private Collection<RequestMethodologyDTO> getRequestMethodologies(Request request) {
    var methodologies = requestMethodologyRepository.getAllByRequestId(request.getId());
    return methodologyConverter.toRequestMethodologyDtos(methodologies);
  }

  private RequestDTO.RequestDTOBuilder toRequestOverviewBuilder(Request request) {
    return RequestDTO.builder()
        .id(request.getId())
        .bookingDate(request.getBookingDate())
        .creditingPeriod(toCreditingPeriodDto(request).orElse(null))
        .emissionReductionAmount(request.getEmissionReductionAmount())
        .endDate(request.getEndDate())
        .index(request.getIndex())
        .manuallyAdded(request.getManuallyAdded())
        .reviewDocumentStatus(request.getRequestDocumentStatus())
        .reviewName(request.getRequestName())
        .requestType(request.getRequestType())
        .sameReviewTypeCount(request.getSameRequestTypeCount())
        .startDate(request.getStartDate())
        .project(projectConverter.toReferenceDto(request.getProject()))
        .requestStatus(request.getCurrentRequestStatus())
        .currentRoundIndex(toCurrentRoundIndex(request))
        .requestRounds(toRequestRounds(request))
        .batchId(Optional.ofNullable(request.getBatch()).map(Batch::getId).orElse(null))
        .batchTypeId(
            Optional.ofNullable(request.getBatch())
                .map(Batch::getBatchType)
                .map(BatchType::getId)
                .orElse(null))
        .migrated(request.getMigrated());
  }

  private Optional<CreditingPeriodDTO> toCreditingPeriodDto(Request request) {
    var periodId = request.getCreditingPeriod();
    if (Objects.nonNull(periodId)) {
      return getProjectCreditingPeriodDto(request.getProject().getId(), periodId);
    }
    return request.getDraftCreditingPeriod().map(RequestConverter::toDto);
  }

  private Optional<CreditingPeriodDTO> getProjectCreditingPeriodDto(
      Integer projectId, Integer periodId) {
    return projectCreditingPeriodRepository
        .findByProjectIdAndPeriodId(projectId, periodId)
        // we clear the methodologies here as we only need period details (not methodology details)
        // against a request this can be removed when we store request methodologies within the
        // request crediting period
        .map(period -> period.withMethodologies(null))
        .map(projectCreditingPeriodConverter::toDto);
  }

  private static CreditingPeriodDTO toDto(RequestCreditingPeriod creditingPeriod) {
    return CreditingPeriodDTO.builder()
        .startDate(creditingPeriod.getStartDate())
        .endDate(creditingPeriod.getEndDate())
        .build();
  }

  private static Integer toCurrentRoundIndex(Request request) {
    var round = request.getParentIfChild();
    return round.getCurrentRoundIndex();
  }

  private static List<RequestRoundDTO> toRequestRounds(Request request) {
    var parentRequest = request.getParentIfChild();
    var rounds = Optional.ofNullable(parentRequest.getRequestRounds()).orElse(new ArrayList<>());
    return rounds.stream().map(RequestRoundDTO::fromRequestRound).toList();
  }
}
