package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.GsidSearchTermFactory.toGsidSearchTerm;
import static org.goldstandard.assurance.api.service.GsidSearchTermFactory.toLowerCaseWithWildcards;
import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.dto.GetProjectDeveloperRequestsDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class ProjectDeveloperRequestSpecFactory {

  private static final String PROJECT = "project";

  public Specification<Request> toSpec(int accountId, GetProjectDeveloperRequestsDTO dto) {
    var spec = belongsToAccount(accountId);
    spec = applyFilters(spec, dto);
    return applyProjectSearchIfRequired(spec, dto);
  }

  private static Specification<Request> belongsToAccount(Integer accountId) {
    return (request, cq, cb) -> request.join(PROJECT).get("accountId").in(accountId);
  }

  private static Specification<Request> applyFilters(
      Specification<Request> spec, GetProjectDeveloperRequestsDTO dto) {
    Collection<Specification<Request>> filterSpecs = toSpecs(dto);
    for (Specification<Request> filterSpec : filterSpecs) {
      spec = spec.and(filterSpec);
    }
    return spec;
  }

  private static Collection<Specification<Request>> toSpecs(
      GetProjectDeveloperRequestsDTO getRequestsDto) {
    return List.of(hasRequestStatusId(getRequestsDto.getRequestStatusIds()));
  }

  private static Specification<Request> hasRequestStatusId(Collection<Integer> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return unrestricted();
    }
    return (request, cq, cb) -> cb.in(request.get("currentRequestStatus").get("id")).value(ids);
  }

  private static Specification<Request> applyProjectSearchIfRequired(
      Specification<Request> spec, GetProjectDeveloperRequestsDTO filters) {
    return filters
        .getProjectSearch()
        .filter(StringUtils::isNotEmpty)
        .map(searchTerm -> spec.and(hasProjectSearchMatch(searchTerm)))
        .orElse(spec);
  }

  private static Specification<Request> hasProjectSearchMatch(String search) {
    var nameSpec = projectNameContains(search);
    var gsidSpec = projectGsidMatches(search);
    return nameSpec.or(gsidSpec);
  }

  private static Specification<Request> projectNameContains(String search) {
    return (request, cq, cb) ->
        cb.like(cb.lower(request.get(PROJECT).get("name")), toLowerCaseWithWildcards(search));
  }

  private static Specification<Request> projectGsidMatches(String search) {
    return (request, cq, cb) ->
        cb.like(cb.lower(request.get(PROJECT).get("gsid")), toGsidSearchTerm(search));
  }
}
