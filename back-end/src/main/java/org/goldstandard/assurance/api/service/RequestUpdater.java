package org.goldstandard.assurance.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.external.ExternalUpdateExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestUpdater {

  private final RequestUpdatePopulator populator;
  private final RequestUpdateValidator validator;
  private final UpdateRequestTransaction transaction;
  private final ExternalUpdateExecutor externalUpdateExecutor;
  private final RequestUpdateParamsCalculator updateParamsCalculator;
  private final RequestUpdateNotificationSender notificationSender;
  private final RequestSalesOrderCreator salesOrderCreator;

  public void update(Request originalRequest, RequestCreateUpdateDTO update, Integer projectId) {
    var updateWithDefaults = populator.populateDefaultsIfRequired(originalRequest, update);
    validator.validate(originalRequest, updateWithDefaults, projectId);
    performUpdate(originalRequest, updateWithDefaults);
    notificationSender.sendUpdateNotifications(originalRequest);
  }

  private void performUpdate(Request originalRequest, RequestCreateUpdateDTO update) {
    var updateParams = updateParamsCalculator.toUpdateParams(originalRequest, update);
    transaction.updateRequest(updateParams);
    salesOrderCreator.createRequiredSalesOrders(updateParams);
    externalUpdateExecutor.executeAsync(updateParams.getUpdatedProjects());
  }
}
