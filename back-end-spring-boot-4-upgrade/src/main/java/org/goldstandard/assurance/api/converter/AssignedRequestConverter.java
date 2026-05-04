package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignedRequestDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.RequestFieldAccessPolicy;
import org.goldstandard.assurance.api.service.conversation.UnreadMessageFinder;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssignedRequestConverter {

  private final RequestFieldAccessPolicy policy;
  private final AuthenticatedUserSupplier userSupplier;
  private final PermissionsService permissionsService;
  private final UnreadMessageFinder unreadMessagesFinder;

  public PagedResource<AssignedRequestDTO> toPagedResource(Page<AssignedRequestView> page) {
    return new PagedResource<>(page.map(this::toDTO));
  }

  private AssignedRequestDTO toDTO(AssignedRequestView request) {
    var dto =
        AssignedRequestDTO.builder()
            .projectId(request.getProjectId())
            .gsid(request.getProjectGsid())
            .projectName(request.getProjectName())
            .projectAccountId(request.getProjectAccountId())
            .projectAccountName(request.getProjectAccountName())
            .projectCarbonStream(request.getProjectCarbonStream())
            .projectSize(request.getProjectSize())
            .projectTypes(request.getProjectTypes())
            .projectLocations(request.getProjectLocations())
            .requestId(request.getRequestId())
            .requestName(request.getRequestName())
            .batchId(request.getBatchId())
            .batchSize(request.getBatchSize())
            .requestStatusId(request.getRequestStatusId())
            .requestStatus(request.getRequestStatusDescription())
            .requestTypeId(request.getRequestTypeId())
            .requestTypeDescription(request.getRequestTypeDescription())
            .currentRoundIndex(request.getCurrentRoundIndex())
            .currentRoundStartDate(request.getCurrentRoundStartDate())
            .currentRoundEndDate(request.getCurrentRoundEndDate())
            .estimatedAnnualEmissionReductionAmount(request.getRequestEmissionReductionAmount())
            .lastRequestStatusChangeDate(request.getLastRequestStatusChangeDate())
            .invoiceId(request.getInvoiceId())
            .invoiceStatusId(request.getInvoiceStatusId())
            .invoiceStatus(request.getInvoiceStatusDescription())
            .requestRounds(request.getRequestRounds())
            .assignmentId(request.getAssignmentId())
            .assignmentStatus(request.getAssignmentStatusDescription())
            .assignmentCreatedDate(request.getAssignmentCreatedDate())
            .dueDate(request.getAssignmentDueDate())
            .vvbAccountId(request.getVvbAccountId())
            .vvbAccountName(request.getVvbAccountName())
            .reviewerAccountId(request.getReviewerAccountId())
            .reviewerAccountName(request.getReviewerAccountName());
    if (policy.canViewSubmittedBy()) {
      dto.submittedById(request.getSubmittedById())
          .submittedByGivenName(request.getSubmittedByGivenName())
          .submittedByFamilyName(request.getSubmittedByFamilyName())
          .submittedByEmailAddress(request.getSubmittedByEmailAddress());
    }
    if (policy.canViewActivity()) {
      dto.latestActivityName(request.getLatestActivityName())
          .latestActivityPlannedEndDate(request.getLatestActivityPlannedEndDate())
          .latestActivityResponsibleAccountId(request.getLatestActivityResponsibleAccountId())
          .latestActivityResponsibleAccountName(request.getLatestActivityResponsibleAccountName())
          .build();
    }
    if (policy.canViewProjectRisk()) {
      dto.projectRisk(request.getProjectRisk()).projectRiskSummary(request.getProjectRiskSummary());
    }
    if (permissionsService.isArmOrVvb()) {
      var user = userSupplier.forceGetAuthenticatedUser();
      dto.unreadMessages(
          unreadMessagesFinder.getUnreadMessageCountForUserAndRequest(
              user.getId(), request.getRequestId()));
    }
    return dto.build();
  }
}
