package org.goldstandard.assurance.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class StatusReportDTO {
  private final List<StatusReportProject> projects;
  private final List<StatusReportRequest> requests;
  private final StatusReportAllRequests allRequests;

  @Getter
  @RequiredArgsConstructor
  @NoArgsConstructor(force = true)
  @Builder
  public static class StatusReportProject {
    private final String type;
    private final String description;
    private final int count;
  }

  @Getter
  @RequiredArgsConstructor
  @NoArgsConstructor(force = true)
  @Builder
  public static class StatusReportRequest {
    private final String type;
    private final String description;
    private final int completedCount;
  }

  @Getter
  @RequiredArgsConstructor
  @NoArgsConstructor(force = true)
  @Builder
  public static class StatusReportAllRequests {
    private final int completedCount;
    private final int successfulCompletenessChecksCount;
  }
}
