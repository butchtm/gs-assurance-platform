package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssignmentSummaryFinderFactory {
  private final List<AssignmentSummaryFinder> summaryFinders;

  public AssignmentSummaryFinder getSummaryFinder(AssignmentSummaryType type) {
    return summaryFinders.stream()
        .filter(finder -> finder.getType().equals(type))
        .findFirst()
        .orElse(null);
  }
}
