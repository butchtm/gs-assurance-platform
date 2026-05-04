package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectUpdateFinder {

  private final RequestFinder requestFinder;
  private final BatchRequestFinder batchRequestFinder;

  public Collection<Project> toProjectsAppendingLegacyBatchProjectsIfPresent(Integer requestId) {
    var request = requestFinder.getRequestById(requestId);
    var childRequests = batchRequestFinder.getBatchChildRequests(request);
    return toProjectsAppendingLegacyBatchProjectsIfPresent(request, childRequests);
  }

  public Collection<Project> toProjectsAppendingLegacyBatchProjectsIfPresent(
      Request request, Collection<Request> childRequests) {
    var initialProjects = toProjects(request, childRequests);
    return appendLegacyBatchProjectsIfPresent(request.getId(), initialProjects);
  }

  private Collection<Project> toProjects(Request request, Collection<Request> childRequests) {
    return Stream.concat(Stream.of(request), childRequests.stream())
        .map(Request::getProject)
        .toList();
  }

  private Collection<Project> appendLegacyBatchProjectsIfPresent(
      Integer requestId, Collection<Project> projects) {
    var legacyBatchProjects = batchRequestFinder.getLegacyBatchProjects(requestId);
    var allProjects = Stream.concat(projects.stream(), legacyBatchProjects.stream()).toList();
    return deduplicateById(allProjects);
  }

  private static Collection<Project> deduplicateById(Collection<Project> projects) {
    return projects.stream()
        .collect(Collectors.toMap(Project::getId, Function.identity(), (p1, p2) -> p1))
        .values();
  }
}
