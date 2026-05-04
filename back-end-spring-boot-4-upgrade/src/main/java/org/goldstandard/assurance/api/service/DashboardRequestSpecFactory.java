package org.goldstandard.assurance.api.service;

import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.dto.SpecificDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.entity.view.AllRequestView;
import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.goldstandard.assurance.api.entity.view.CommonDashboardView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardRequestSpecFactory {

  private final ProjectSearchMatchSpecFactory searchMatchSpecFactory;

  public <T> Specification<T> isAssignedToAccount(int accountId) {
    return (assignedRequest, cq, cb) ->
        cb.equal(assignedRequest.get("assignedAccountId"), accountId);
  }

  public Specification<AssignedRequestView> toSpec(
      int accountId, SpecificDashboardRequestFiltersDTO filters) {
    Specification<AssignedRequestView> spec = isAssignedToAccount(accountId);
    return toSpec(spec, toFilterSpecs(filters));
  }

  public Specification<AllRequestView> toSpec(SpecificDashboardRequestFiltersDTO filters) {
    Specification<AllRequestView> spec = unrestricted();
    return toSpec(spec, toFilterSpecs(filters));
  }

  public Specification<CommonDashboardView> toSpec(CommonDashboardRequestFiltersDTO filters) {
    Specification<CommonDashboardView> spec = unrestricted();
    return toSpec(spec, toFilterSpecs(filters));
  }

  private <T> Specification<T> toSpec(
      Specification<T> spec, Collection<Specification<T>> filterSpecs) {
    for (Specification<T> filterSpec : filterSpecs) {
      spec = spec.and(filterSpec);
    }
    return spec;
  }

  private <T> Collection<Specification<T>> toFilterSpecs(
      SpecificDashboardRequestFiltersDTO filters) {
    var specs = new ArrayList<Specification<T>>();
    specs.add(in("requestStatusId", filters.getRequestStatusIds()));
    specs.add(in("projectId", filters.getProjectIds()));
    specs.add(in("projectAccountId", filters.getProjectAccountIds()));
    specs.add(in("reviewerAccountId", filters.getReviewerAccountIds()));
    specs.add(in("vvbAccountId", filters.getVvbAccountIds()));
    specs.add(in("invoiceStatusId", filters.getInvoiceStatusIds()));
    specs.add(in("latestActivityId", filters.getActivityIds()));
    specs.add(in("latestActivityResponsibleAccountId", filters.getResponsibleAccountIds()));
    specs.add(in("requestTypeId", filters.getRequestTypeIds()));
    specs.add(in("qaqcAccountId", filters.getQaqcAccountIds()));
    filters
        .getProjectSearch()
        .ifPresent(gsidSearch -> specs.add(searchMatchSpecFactory.hasSearchMatch(gsidSearch)));
    return Collections.unmodifiableList(specs);
  }

  private <T> Collection<Specification<T>> toFilterSpecs(CommonDashboardRequestFiltersDTO filters) {
    var specs = new ArrayList<Specification<T>>();
    specs.add(in("requestStatusId", filters.getRequestStatusIds()));
    specs.add(in("invoiceStatusId", filters.getInvoiceStatusIds()));
    specs.add(in("requestTypeId", filters.getRequestTypeIds()));
    specs.add(in("projectClassificationId", filters.getProjectClassificationIds()));
    specs.add(in("currentRoundIndex", filters.getRoundOfReviewCounts()));
    specs.add(hasProjectTypeId(filters.getProjectTypeIds()));
    filters
        .getFirstRoundDateStart()
        .ifPresent(startDate -> specs.add(isAfter("firstRoundStartDate", startDate)));
    filters
        .getFirstRoundDateEnd()
        .ifPresent(endDate -> specs.add(isBefore("firstRoundStartDate", endDate)));
    filters
        .getProjectSearch()
        .ifPresent(
            gsidSearch ->
                specs.add(searchMatchSpecFactory.hasRelatedProjectsSearchMatch(gsidSearch)));
    return Collections.unmodifiableList(specs);
  }

  private static <T> Specification<T> isAfter(String fieldName, ZonedDateTime date) {
    return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get(fieldName), date);
  }

  private static <T> Specification<T> isBefore(String fieldName, ZonedDateTime date) {
    return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get(fieldName), date);
  }

  private static <T> Specification<T> in(String fieldName, Collection<Integer> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return unrestricted();
    }
    return (root, cq, cb) -> cb.in(root.get(fieldName)).value(ids);
  }

  private static <T> Specification<T> hasProjectTypeId(Collection<Integer> projectTypeIds) {
    if (CollectionUtils.isEmpty(projectTypeIds)) {
      return unrestricted();
    }
    return (root, cq, cb) -> cb.in(root.join("projectTypes").get("id")).value(projectTypeIds);
  }
}
