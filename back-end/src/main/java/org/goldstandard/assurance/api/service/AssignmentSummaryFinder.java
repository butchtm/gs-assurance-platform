package org.goldstandard.assurance.api.service;

import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignmentSummaryFinder {
  Page<AssignmentSummaryDTO> getAssignmentSummaryPage(
      AssignmentSummaryFiltersDTO filters, Pageable pageable);

  AssignmentSummaryDTO getAssignmentsSummaryTotals(AssignmentSummaryFiltersDTO filters);

  AssignmentSummaryType getType();
}
