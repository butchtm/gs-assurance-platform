package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.common.PagedResource;

@RequiredArgsConstructor
@Builder
@Getter
public class AssignmentSummaryResponseDTO {

  private final PagedResource<AssignmentSummaryDTO> assignmentSummary;
  private final AssignmentSummaryDTO assignmentTotals;
}
