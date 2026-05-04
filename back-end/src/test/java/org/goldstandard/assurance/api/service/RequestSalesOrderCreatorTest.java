package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestUpdateParams;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.service.finance.FinanceService;
import org.goldstandard.assurance.api.util.RequestStatusChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestSalesOrderCreatorTest {

  @InjectMocks RequestSalesOrderCreator creator;

  @Mock FinanceService financeService;

  @Mock RequestStatusChecker statusChecker;

  @Test
  void shouldNotCreateSalesOrderIfRequestHasNotPassedCompletenessCheck() {
    var params = buildRequestUpdateParams();
    var original = params.getOriginalRequest();
    var updated = params.getUpdatedRequest();
    var originalChild = IterableUtils.get(params.getOriginalChildRequests(), 0);
    var updatedChild = IterableUtils.get(params.getUpdatedChildRequests(), 0);
    when(statusChecker.movedToInAssignmentFromCompletenessCheck(original, updated))
        .thenReturn(false);
    when(statusChecker.movedToInAssignmentFromCompletenessCheck(originalChild, updatedChild))
        .thenReturn(false);

    creator.createRequiredSalesOrders(params);

    verifyNoInteractions(financeService);
  }

  @Test
  void shouldCreateSalesOrderIfRequestHasNotPassedCompletenessCheck() {
    var params = buildRequestUpdateParams();
    var original = params.getOriginalRequest();
    var updated = params.getUpdatedRequest();
    var originalChild = IterableUtils.get(params.getOriginalChildRequests(), 0);
    var updatedChild = IterableUtils.get(params.getUpdatedChildRequests(), 0);
    when(statusChecker.movedToInAssignmentFromCompletenessCheck(original, updated))
        .thenReturn(true);
    when(statusChecker.movedToInAssignmentFromCompletenessCheck(originalChild, updatedChild))
        .thenReturn(true);

    creator.createRequiredSalesOrders(params);

    var captor = ArgumentCaptor.forClass(Request.class);
    verify(financeService, times(2)).triggerRequestSalesOrderIfPossible(captor.capture());
    assertThat(captor.getAllValues()).containsExactlyInAnyOrder(updated, updatedChild);
  }

  private RequestUpdateParams buildRequestUpdateParams() {
    return RequestUpdateParams.builder()
        .originalRequest(RequestMother.withId(1))
        .updatedRequest(RequestMother.withId(1))
        .originalChildRequests(List.of(RequestMother.withId(2)))
        .updatedChildRequests(List.of(RequestMother.withId(2)))
        .build();
  }
}
