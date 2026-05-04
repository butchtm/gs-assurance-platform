package org.goldstandard.assurance.api.service.form;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.external.registry.RegistryUiLinkFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.node.ObjectNode;

@Slf4j
@Component
@RequiredArgsConstructor
@Builder
public class RequestFormFieldOldProjectDataPopulator implements RequestFormFieldDataPopulator {

  private final BatchRequestFinder batchRequestFinder;
  private final RegistryUiLinkFactory registryLinkFactory;

  @Override
  public void populateData(ObjectNode data, FormFieldLocation location, Request request) {
    var projects = batchRequestFinder.getAllBatchProjects(request);
    log.info("found {} projects to pre populate for request {}", projects.size(), request.getId());
    location.getChildFields().forEach(field -> data.put(field, toValue(field, projects)));
  }

  private String toValue(String field, Collection<Project> projects) {
    return switch (field) {
      case "projectTitles" -> toString(projects, Project::getGsid);
      case "projectGsids" -> toString(projects, Project::getName);
      case "projectRegistryLinks" -> toString(projects, this::toRegistryLinkUrl);
      default -> "";
    };
  }

  private String toRegistryLinkUrl(Project project) {
    return registryLinkFactory.buildProjectLinkUrl(project).orElse("");
  }

  private String toString(Collection<Project> projects, Function<Project, String> function) {
    return projects.stream().map(function).collect(Collectors.joining(", "));
  }
}
