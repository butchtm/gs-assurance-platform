package org.goldstandard.assurance.api.service.report;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssignmentSummaryReportFactoryProvider {

  private final List<AssignmentSummaryReportFactory> reportFactories;

  public AssignmentSummaryReportFactory getReportFactory(AssignmentSummaryType type) {
    return reportFactories.stream()
        .filter(factory -> factory.getType().equals(type))
        .findFirst()
        .orElse(null);
  }
}
