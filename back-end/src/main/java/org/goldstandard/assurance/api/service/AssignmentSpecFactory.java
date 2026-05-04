package org.goldstandard.assurance.api.service;

import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignmentSpecFactory {

  private static final String ACCOUNT = "account";
  private static final String ACCOUNT_TYPE = "accountType";
  private static final String ASSIGNMENT_TYPE = "assignmentType";
  private static final String ASSIGNMENT_STATUS = "assignmentStatus";
  private static final String ID = "id";
  private static final String REQUEST_ID = "requestId";

  public static Specification<RequestAccountAssignment> toSpec(AssignmentFilters filters) {
    Specification<RequestAccountAssignment> spec = unrestricted();
    spec = and(spec, AssignmentSpecFactory::hasRequestId, filters.getRequestId());
    spec = and(spec, AssignmentSpecFactory::hasAssignmentStatusId, filters.getAssignmentStatusId());
    spec = and(spec, AssignmentSpecFactory::hasAccountTypeId, filters.getAccountTypeId());
    spec = and(spec, AssignmentSpecFactory::hasAccountId, filters.getAccountId());
    spec =
        and(spec, AssignmentSpecFactory::hasAnyOfAssignmentTypeIds, filters.getAssignmentTypeIds());
    spec = and(spec, AssignmentSpecFactory::hasAssignmentId, filters.getAssignmentId());
    return spec;
  }

  public static Specification<RequestAccountAssignment> hasAccountId(Integer accountId) {
    return (assignment, cq, cb) -> cb.equal(assignment.get(ACCOUNT).get(ID), accountId);
  }

  public static Specification<RequestAccountAssignment> hasRequestIdExcludingAccountTypes(
      Integer requestId, Collection<Integer> accountTypeIds) {
    var spec = AssignmentSpecFactory.hasRequestId(requestId);
    if (accountTypeIds.isEmpty()) {
      return spec;
    }
    return spec.and(
        Specification.not(AssignmentSpecFactory.hasAnyOfAccountTypeIds(accountTypeIds)));
  }

  public static Specification<RequestAccountAssignment> hasRequestId(Integer requestId) {
    return (assignment, cq, cb) -> cb.equal(assignment.get(REQUEST_ID), requestId);
  }

  public static Specification<RequestAccountAssignment> hasAssignmentTypeId(
      Integer assignmentTypeId) {
    return hasAnyOfAssignmentTypeIds(Set.of(assignmentTypeId));
  }

  public static Specification<RequestAccountAssignment> hasAnyOfAssignmentTypeIds(
      Collection<Integer> assignmentTypeIds) {
    if (CollectionUtils.isEmpty(assignmentTypeIds)) {
      return Specification.unrestricted();
    }
    return (assignment, cq, cb) ->
        cb.in(assignment.get(ASSIGNMENT_TYPE).get(ID)).value(assignmentTypeIds);
  }

  public static Specification<RequestAccountAssignment> hasAssignmentId(Integer assignmentId) {
    return (assignment, cq, cb) -> cb.equal(assignment.get(ID), assignmentId);
  }

  public static Specification<RequestAccountAssignment> hasAccountTypeId(Integer accountTypeId) {
    return (assignment, cq, cb) ->
        cb.equal(assignment.join(ACCOUNT).get(ACCOUNT_TYPE).get(ID), accountTypeId);
  }

  public static Specification<RequestAccountAssignment> hasAssignmentStatusId(
      Integer assignmentStatusId) {
    return (assignment, cq, cb) ->
        cb.equal(assignment.get(ASSIGNMENT_STATUS).get(ID), assignmentStatusId);
  }

  public static Specification<RequestAccountAssignment> hasAnyOfAccountTypeIds(
      Collection<Integer> accountTypeIds) {
    return (assignment, cq, cb) ->
        cb.in(assignment.join(ACCOUNT).get(ACCOUNT_TYPE).get(ID)).value(accountTypeIds);
  }

  private static <I, O> Specification<O> and(
      Specification<O> spec1, Function<I, Specification<O>> spec2, I input) {
    if (Objects.nonNull(input)) {
      return spec1.and(spec2.apply(input));
    }
    return spec1;
  }
}
