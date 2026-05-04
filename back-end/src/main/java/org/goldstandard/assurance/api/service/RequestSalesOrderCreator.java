package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestUpdateParams;
import org.goldstandard.assurance.api.service.finance.FinanceService;
import org.goldstandard.assurance.api.util.RequestStatusChecker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestSalesOrderCreator {

  private final FinanceService financeService;
  private final RequestStatusChecker statusChecker;

  public void createRequiredSalesOrders(RequestUpdateParams params) {
    var originalAndUpdatedRequests = params.toOriginalAndUpdatedRequests();
    originalAndUpdatedRequests.forEach(this::raiseSalesOrderIfPassedCompletenessCheck);
  }

  private void raiseSalesOrderIfPassedCompletenessCheck(Request original, Request updated) {
    if (hasPassedCompletenessCheck(original, updated)) {
      financeService.triggerRequestSalesOrderIfPossible(updated);
    }
  }

  private boolean hasPassedCompletenessCheck(Request original, Request updated) {
    var passed = statusChecker.movedToInAssignmentFromCompletenessCheck(original, updated);
    log.debug("request {} passed completeness check {}", updated.getId(), passed);
    return passed;
  }
}
