package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AllRequestsDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.view.AllRequestView;
import org.goldstandard.assurance.api.service.RequestFieldAccessPolicy;
import org.goldstandard.assurance.api.service.conversation.UnreadMessageFinder;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AllRequestConverter {

  private final RequestFieldAccessPolicy policy;
  private final UserFinder userFinder;
  private final UnreadMessageFinder unreadMessagesFinder;

  public PagedResource<AllRequestsDTO> toPagedResource(Page<AllRequestView> page) {
    var accountIds =
        page.map(AllRequestView::getQaqcAccountId).stream().collect(Collectors.toSet());
    var usersByQaqcAccountId = userFinder.getUsersByAccountIds(accountIds);
    return new PagedResource<>(page.map(request -> toDTO(request, usersByQaqcAccountId)));
  }

  private AllRequestsDTO toDTO(
      AllRequestView request, Map<Integer, List<User>> usersByQaqcAccountId) {
    var qaqcAccountUserIds =
        Optional.ofNullable(request.getQaqcAccountId()).map(usersByQaqcAccountId::get).stream()
            .flatMap(Collection::stream)
            .map(User::getId)
            .collect(Collectors.toSet());

    var dto =
        AllRequestsDTO.builder()
            .projectId(request.getProjectId())
            .gsid(request.getProjectGsid())
            .projectName(request.getProjectName())
            .projectAccountId(request.getProjectAccountId())
            .projectAccountName(request.getProjectAccountName())
            .requestId(request.getRequestId())
            .requestName(request.getRequestName())
            .requestTypeId(request.getRequestTypeId())
            .requestTypeDescription(request.getRequestTypeDescription())
            .batchId(request.getBatchId())
            .batchSize(request.getBatchSize())
            .requestStatusId(request.getRequestStatusId())
            .requestStatus(request.getRequestStatusDescription())
            .currentRoundIndex(request.getRequestCurrentRoundIndex())
            .currentRoundStartDate(request.getRequestRoundStartDate())
            .currentRoundEndDate(request.getRequestRoundEndDate())
            .estimatedAnnualEmissionReductionAmount(request.getRequestEmissionReductionAmount())
            .lastRequestStatusChangeDate(request.getLastRequestStatusChangeDate())
            .invoiceId(request.getInvoiceId())
            .invoiceStatusId(request.getInvoiceStatusId())
            .invoiceStatus(request.getInvoiceStatus())
            .vvbAccountId(request.getVvbAccountId())
            .vvbAccountName(request.getVvbAccountName())
            .reviewerAccountId(request.getReviewerAccountId())
            .reviewerAccountName(request.getReviewerAccountName())
            .submittedById(request.getSubmittedById())
            .submittedByGivenName(request.getSubmittedByGivenName())
            .submittedByFamilyName(request.getSubmittedByFamilyName())
            .submittedByEmailAddress(request.getSubmittedByEmailAddress())
            .qaqcAccountId(request.getQaqcAccountId())
            .qaqcAccountName(request.getQaqcAccountName());

    if (policy.canViewProjectRisk()) {
      dto.projectRisk(request.getProjectRisk()).projectRiskSummary(request.getProjectRiskSummary());
    }
    var requestStatus = new RequestStatus(request.getRequestStatusId());
    if (!requestStatus.isClosed()) {
      dto.qaqcAccountUnreadMessages(
          unreadMessagesFinder.getUnreadMessageCountForUsersAndRequest(
              qaqcAccountUserIds, request.getRequestId()));
    }
    if (policy.canViewActivity()) {
      return dto.activityId(request.getLatestActivityId())
          .activityName(request.getLatestActivityName())
          .plannedEndDate(request.getLatestActivityPlannedEndDate())
          .responsibleAccountId(request.getLatestActivityResponsibleAccountId())
          .responsibleAccountName(request.getLatestActivityResponsibleAccountName())
          .build();
    }
    return dto.build();
  }
}
