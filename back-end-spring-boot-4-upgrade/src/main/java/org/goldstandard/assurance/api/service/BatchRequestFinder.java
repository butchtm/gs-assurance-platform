package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.BatchRequest;
import org.goldstandard.assurance.api.entity.BatchRequests;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.BatchRepository;
import org.goldstandard.assurance.api.repository.BatchRequestRepository;
import org.goldstandard.assurance.api.repository.LegacyBatchRequestRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchRequestFinder {

  private final BatchRepository batchRepository;
  private final BatchRequestRepository batchRequestRepository;
  private final LegacyBatchRequestRepository legacyBatchRequestRepository;

  public Collection<Project> getAllBatchProjects(Request request) {
    var childProjects = getChildProjects(request);
    return Stream.concat(Stream.of(request.getProject()), childProjects).toList();
  }

  public Stream<Project> getChildProjects(Request request) {
    return getBatchChildRequests(request).stream().map(Request::getProject);
  }

  public Collection<Request> getBatchChildRequests(Request request) {
    if (request.isBatchParent()) {
      var batchRequests = getRequestsInBatch(request.getBatch().getId());
      return batchRequests.getChildren().stream().map(BatchRequest::getRequest).toList();
    }
    return Collections.emptyList();
  }

  public BatchRequests getRequestsInBatch(int batchId) {
    var batch = batchRepository.forceFindById(batchId);
    return BatchRequests.builder()
        .batch(batch)
        .requests(batchRequestRepository.findAllByBatchId(batchId))
        .build();
  }

  public List<Integer> getLegacyBatchProjectIds(Integer requestId) {
    return legacyBatchRequestRepository.findAllProjectIdsInLegacyBatch(requestId);
  }

  public List<Project> getLegacyBatchProjects(Integer requestId) {
    return legacyBatchRequestRepository.findAllProjectsInLegacyBatch(requestId);
  }
}
