package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.fixtures.CreditingPeriodDTOMother;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.goldstandard.assurance.api.service.activity.ActivityEnder;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestUpdateApplierTest {

  @InjectMocks RequestUpdateApplier applier;

  @Mock AuthenticatedUserSupplier authenticatedUserSupplier;

  @Mock ActivityEnder activityEnder;

  @Mock RequestMethodologyFactory methodologyFactory;

  @Mock RequestDraftCreditingPeriodFactory draftCreditingPeriodFactory;

  @Mock Clock clock;

  @Test
  void shouldUpdateAllFieldsForNonBatchedRequest() {
    var originalRequest = buildRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    int userId = 456;
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId()).thenReturn(userId);
    var expectedMethodologies = givenMethodologies(update);
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyUpdates(originalRequest, update);

    assertThat(updatedRequest.getUpdatedByUserId()).isEqualTo(userId);
    assertThat(updatedRequest.getSubmittedByUserId()).isEqualTo(userId);
    assertThat(updatedRequest.getCurrentRequestStatus().getId())
        .isEqualTo(update.getRequestStatusId());
    assertThat(updatedRequest.getStartDate()).isEqualTo(update.getStartDate());
    assertThat(updatedRequest.getCreditingPeriod())
        .isEqualTo(update.getCreditingPeriod().getPeriodId());
    assertThat(updatedRequest.getEmissionReductionAmount())
        .isEqualTo(update.getEmissionReductionAmount());
    assertThat(updatedRequest.getManuallyAdded()).isEqualTo(update.getManuallyAdded());
    assertThat(updatedRequest.getFastTracked()).isEqualTo(update.getFastTrack());
    assertThat(updatedRequest.getInternal()).isEqualTo(update.getInternal());
    assertThat(updatedRequest.getRequestMethodologies()).isEqualTo(expectedMethodologies);
  }

  @Test
  void shouldNotUpdateLastSubmittedByUserIdIfStatusIsNotChanged() {
    var originalRequest = buildRequest();
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .requestStatusId(originalRequest.getCurrentRequestStatus().getId())
            .build();
    int userId = 789;
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId()).thenReturn(userId);
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyUpdates(originalRequest, update);

    assertThat(updatedRequest.getSubmittedByUserId()).isNull();
  }

  @Test
  void shouldUpdateAllFieldsForBatchParentRequest() {
    var originalRequest = buildRequest(1);
    originalRequest.setBatch(buildBatchWithParent(buildRequest(1)));
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    int userId = 321;
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId()).thenReturn(userId);
    var expectedMethodologies = givenMethodologies(update);
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyUpdates(originalRequest, update);

    assertThat(updatedRequest.getUpdatedByUserId()).isEqualTo(userId);
    assertThat(updatedRequest.getSubmittedByUserId()).isEqualTo(userId);
    assertThat(updatedRequest.getCurrentRequestStatus().getId())
        .isEqualTo(update.getRequestStatusId());
    assertThat(updatedRequest.getStartDate()).isEqualTo(update.getStartDate());
    assertThat(updatedRequest.getCreditingPeriod())
        .isEqualTo(update.getCreditingPeriod().getPeriodId());
    assertThat(updatedRequest.getEmissionReductionAmount())
        .isEqualTo(update.getEmissionReductionAmount());
    assertThat(updatedRequest.getManuallyAdded()).isEqualTo(update.getManuallyAdded());
    assertThat(updatedRequest.getFastTracked()).isEqualTo(update.getFastTrack());
    assertThat(updatedRequest.getInternal()).isEqualTo(update.getInternal());
    assertThat(updatedRequest.getRequestMethodologies()).isEqualTo(expectedMethodologies);
  }

  @Test
  void shouldOnlyUpdateChildRequestFieldsForBatchChildRequest() {
    var request = buildRequest(1);
    var original = request.toBuilder().build();
    request.setBatch(buildBatchWithParent(buildRequest(2)));
    var update = RequestCreateUpdateDTOMother.build(request.getId());
    int userId = 654;
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId()).thenReturn(userId);

    var updatedRequest = applier.applyUpdates(request, update);

    assertThat(updatedRequest.getUpdatedByUserId()).isEqualTo(userId);
    assertThat(updatedRequest.getCreditingPeriod())
        .isEqualTo(update.getCreditingPeriod().getPeriodId());
    assertThat(updatedRequest.getEmissionReductionAmount())
        .isEqualTo(update.getEmissionReductionAmount());

    assertThat(updatedRequest.getCurrentRequestStatus().getId())
        .isEqualTo(original.getCurrentRequestStatus().getId());
    assertThat(updatedRequest.getStartDate()).isEqualTo(original.getStartDate());
    assertThat(updatedRequest.getEndDate()).isEqualTo(original.getEndDate());
    assertThat(updatedRequest.getBookingDate()).isEqualTo(original.getBookingDate());
    assertThat(updatedRequest.getManuallyAdded()).isEqualTo(original.getManuallyAdded());
    assertThat(updatedRequest.getFastTracked()).isEqualTo(original.getFastTracked());
    assertThat(updatedRequest.getInternal()).isEqualTo(original.getInternal());
    assertThat(updatedRequest.getCurrentActivity()).map(RequestActivity::getEndDate).isEmpty();
  }

  @Test
  void shouldUpdateEndDateIfRequestIsInactiveAndEndDateNotAlreadySet() {
    var originalRequest = buildRequest();
    originalRequest.setEndDate(null);
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .requestStatusId(RequestStatus.CANCELLED_ID)
            .build();
    mockTimeNow();
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyParentUpdates(originalRequest, update);

    assertThat(updatedRequest.getEndDate()).isNotNull();
  }

  @Test
  void shouldNotUpdateEndDateIfRequestIsInactiveAndEndDateIsAlreadySet() {
    var originalRequest = buildRequest(); // End date is set in this build method
    var originalEndDate = originalRequest.getEndDate();
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .requestStatusId(RequestStatus.CANCELLED_ID)
            .build();
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyParentUpdates(originalRequest, update);

    // Assert that the end date has remained unchanged as it was already set before this update
    assertThat(updatedRequest.getEndDate()).isEqualTo(originalEndDate);
  }

  @Test
  void shouldNotUpdateEndDateIfRequestIsActive() {
    var originalRequest = buildRequest();
    originalRequest.setEndDate(null);
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .requestStatusId(RequestStatus.IN_PROGRESS_ID)
            .build();
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyParentUpdates(originalRequest, update);

    assertThat(updatedRequest.getEndDate()).isNull();
  }

  @Test
  void shouldRemoveDraftCreditingPeriodIfExistingCreditingPeriodIdSpecified() {
    var originalRequest = buildRequest();
    originalRequest.setDraftCreditingPeriod(new RequestCreditingPeriod());
    var creditingPeriodIndex = 1;
    var creditingPeriodDto = CreditingPeriodDTOMother.withId(creditingPeriodIndex);
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .requestStatusId(RequestStatus.IN_PROGRESS_ID)
            .creditingPeriod(creditingPeriodDto)
            .build();
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyUpdates(originalRequest, update);

    assertThat(updatedRequest.getDraftCreditingPeriod()).isEmpty();
    assertThat(updatedRequest.getCreditingPeriod()).isEqualTo(creditingPeriodIndex);
  }

  @Test
  void shouldRemoveExistingCreditingPeriodIdIfNewCreditingPeriodSpecified() {
    var originalRequest = buildRequest();
    originalRequest.setCreditingPeriod(1);
    var creditingStartDate = ZonedDateTime.now();
    var creditingPeriodDto =
        CreditingPeriodDTO.builder()
            .startDate(creditingStartDate)
            .endDate(creditingStartDate.plusYears(5))
            .build();
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .requestStatusId(RequestStatus.IN_PROGRESS_ID)
            .creditingPeriod(creditingPeriodDto)
            .build();
    var draftCreditingPeriod =
        RequestCreditingPeriod.builder()
            .requestId(originalRequest.getId())
            .startDate(creditingPeriodDto.getStartDate())
            .endDate(creditingPeriodDto.getEndDate())
            .build();
    when(draftCreditingPeriodFactory.build(originalRequest.getId(), creditingPeriodDto))
        .thenReturn(Optional.of(draftCreditingPeriod));
    givenActivityEnderReturnsUpdatedRequest(originalRequest);

    var updatedRequest = applier.applyUpdates(originalRequest, update);

    assertThat(updatedRequest.getDraftCreditingPeriod())
        .map(RequestCreditingPeriod::getStartDate)
        .contains(draftCreditingPeriod.getStartDate());
    assertThat(updatedRequest.getDraftCreditingPeriod())
        .map(RequestCreditingPeriod::getEndDate)
        .contains(draftCreditingPeriod.getEndDate());
    assertThat(updatedRequest.getCreditingPeriod()).isNull();
  }

  @Test
  void shouldCloseRequestWithReason() {
    var originalRequest = buildRequest();
    var update =
        RequestCreateUpdateDTOMother.build(
            originalRequest.getId(), 8, "Other reason for cancellation");
    int userId = 456;
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId()).thenReturn(userId);
    givenActivityEnderReturnsUpdatedRequest(originalRequest);
    var updatedRequest = applier.applyUpdates(originalRequest, update);
    assertThat(updatedRequest.getCancellationReason().getId()).isEqualTo(8);
    assertThat(updatedRequest.getOtherCancellationReason())
        .isEqualTo("Other reason for cancellation");
  }

  private void givenActivityEnderReturnsUpdatedRequest(Request original) {
    when(activityEnder.endCurrentActivityIfRequired(eq(original), any(Request.class)))
        .thenAnswer(i -> i.getArguments()[1]);
  }

  private List<RequestMethodology> givenMethodologies(RequestCreateUpdateDTO update) {
    var methodologies = List.of(mock(RequestMethodology.class));
    when(methodologyFactory.toMethodologies(eq(update.getMethodologies()), any(Request.class)))
        .thenReturn(methodologies);
    return methodologies;
  }

  private static Batch buildBatchWithParent(Request parentRequest) {
    return Batch.builder().parentRequest(parentRequest).build();
  }

  private static Request buildRequest() {
    return buildRequest(1);
  }

  private static Request buildRequest(Integer id) {
    var request = new Request();
    ZonedDateTime start = ZonedDateTime.now();
    request.setId(id);
    request.setCurrentRequestStatus(new RequestStatus(2));
    request.setStartDate(start);
    request.setEndDate(start.plusDays(2));
    request.setBookingDate(start.plusMinutes(100));
    request.setCreditingPeriod(456);
    request.setEmissionReductionAmount(789);
    request.setManuallyAdded(true);
    request.setRequestType(new RequestType(0));
    request.setCurrentActivity(buildRequestActivity());
    return request;
  }

  private static RequestActivity buildRequestActivity() {
    return RequestActivity.builder().activity(buildActivity()).build();
  }

  private static Activity buildActivity() {
    return new Activity();
  }

  private void mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
  }
}
