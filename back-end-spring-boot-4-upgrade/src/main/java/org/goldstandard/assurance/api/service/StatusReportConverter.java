package org.goldstandard.assurance.api.service;

import java.util.List;
import org.goldstandard.assurance.api.dto.StatusReportDTO;
import org.goldstandard.assurance.api.entity.StatusReport;
import org.goldstandard.assurance.api.entity.view.StatusReportProjectsGrouping;
import org.goldstandard.assurance.api.entity.view.StatusReportRequestsGrouping;
import org.springframework.stereotype.Component;

@Component
public class StatusReportConverter {

  public StatusReportDTO toResponseDto(StatusReport statusReportData) {
    return StatusReportDTO.builder()
        .projects(toResponseProjects(statusReportData.getProjectsByType()))
        .requests(toResponseRequests(statusReportData.getCompletedRequests()))
        .allRequests(toResponseAllRequests(statusReportData))
        .build();
  }

  private List<StatusReportDTO.StatusReportProject> toResponseProjects(
      List<StatusReportProjectsGrouping> projectGroupings) {
    return projectGroupings.stream()
        .map(
            projectGroup ->
                StatusReportDTO.StatusReportProject.builder()
                    .type(projectGroup.getType())
                    .description(projectGroup.getDescription())
                    .count(projectGroup.getCount())
                    .build())
        .toList();
  }

  private List<StatusReportDTO.StatusReportRequest> toResponseRequests(
      List<StatusReportRequestsGrouping> requestGroupings) {
    return requestGroupings.stream()
        .map(
            requestGrouping ->
                StatusReportDTO.StatusReportRequest.builder()
                    .type(requestGrouping.getType())
                    .description(requestGrouping.getDescription())
                    .completedCount(requestGrouping.getCount())
                    .build())
        .toList();
  }

  private StatusReportDTO.StatusReportAllRequests toResponseAllRequests(StatusReport statusReport) {
    return StatusReportDTO.StatusReportAllRequests.builder()
        .completedCount(
            statusReport.getCompletedRequests().stream()
                .mapToInt(StatusReportRequestsGrouping::getCount)
                .sum())
        .successfulCompletenessChecksCount(
            statusReport.getSuccessfulCompletenessCheckRequestsCount())
        .build();
  }
}
