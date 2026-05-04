package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.AssignmentSummaryConverter;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryReport;
import org.goldstandard.assurance.api.dto.AssignmentSummaryResponseDTO;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.goldstandard.assurance.api.service.report.AssignmentSummaryReportGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentSummaryService {

  private final AssignmentSummaryFinderFactory finderFactory;
  private final AssignmentSummaryConverter converter;
  private final AssignmentSummaryReportGenerator reportGenerator;

  public AssignmentSummaryResponseDTO getArmAssignmentSummaryResponse(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    return getAssignmentSummaryByType(filters, pageable, AssignmentSummaryType.ARM);
  }

  public AssignmentSummaryResponseDTO getReviewAssignmentSummaryResponse(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    return getAssignmentSummaryByType(filters, pageable, AssignmentSummaryType.ROE);
  }

  public AssignmentSummaryReport generateArmReport(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    return generateReviewReport(filters, pageable, AssignmentSummaryType.ARM);
  }

  public AssignmentSummaryReport generateReviewReport(
      AssignmentSummaryFiltersDTO filters, Pageable pageable) {
    return generateReviewReport(filters, pageable, AssignmentSummaryType.ROE);
  }

  private AssignmentSummaryResponseDTO getAssignmentSummaryByType(
      AssignmentSummaryFiltersDTO filters, Pageable pageable, AssignmentSummaryType type) {
    var finder = finderFactory.getSummaryFinder(type);
    var assignmentsSummary = finder.getAssignmentSummaryPage(filters, pageable);
    var assignmentsSummaryTotals = finder.getAssignmentsSummaryTotals(filters);
    return converter.toAssignmentSummaryResponse(assignmentsSummary, assignmentsSummaryTotals);
  }

  private AssignmentSummaryReport generateReviewReport(
      AssignmentSummaryFiltersDTO filters, Pageable pageable, AssignmentSummaryType type) {
    var finder = finderFactory.getSummaryFinder(type);
    return reportGenerator.generate(filters, pageable, finder);
  }
}
