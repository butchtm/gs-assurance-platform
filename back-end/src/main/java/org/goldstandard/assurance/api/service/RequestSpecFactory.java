package org.goldstandard.assurance.api.service;

import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.util.Collection;
import java.util.Set;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RequestSpecFactory {

  public Specification<Request> toSpec(Project project) {
    var spec = belongsToProject(project.getId());
    if (project.shouldDisplayNotRequestedStatusRequests()) {
      return spec;
    }
    return spec.and(doesNotHaveRequestStatusId(Set.of(RequestStatus.NOT_REQUESTED_ID)));
  }

  public Specification<Request> byRequestIds(Collection<Integer> requestIds) {
    return requestIdIsIn(requestIds);
  }

  public Specification<Request> byProjectIds(Collection<Integer> projectIds) {
    return projectIdIsIn(projectIds);
  }

  private static Specification<Request> belongsToProject(Integer projectId) {
    return (request, cq, cb) -> cb.equal(request.join("project").get("id"), projectId);
  }

  private static Specification<Request> doesNotHaveRequestStatusId(Collection<Integer> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return unrestricted();
    }
    return (request, cq, cb) ->
        cb.not(cb.in(request.get("currentRequestStatus").get("id")).value(ids));
  }

  private static Specification<Request> projectIdIsIn(Collection<Integer> projectIds) {
    return (request, cq, cb) -> request.join("project").get("id").in(projectIds);
  }

  private static Specification<Request> requestIdIsIn(Collection<Integer> requestIds) {
    return (request, cq, cb) -> request.get("id").in(requestIds);
  }
}
