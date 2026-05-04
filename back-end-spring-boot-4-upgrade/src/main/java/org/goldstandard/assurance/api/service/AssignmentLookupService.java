package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.AUDIT_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.COMPLETENESS_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.util.CustomCollectionUtils.toCollection;

import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.service.AssignmentFilters.AssignmentFiltersBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentLookupService {

  private final AssignmentFinder assignmentFinder;
  private final AssignmentPermissionsService assignmentPermissionsService;
  private final PermissionsService permissionsService;

  public Collection<RequestAccountAssignment> getAssignments(AssignmentFilters filters) {
    assignmentPermissionsService.validateCanReadRequestAssignments(filters.getRequestId());
    var updatedFilters = updateFiltersIfRequired(filters);
    return assignmentFinder.find(updatedFilters);
  }

  public Page<RequestAccountAssignment> findAllAssignments(
      Integer accountId, Integer requestId, Integer assignmentTypeId, Pageable pageable) {
    assignmentPermissionsService.validateCanReadAccountAssignments(accountId);
    var filters =
        AssignmentFilters.builder()
            .accountId(accountId)
            .requestId(requestId)
            .assignmentTypeIds(toCollection(assignmentTypeId))
            .build();
    return assignmentFinder.findPage(filters, pageable);
  }

  public Collection<RequestAccountAssignment> findAssignments(Integer accountId, Request request) {
    var filters =
        AssignmentFilters.builder()
            .accountId(accountId)
            .requestId(request.getParentIfChild().getId())
            .build();
    return assignmentFinder.findAll(filters);
  }

  public RequestAccountAssignment forceFindAssignmentById(Integer assignmentId) {
    return assignmentFinder.forceFindAssignmentById(assignmentId);
  }

  private AssignmentFilters updateFiltersIfRequired(AssignmentFilters filters) {
    AssignmentFiltersBuilder filtersBuilder = filters.toBuilder();
    if (assignmentPermissionsService.projectOwner(filters.getRequestId())) {
      filtersBuilder = overrideAssignmentTypeIds(filters, filtersBuilder);
    }
    if (assignmentPermissionsService.canReadOwnAccountAssignments()) {
      permissionsService.getAccountIdForUser().ifPresent(filtersBuilder::accountId);
    }
    return filtersBuilder.build();
  }

  private static AssignmentFiltersBuilder overrideAssignmentTypeIds(
      AssignmentFilters filters, AssignmentFiltersBuilder builder) {
    var requestedTypeIds = filters.getAssignmentTypeIds();
    var pdTypeIds = Set.of(AUDIT_ASSIGNMENT_TYPE_ID, COMPLETENESS_ASSIGNMENT_TYPE_ID);
    log.warn(
        "overriding requested assignment type ids {} with project developer assignment type ids {}",
        requestedTypeIds,
        pdTypeIds);
    return builder.assignmentTypeIds(pdTypeIds);
  }
}
