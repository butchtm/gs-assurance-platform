package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.common.PagedResource;

@Builder
@Getter
public class AssignmentSummaryResponseDTO {

  private final PagedResource<AssignmentSummaryDTO> assignmentSummary;
  private final AssignmentSummaryDTO assignmentTotals;
}
