package org.goldstandard.assurance.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ProjectStatusUpdateMapping;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.repository.ProjectStatusUpdateMappingRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectStatusUpdateCalculator {

  private final ProjectStatusUpdateMappingRepository repository;

  public Optional<ProjectStatus> calculateNewProjectStatus(Request request) {
    var requestStatus = request.getCurrentRequestStatus();
    var requestType = request.getRequestType();
    var mapping =
        repository.findByRequestStatusIdAndRequestTypeId(
            requestStatus.getId(), requestType.getId());
    log.info(
        "found project status update mapping {} for request status id {} and request type id {}",
        mapping.map(ProjectStatusUpdateCalculator::toString),
        requestStatus.getId(),
        requestType.getId());
    return mapping.map(ProjectStatusUpdateMapping::getProjectStatus);
  }

  private static String toString(ProjectStatusUpdateMapping mapping) {
    return String.format(
        "%s %s %s",
        mapping.getRequestStatus().getCode(),
        mapping.getRequestType().getCode(),
        mapping.getProjectStatus().getCode());
  }
}
