package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.AssignmentSummaryConverter;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.goldstandard.assurance.api.repository.assignmentsummary.AssignmentSummaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArmAssignmentSummaryFinder implements AssignmentSummaryFinder {

  private final AssignmentSummaryRepository repository;
  private final AssignmentSummaryConverter converter;

  public Page<AssignmentSummaryDTO> getAssignmentSummaryPage(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    return repository.getArmAssignmentSummary(filters, pageable).map(converter::toDto);
  }

  public AssignmentSummaryDTO getAssignmentsSummaryTotals(AssignmentSummaryFiltersDTO filters) {
    return repository
        .getArmAssignmentSummaryTotals(filters)
        .map(converter::toDto)
        .orElse(converter.toEmptyDto());
  }

  public AssignmentSummaryType getType() {
    return AssignmentSummaryType.ARM;
  }
}
