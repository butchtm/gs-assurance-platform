package org.goldstandard.assurance.api.service.report;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.service.AssignmentSummaryFinder;
import org.springframework.data.domain.Pageable;

@Getter
@RequiredArgsConstructor
@Builder
public class AssignmentSummaryReportWriterParams {
  private final AssignmentSummaryFinder summaryFinder;
  private final AssignmentSummaryReportFactory factory;
  private final AssignmentSummaryFiltersDTO filters;
  private final Pageable originalPageable;
}
