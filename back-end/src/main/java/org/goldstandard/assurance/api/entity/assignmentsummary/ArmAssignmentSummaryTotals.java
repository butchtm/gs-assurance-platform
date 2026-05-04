package org.goldstandard.assurance.api.entity.assignmentsummary;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class ArmAssignmentSummaryTotals {
  private final int upcomingCount;
  private final int dueCount;
  private final int overdueCount;
}
