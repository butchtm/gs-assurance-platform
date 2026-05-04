package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.ProjectStatus.newStatus;
import static org.goldstandard.assurance.api.service.GsidSearchTermFactory.toGsidSearchTerm;
import static org.goldstandard.assurance.api.service.GsidSearchTermFactory.toLowerCaseWithWildcards;
import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.entity.ProjectSearchCriteriaDTO;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.view.ProjectTreeNodeView;
import org.goldstandard.assurance.api.entity.view.SearchableProjectView;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectSearchSpecFactory {

  private static final String PROJECT_GS_ID = "gsid";

  private final PermissionsService permissionsService;

  public Specification<SearchableProjectView> build(ProjectSearchCriteriaDTO criteria) {
    var spec = toSpecification(criteria);
    if (permissionsService.isAdminOrFinanceOrArmOrTacOrRegistry()) {
      return spec;
    }
    return spec.and(buildNotNewSearchSpec());
  }

  public Specification<ProjectTreeNodeView> idIn(Set<Integer> ids) {
    return (project, cq, cb) -> project.get("id").in(ids);
  }

  private Specification<SearchableProjectView> toSpecification(ProjectSearchCriteriaDTO criteria) {
    Specification<SearchableProjectView> specification = unrestricted();
    String gsid = criteria.getGsid();
    if (StringUtils.isNotEmpty(gsid)) {
      specification = specification.and(hasGSId(gsid));
    }
    Integer bundleId = criteria.getBundleId();
    if (Objects.nonNull(bundleId)) {
      specification = specification.and(hasBundleId(bundleId));
    }
    String name = criteria.getName();
    if (StringUtils.isNotEmpty(name)) {
      specification = specification.and(nameContains(name));
    }
    Integer accountId = criteria.getAccountId();
    if (Objects.nonNull(accountId)) {
      specification = specification.and(hasAccountId(accountId));
    }
    Integer masterProjectId = criteria.getMasterProjectId();
    if (Objects.nonNull(masterProjectId)) {
      specification = specification.and(hasMasterProjectId(masterProjectId));
    }
    Integer parentProjectId = criteria.getParentProjectId();
    if (Objects.nonNull(parentProjectId)) {
      specification = specification.and(hasParentProjectId(parentProjectId));
    }
    Set<Integer> projectStatusId = criteria.getProjectStatusId();
    if (Objects.nonNull(projectStatusId)) {
      specification = specification.and(searchableProjectStatusIdIn(projectStatusId));
    }
    Set<Integer> projectIds = criteria.getProjectIds();
    if (CollectionUtils.isNotEmpty(projectIds)) {
      specification = specification.and(searchableProjectIdIn(projectIds));
    }
    String search = criteria.getSearch();
    if (StringUtils.isNotEmpty(search)) {
      specification = specification.and(hasSearchMatch(search));
    }
    return specification;
  }

  private Specification<SearchableProjectView> buildNotNewSearchSpec() {
    var notNewSpec = doesNotHaveProjectStatus(newStatus());
    if (permissionsService.isProjectDeveloper()) {
      return notNewSpec.or(buildProjectDeveloperOwnedSearchSpec());
    }
    return notNewSpec;
  }

  private Specification<SearchableProjectView> buildProjectDeveloperOwnedSearchSpec() {
    var accountId = permissionsService.getAccountIdForUser();
    if (accountId.isPresent()) {
      return hasAccountId(accountId.get());
    }
    var username =
        permissionsService.getAuthenticatedUser().map(User::getUserName).orElse("anonymous");
    throw new PermissionException(
        "project developer user %s does not have an account assigned", username);
  }

  private static Specification<SearchableProjectView> hasBundleId(Integer bundleId) {
    return (project, cq, cb) -> cb.equal(project.get("bundleId"), bundleId);
  }

  private static Specification<SearchableProjectView> doesNotHaveProjectStatus(
      ProjectStatus projectStatus) {
    return Specification.not(hasProjectStatusId(projectStatus.getId()));
  }

  private static Specification<SearchableProjectView> hasAccountId(Integer accountId) {
    return (project, cq, cb) -> cb.equal(project.get("accountId"), accountId);
  }

  private static Specification<SearchableProjectView> nameContains(String name) {
    return (project, cq, cb) ->
        cb.like(cb.lower(project.get("name")), toLowerCaseWithWildcards(name));
  }

  private static Specification<SearchableProjectView> hasGSId(String gsid) {
    return (project, cq, cb) -> cb.equal(project.get(PROJECT_GS_ID), gsid);
  }

  private static Specification<SearchableProjectView> hasMasterProjectId(Integer masterProjectId) {
    return (project, cq, cb) -> cb.equal(project.get("masterProjectId"), masterProjectId);
  }

  private static Specification<SearchableProjectView> hasParentProjectId(Integer parentProjectId) {
    return (project, cq, cb) -> cb.equal(project.get("parentProjectId"), parentProjectId);
  }

  private static Specification<SearchableProjectView> hasProjectStatusId(Integer projectStatusId) {
    return (project, cq, cb) -> cb.equal(project.get("projectStatusId"), projectStatusId);
  }

  private static Specification<SearchableProjectView> searchableProjectStatusIdIn(
      Set<Integer> ids) {
    return (project, cq, cb) -> project.get("projectStatusId").in(ids);
  }

  private static Specification<SearchableProjectView> searchableProjectIdIn(Set<Integer> ids) {
    return (project, cq, cb) -> project.get("id").in(ids);
  }

  private static Specification<SearchableProjectView> hasSearchMatch(String search) {
    return nameContains(search)
        .or(
            (project, cq, cb) ->
                cb.equal(cb.lower(project.get(PROJECT_GS_ID)), toGsidSearchTerm(search)));
  }
}
