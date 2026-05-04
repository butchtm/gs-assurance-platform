package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class AssignmentSummaryDTO {

  private final String accountName;
  private final int openRequests;
  private final int closedRequests;
  private final int roundOneRequests;
  private final int inProgressRequests;
  private final int upcomingRequests;
  private final int dueRequests;
  private final int overdueRequests;
}
