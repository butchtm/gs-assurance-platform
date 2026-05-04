package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.springframework.stereotype.Component;

@Component
public class RequestAuditInfoFactory {

  public Map<String, Object> buildRequestInfo(Request request, Collection<Request> childRequests) {
    var info = new HashMap<String, Object>();
    info.put("requestId", request.getId());
    info.put("projectId", request.getProject().getId());
    if (!childRequests.isEmpty()) {
      info.put("childRequestIds", toString(childRequests.stream().map(Request::getId)));
      info.put(
          "childProjectIds",
          toString(childRequests.stream().map(Request::getProject).map(Project::getId)));
    }
    return Collections.unmodifiableMap(info);
  }

  public Map<String, Object> buildProjectStatusInfo(Collection<Project> projects) {
    var projectIds = toString(projects.stream().map(Project::getId));
    var projectStatusIds =
        toString(projects.stream().map(Project::getProjectStatus).map(ReferenceDataType::getId));
    return Map.of("projectIds", projectIds, "statusIds", projectStatusIds);
  }

  private static String toString(Stream<Integer> idStream) {
    return idStream.map(Object::toString).collect(Collectors.joining(","));
  }
}
