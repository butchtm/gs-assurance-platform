package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestUpdateParams;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.service.external.ExternalUpdateExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestUpdaterTest {

  public static final int PROJECT_ID = 1;

  @InjectMocks RequestUpdater requestUpdater;

  @Mock RequestUpdatePopulator populator;

  @Mock RequestUpdateValidator validator;

  @Mock UpdateRequestTransaction transaction;

  @Mock ExternalUpdateExecutor externalUpdateExecutor;

  @Mock RequestUpdateParamsCalculator updateParamsCalculator;

  @Mock RequestUpdateNotificationSender notificationSender;

  @Mock RequestSalesOrderCreator salesOrderCreator;

  @Captor ArgumentCaptor<RequestUpdateParams> updateParamsCaptor;

  @Captor ArgumentCaptor<Request> requestCaptor;

  @Captor ArgumentCaptor<Collection<Project>> projectsCaptor;

  @Test
  void shouldThrowErrorIfValidatorThrowsError() {
    var request = buildOriginalRequest();
    var update = mock(RequestCreateUpdateDTO.class);
    var populatedUpdate = givenUpdatePopulated(request, update);
    var expectedError =
        new BatchRequestException("Cannot update specific batch child request fields");
    doThrow(expectedError).when(validator).validate(request, populatedUpdate, PROJECT_ID);

    Throwable error = catchThrowable(() -> requestUpdater.update(request, update, PROJECT_ID));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldSaveUpdateParams() {
    var originalRequest = buildOriginalRequest();
    var update = mock(RequestCreateUpdateDTO.class);
    var populatedUpdate = givenUpdatePopulated(originalRequest, update);
    var updatedRequest = mock(Request.class);
    var expectedUpdateParams = buildRequestUpdateParams(updatedRequest);
    when(updateParamsCalculator.toUpdateParams(originalRequest, populatedUpdate))
        .thenReturn(expectedUpdateParams);

    requestUpdater.update(originalRequest, update, PROJECT_ID);

    verify(transaction).updateRequest(updateParamsCaptor.capture());
    assertThat(updateParamsCaptor.getValue()).isEqualTo(expectedUpdateParams);
  }

  @Test
  void shouldPublishProjectStatusToExternalSystemsWhenUpdatedProjectStatusUpdatesCalculated() {
    var originalRequest = buildOriginalRequest();
    var update = mock(RequestCreateUpdateDTO.class);
    var populatedUpdate = givenUpdatePopulated(originalRequest, update);
    var updatedRequest = mock(Request.class);
    var expectedUpdatedProjects = List.of(new Project());
    var updateParams =
        buildRequestUpdateParams(updatedRequest).withUpdatedProjects(expectedUpdatedProjects);
    when(updateParamsCalculator.toUpdateParams(originalRequest, populatedUpdate))
        .thenReturn(updateParams);

    requestUpdater.update(originalRequest, update, PROJECT_ID);

    verify(externalUpdateExecutor).executeAsync(projectsCaptor.capture());
    var updatedProjects = projectsCaptor.getValue();
    assertThat(updatedProjects).isEqualTo(expectedUpdatedProjects);
  }

  @Test
  void shouldTriggerSalesOrderCreation() {
    var originalRequest = buildOriginalRequest();
    var update = mock(RequestCreateUpdateDTO.class);
    var populatedUpdate = givenUpdatePopulated(originalRequest, update);
    var updatedRequest = mock(Request.class);
    var expectedUpdateParams = buildRequestUpdateParams(updatedRequest);
    when(updateParamsCalculator.toUpdateParams(originalRequest, populatedUpdate))
        .thenReturn(expectedUpdateParams);

    requestUpdater.update(originalRequest, update, PROJECT_ID);

    verify(salesOrderCreator).createRequiredSalesOrders(updateParamsCaptor.capture());
    assertThat(updateParamsCaptor.getValue()).isEqualTo(expectedUpdateParams);
  }

  @Test
  void shouldTriggerNotifications() {
    var originalRequest = buildOriginalRequest();
    var update = mock(RequestCreateUpdateDTO.class);
    var populatedUpdate = givenUpdatePopulated(originalRequest, update);
    var updatedRequest = mock(Request.class);
    var updateParams = buildRequestUpdateParams(updatedRequest);
    when(updateParamsCalculator.toUpdateParams(originalRequest, populatedUpdate))
        .thenReturn(updateParams);

    requestUpdater.update(originalRequest, update, PROJECT_ID);

    verify(notificationSender).sendUpdateNotifications(requestCaptor.capture());
    assertThat(requestCaptor.getValue()).isEqualTo(originalRequest);
  }

  private RequestCreateUpdateDTO givenUpdatePopulated(
      Request request, RequestCreateUpdateDTO update) {
    var populatedUpdate = mock(RequestCreateUpdateDTO.class);
    when(populator.populateDefaultsIfRequired(request, update)).thenReturn(populatedUpdate);
    return populatedUpdate;
  }

  private static Request buildOriginalRequest() {
    var request = new Request();
    ZonedDateTime start = ZonedDateTime.now();
    request.setId(1);
    request.setCurrentRequestStatus(new RequestStatus(2));
    request.setStartDate(start);
    request.setEndDate(start.plusDays(2));
    request.setBookingDate(start.plusMinutes(100));
    request.setCreditingPeriod(456);
    request.setEmissionReductionAmount(789);
    request.setManuallyAdded(true);
    request.setRequestType(mock(RequestType.class));
    return request;
  }

  private static RequestUpdateParams buildRequestUpdateParams(Request request) {
    return RequestUpdateParams.builder()
        .updatedRequest(request)
        .updatedChildRequests(Collections.emptyList())
        .consultations(Collections.emptyList())
        .originalProjects(Collections.emptyList())
        .updatedProjects(Collections.emptyList())
        .rounds(Collections.emptyList())
        .activities(Collections.emptyList())
        .build();
  }
}
