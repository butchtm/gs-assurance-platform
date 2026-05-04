package org.goldstandard.assurance.api.fixtures;

import java.util.Arrays;
import java.util.List;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.goldstandard.assurance.api.entity.view.StatusReportProjectsGrouping;
import org.goldstandard.assurance.api.entity.view.StatusReportRequestsGrouping;

public class StatusReportMother {

  public static StatusReport build() {
    return StatusReport.builder()
        .projectsByType(buildProjectGroups())
        .completedRequests(buildRequestGroups())
        .successfulCompletenessCheckRequestsCount(1)
        .build();
  }

  public static List<StatusReportProjectsGrouping> buildProjectGroups() {
    return Arrays.asList(
        StatusReportProjectsGrouping.builder()
            .type("AGRICULTURE")
            .description("Agriculture")
            .count(2)
            .build(),
        StatusReportProjectsGrouping.builder()
            .type("BIOGAS_COGENERATION")
            .description("Biogas - Cogeneration")
            .count(1)
            .build());
  }

  public static List<StatusReportRequestsGrouping> buildRequestGroups() {
    return Arrays.asList(
        StatusReportRequestsGrouping.builder()
            .type("ANNUAL_REPORT")
            .description("Annual Report")
            .count(3)
            .build(),
        StatusReportRequestsGrouping.builder()
            .type("GENERIC")
            .description("Generic")
            .count(1)
            .build());
  }
}
