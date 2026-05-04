package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.GsidSearchTermFactory.toGsidSearchTerm;
import static org.goldstandard.assurance.api.service.GsidSearchTermFactory.toLowerCaseWithWildcards;

import jakarta.persistence.criteria.Predicate;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectSearchMatchSpecFactory {

  private static final String PROJECT_GSID = "projectGsid";
  private static final String PARENT_GSID = "parentGsid";
  private static final String MASTER_GSID = "masterGsid";

  public <T> Specification<T> hasSearchMatch(String search) {
    Specification<T> nameSpec = projectNameContains(search);
    Specification<T> gsidSpec = gsidSearchTermMatchesColumn(search, PROJECT_GSID);
    return nameSpec.or(gsidSpec);
  }

  public <T> Specification<T> hasRelatedProjectsSearchMatch(String search) {
    Specification<T> searchSpec = hasSearchMatch(search);
    Specification<T> parentGsidSpec = gsidSearchTermMatchesColumn(search, PARENT_GSID);
    Specification<T> masterGsidSpec = gsidSearchTermMatchesColumn(search, MASTER_GSID);
    Specification<T> childGsidSpec = childGsidMatches(search);
    return searchSpec.or(parentGsidSpec).or(masterGsidSpec).or(childGsidSpec);
  }

  private static <T> Specification<T> projectNameContains(String search) {
    return (root, cq, cb) ->
        cb.like(cb.lower(root.get("projectName")), toLowerCaseWithWildcards(search));
  }

  private static <T> Specification<T> gsidSearchTermMatchesColumn(String search, String column) {
    return (root, cq, cb) -> cb.like(cb.lower(root.get(column)), toGsidSearchTerm(search));
  }

  private static <T> Specification<T> childGsidMatches(String search) {
    return (root, cq, cb) -> {
      var childSubquery = cb.createQuery().subquery(Integer.class);
      var childRoot = childSubquery.from(CommonDashboardView.class);
      childSubquery.select(childRoot.get("id"));

      Predicate childProjectGsidMatches =
          cb.like(cb.lower(childRoot.get(PROJECT_GSID)), toGsidSearchTerm(search));
      Predicate childParentMatchesRoot =
          cb.or(
              cb.equal(childRoot.get(PARENT_GSID), root.get(PROJECT_GSID)),
              cb.equal(childRoot.get(MASTER_GSID), root.get(PROJECT_GSID)));

      childSubquery.where(childProjectGsidMatches, childParentMatchesRoot);

      return cb.exists(childSubquery);
    };
  }
}
