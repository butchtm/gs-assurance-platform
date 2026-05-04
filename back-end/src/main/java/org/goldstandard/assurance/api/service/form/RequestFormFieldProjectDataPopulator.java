package org.goldstandard.assurance.api.service.form;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collection;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.external.registry.RegistryUiLinkFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Builder
public class RequestFormFieldProjectDataPopulator implements RequestFormFieldDataPopulator {

  private final BatchRequestFinder batchRequestFinder;
  private final RegistryUiLinkFactory registryLinkFactory;
  private final ObjectMapper mapper;

  @Override
  public void populateData(ObjectNode data, FormFieldLocation location, Request request) {
    var projects = batchRequestFinder.getAllBatchProjects(request);
    log.info("found {} projects to pre populate for request {}", projects.size(), request.getId());
    data.set(location.parentField(), toJsonArray(projects, location));
  }

  private ArrayNode toJsonArray(Collection<Project> projects, FormFieldLocation location) {
    var array = mapper.createArrayNode();
    var objects = projects.stream().map(project -> toJsonObject(project, location)).toList();
    objects.forEach(array::add);
    return array;
  }

  private JsonNode toJsonObject(Project project, FormFieldLocation location) {
    ObjectNode object = mapper.createObjectNode();
    log.info("got child fields {} for project {}", location.getChildFields(), project.getGsid());
    for (var field : location.getChildFields()) {
      toValue(field, project).ifPresent(value -> object.put(field, value));
    }
    return object;
  }

  private Optional<String> toValue(String field, Project project) {
    return switch (field) {
      case "gsid" -> Optional.ofNullable(project.getGsid());
      case "title" -> Optional.ofNullable(project.getName());
      case "registryLink" -> registryLinkFactory.buildProjectLinkUrl(project);
      default -> Optional.empty();
    };
  }
}
