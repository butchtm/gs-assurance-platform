package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.REVIEWER_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.entity.view.AssignedRequestView;
import org.goldstandard.assurance.api.exception.AccountTypeIdNotSupportedException;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignedRequestAccountFinder {

  private final AssignedRequestFinder assignedRequestFinder;
  private final AccountFinder accountFinder;

  public List<Account> getAssignedRequestAccounts(Integer accountTypeId) {
    var accountIds =
        assignedRequestFinder.getAssignedRequests().stream()
            .map(toAccountIdFunction(accountTypeId))
            .collect(Collectors.toSet());
    return accountFinder.getByIds(accountIds);
  }

  public Optional<Account> getAssignedVvbAccount(Request request) {
    return request.getRequestAccountAssignments().stream()
        .filter(assignment -> assignment.getAssignmentType().isAuditAssignment())
        .findAny()
        .map(RequestAccountAssignment::getAccount);
  }

  private static Function<AssignedRequestView, Integer> toAccountIdFunction(Integer accountTypeId) {
    return switch (accountTypeId) {
      case PROJECT_DEVELOPER_TYPE_ID -> AssignedRequestView::getProjectAccountId;
      case REVIEWER_ACCOUNT_TYPE_ID -> AssignedRequestView::getReviewerAccountId;
      case VVB_ACCOUNT_TYPE_ID -> AssignedRequestView::getVvbAccountId;
      default -> throw new AccountTypeIdNotSupportedException(accountTypeId);
    };
  }
}
