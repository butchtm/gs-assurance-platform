package org.goldstandard.assurance.api.service;

import static java.util.function.Function.identity;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.dto.BundleProjectDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.projection.RequestMinimal;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BundleFinder {

  private final ProjectRepository projectRepository;
  private final RequestRepository requestRepository;

  public Page<BundleProjectDTO> getBundleProjects(Integer bundleId, Pageable pageable) {
    var projectPage = projectRepository.findByBundleId(bundleId, pageable);
    var requests = findRequests(projectPage.getContent());
    return projectPage.map(project -> toBundleProjectDTO(project, requests));
  }

  private Map<Integer, RequestMinimal> findRequests(Collection<Project> projects) {
    var requestIds = toCurrentRequestIds(projects);
    var requests = requestRepository.findAllByIdIn(requestIds, RequestMinimal.class);
    return mapById(requests);
  }

  private static BundleProjectDTO toBundleProjectDTO(
      Project project, Map<Integer, RequestMinimal> requests) {
    return BundleProjectDTO.builder()
        .id(project.getId())
        .gsid(project.getGsid())
        .name(project.getName())
        .currentRequest(toRequestDTO(requests.get(project.getCurrentRequestId())))
        .build();
  }

  private static BundleProjectDTO.Request toRequestDTO(RequestMinimal request) {
    return BundleProjectDTO.Request.builder()
        .id(request.getId())
        .name(request.getRequestName())
        .build();
  }

  private static Collection<Integer> toCurrentRequestIds(Collection<Project> projects) {
    return projects.stream()
        .map(Project::getCurrentRequestId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private static Map<Integer, RequestMinimal> mapById(Iterable<RequestMinimal> requests) {
    return IterableUtils.toList(requests).stream()
        .collect(Collectors.toMap(RequestMinimal::getId, identity()));
  }
}
