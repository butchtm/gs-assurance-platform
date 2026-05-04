package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.junit.jupiter.api.Test;

class RequestAuditInfoFactoryTest {

  private final RequestAuditInfoFactory factory = new RequestAuditInfoFactory();

  @Test
  void shouldPopulateIdsOnRequestInfo() {
    var project = buildProject(1);
    var request = buildRequest(2, project);

    var info = factory.buildRequestInfo(request, Collections.emptyList());

    assertThat(info)
        .hasSize(2)
        .contains(entry("requestId", request.getId()), entry("projectId", project.getId()));
  }

  @Test
  void shouldPopulateChildIdsOnRequestInfoIfPresent() {
    var project = buildProject(3);
    var request = buildRequest(4, project);
    var childRequest1 = buildRequest(5, buildProject(6));
    var childRequest2 = buildRequest(7, buildProject(8));
    var childRequests = List.of(childRequest1, childRequest2);

    var info = factory.buildRequestInfo(request, childRequests);

    var expectedChildRequestIds = toString(childRequests.stream().map(Request::getId));
    var expectedChildProjectIds =
        toString(childRequests.stream().map(Request::getProject).map(Project::getId));
    assertThat(info)
        .hasSize(4)
        .contains(
            entry("requestId", request.getId()),
            entry("projectId", project.getId()),
            entry("childRequestIds", expectedChildRequestIds),
            entry("childProjectIds", expectedChildProjectIds));
  }

  private static Request buildRequest(int id, Project project) {
    return Request.builder().id(id).project(project).build();
  }

  private static Project buildProject(int id) {
    return Project.builder().id(id).build();
  }

  private static String toString(Stream<Integer> ids) {
    return ids.map(id -> Integer.toString(id)).collect(Collectors.joining(","));
  }
}
