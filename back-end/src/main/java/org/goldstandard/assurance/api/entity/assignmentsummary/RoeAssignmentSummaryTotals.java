package org.goldstandard.assurance.api.entity.assignmentsummary;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class RoeAssignmentSummaryTotals {
  private final int assignmentTypeId;
  private final int total;
  private final int openCount;
  private final int closedCount;
  private final int inProgressCount;
  private final int firstRoundCount;
  private final int dueCount;
  private final int overdueCount;
}
