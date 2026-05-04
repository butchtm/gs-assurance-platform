package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryResponseDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.ArmAssignmentSummaryTotals;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummary;
import org.goldstandard.assurance.api.entity.assignmentsummary.RoeAssignmentSummaryTotals;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssignmentSummaryConverter {

  public AssignmentSummaryResponseDTO toAssignmentSummaryResponse(
      Page<AssignmentSummaryDTO> summary, AssignmentSummaryDTO totals) {
    return AssignmentSummaryResponseDTO.builder()
        .assignmentSummary(new PagedResource<>(summary))
        .assignmentTotals(totals)
        .build();
  }

  public AssignmentSummaryDTO toDto(RoeAssignmentSummary summary) {
    return AssignmentSummaryDTO.builder()
        .accountName(summary.getAccountName())
        .openRequests(summary.getOpenCount())
        .closedRequests(summary.getClosedCount())
        .roundOneRequests(summary.getFirstRoundCount())
        .inProgressRequests(summary.getInProgressCount())
        .dueRequests(summary.getDueCount())
        .overdueRequests(summary.getOverdueCount())
        .build();
  }

  public AssignmentSummaryDTO toDto(RoeAssignmentSummaryTotals totals) {
    return AssignmentSummaryDTO.builder()
        .openRequests(totals.getOpenCount())
        .closedRequests(totals.getClosedCount())
        .roundOneRequests(totals.getFirstRoundCount())
        .inProgressRequests(totals.getInProgressCount())
        .dueRequests(totals.getDueCount())
        .overdueRequests(totals.getOverdueCount())
        .build();
  }

  public AssignmentSummaryDTO toDto(ArmAssignmentSummary summary) {
    return AssignmentSummaryDTO.builder()
        .accountName(summary.getAccountName())
        .dueRequests(summary.getDueCount())
        .overdueRequests(summary.getOverdueCount())
        .upcomingRequests(summary.getUpcomingCount())
        .build();
  }

  public AssignmentSummaryDTO toDto(ArmAssignmentSummaryTotals totals) {
    return AssignmentSummaryDTO.builder()
        .dueRequests(totals.getDueCount())
        .overdueRequests(totals.getOverdueCount())
        .upcomingRequests(totals.getUpcomingCount())
        .build();
  }

  public AssignmentSummaryDTO toEmptyDto() {
    return AssignmentSummaryDTO.builder()
        .dueRequests(0)
        .overdueRequests(0)
        .upcomingRequests(0)
        .openRequests(0)
        .closedRequests(0)
        .roundOneRequests(0)
        .inProgressRequests(0)
        .build();
  }
}
