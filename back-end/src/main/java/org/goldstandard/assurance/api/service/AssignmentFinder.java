package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.AUDIT_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.QAQC_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.REVIEW_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.service.AssignmentSpecFactory.hasAccountId;
import static org.goldstandard.assurance.api.service.AssignmentSpecFactory.hasAnyOfAccountTypeIds;
import static org.goldstandard.assurance.api.service.AssignmentSpecFactory.hasAssignmentTypeId;
import static org.goldstandard.assurance.api.service.AssignmentSpecFactory.hasRequestId;
import static org.goldstandard.assurance.api.service.AssignmentSpecFactory.toSpec;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.ReviewerNotAssignedToRequestException;
import org.goldstandard.assurance.api.projection.AssignedAccount;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class AssignmentFinder {

  private final RequestAccountAssignmentRepository repository;

  public Collection<Account> findAssignedAccountsByRequestId(
      Integer requestId, Collection<Integer> excludeAccountTypes) {
    var spec =
        AssignmentSpecFactory.hasRequestIdExcludingAccountTypes(requestId, excludeAccountTypes);
    return findAssignedAccounts(spec);
  }

  public boolean hasAuditAssignment(Integer requestId) {
    return !findAssignedAuditAccountsByRequestId(requestId).isEmpty();
  }

  public Collection<Account> findAssignedAuditAccountsByRequestId(Integer requestId) {
    return findAssignedAccountsByRequestIdAndAssignmentTypeId(requestId, AUDIT_ASSIGNMENT_TYPE_ID);
  }

  public Collection<Account> findAssignedAccountsByRequestIdAndAssignmentTypeId(
      Integer requestId, Integer assignmentTypeId) {
    return findAssignedAccounts(hasRequestId(requestId).and(hasAssignmentTypeId(assignmentTypeId)));
  }

  public Account forceFindLatestAccountAssignedToReviewRequest(Integer requestId) {
    return findLatestAccountAssignedToReviewRequest(requestId)
        .orElseThrow(() -> new ReviewerNotAssignedToRequestException(requestId));
  }

  public Optional<Account> findLatestAccountAssignedToReviewRequest(Integer requestId) {
    return findLatestAccountAssignedToRequest(requestId, REVIEW_ASSIGNMENT_TYPE_ID);
  }

  public Optional<Account> findLatestAccountAssignedToQAQCRequest(Integer requestId) {
    return findLatestAccountAssignedToRequest(requestId, QAQC_ASSIGNMENT_TYPE_ID);
  }

  public Collection<Account> findAssignedAccountsByRequestIdAndAccountTypeIds(
      Integer requestId, Collection<Integer> accountTypeIds) {
    return findAssignedAccounts(
        hasRequestId(requestId).and(hasAnyOfAccountTypeIds(accountTypeIds)));
  }

  public boolean exists(AssignmentFilters filters) {
    return repository.exists(toSpec(filters));
  }

  public Collection<RequestAccountAssignment> findAll(AssignmentFilters filters) {
    return repository.findAll(toSpec(filters));
  }

  public Page<RequestAccountAssignment> findPage(AssignmentFilters filters, Pageable pageable) {
    return repository.findAll(toSpec(filters), pageable);
  }

  public Collection<RequestAccountAssignment> find(AssignmentFilters filters) {
    return repository.findAll(toSpec(filters));
  }

  public Collection<RequestAccountAssignment> findByRequestId(Integer requestId) {
    return repository.findAll(hasRequestId(requestId));
  }

  public boolean hasAssignmentOfType(Integer requestId, Integer assignmentTypeId) {
    return repository.exists(hasRequestId(requestId).and(hasAssignmentTypeId(assignmentTypeId)));
  }

  public Collection<Integer> getAllAssignedRequestIds(Integer accountId) {
    return repository.findAll(hasAccountId(accountId)).stream()
        .map(RequestAccountAssignment::getRequestId)
        .toList();
  }

  public boolean isRequestAssignedToAccount(Integer requestId, Integer accountId) {
    var spec = hasRequestId(requestId).and(hasAccountId(accountId));
    var exists = repository.exists(spec);
    log.debug("assignments for request {} and account {} exist {}", requestId, accountId, exists);
    return exists;
  }

  public RequestAccountAssignment forceFindAssignmentById(Integer assignmentId) {
    return findByAssignmentId(assignmentId)
        .orElseThrow(() -> new NotFoundException("assignment with id %s not found", assignmentId));
  }

  public Optional<RequestAccountAssignment> findByAssignmentId(Integer assignmentId) {
    return repository.findById(assignmentId);
  }

  private Optional<Account> findLatestAccountAssignedToRequest(
      Integer requestId, Integer assignmentTypeId) {
    return repository
        .findFirstByRequestIdAndAssignmentType_IdOrderByUpdatedTimeDesc(requestId, assignmentTypeId)
        .map(RequestAccountAssignment::getAccount);
  }

  private Collection<Account> findAssignedAccounts(Specification<RequestAccountAssignment> spec) {
    return repository
        .findBy(spec, q -> q.project("account").as(AssignedAccount.class).all())
        .stream()
        .map(AssignedAccount::getAccount)
        .toList();
  }
}
