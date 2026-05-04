package org.goldstandard.assurance.api.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.COMPLETED_APPROVED_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_PROGRESS_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestStatus.IN_ROUND_COMPLETION_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.activity.RequestActivities;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.goldstandard.assurance.api.service.activity.ActivityPathwayPolicy;
import org.goldstandard.assurance.api.service.conversation.ConversationFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestUpdateParamsCalculatorTest {

  @InjectMocks RequestUpdateParamsCalculator updateParamsCalculator;

  @Mock RequestUpdateApplier updateApplier;

  @Mock StakeholderConsultationFactory consultationFactory;

  @Mock BatchRequestFinder batchRequestFinder;

  @Mock ProjectStatusUpdatePolicy projectStatusUpdatePolicy;

  @Mock RoundOfReviewFactory roundOfReviewFactory;

  @Mock ActivityPathwayPolicy activityPathwayPolicy;

  @Mock ProjectUpdateFinder projectUpdateFinder;

  @Mock ProjectCreditingPeriodUpdatePolicy projectCreditingPeriodUpdatePolicy;

  @Mock ConversationFinder conversationFinder;

  @Test
  void shouldUpdateAllRequestFieldsBeforeSavingForNonBatchedRequest() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getUpdatedRequest()).isEqualTo(updatedRequest);
  }

  @Test
  void shouldUpdateProjectStatusWhenUpdatedProjectStatusUpdatesCalculated() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    var originalProjects = List.of(new Project());
    when(projectUpdateFinder.toProjectsAppendingLegacyBatchProjectsIfPresent(
            updatedRequest, emptyList()))
        .thenReturn(originalProjects);
    var expectedUpdatedProjects = List.of(new Project());
    when(projectStatusUpdatePolicy.calculateProjectStatusUpdates(originalProjects, updatedRequest))
        .thenReturn(expectedUpdatedProjects);
    when(projectCreditingPeriodUpdatePolicy.calculateProjectCreditingPeriodUpdates(
            List.of(originalRequest), List.of(updatedRequest), expectedUpdatedProjects))
        .thenReturn(expectedUpdatedProjects);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getUpdatedProjects()).isEqualTo(expectedUpdatedProjects);
  }

  @Test
  void shouldUpdateProjectMethodologiesWhenUpdatedProjectMethodologyUpdatesCalculated() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    var originalProjects = List.of(new Project());
    when(projectUpdateFinder.toProjectsAppendingLegacyBatchProjectsIfPresent(
            updatedRequest, emptyList()))
        .thenReturn(originalProjects);
    var updatedProjects = List.of(new Project());
    when(projectStatusUpdatePolicy.calculateProjectStatusUpdates(originalProjects, updatedRequest))
        .thenReturn(updatedProjects);
    var expectedUpdatedProjects = List.of(new Project());
    when(projectCreditingPeriodUpdatePolicy.calculateProjectCreditingPeriodUpdates(
            List.of(originalRequest), List.of(updatedRequest), updatedProjects))
        .thenReturn(expectedUpdatedProjects);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getUpdatedProjects()).isEqualTo(expectedUpdatedProjects);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false})
  @NullSource
  void shouldNotBuildConsultationsIfStartConsultationNotRequested(Boolean startConsultation) {
    var originalRequest = buildOriginalRequest();
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .startStakeholderConsultation(startConsultation)
            .build();
    var updatedRequest = buildUpdatedRequest();
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    updateParamsCalculator.toUpdateParams(originalRequest, update);

    verifyNoInteractions(consultationFactory);
  }

  @Test
  void shouldBuildConsultationsStartConsultationRequested() {
    var originalRequest = buildOriginalRequest();
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .startStakeholderConsultation(true)
            .build();
    List<StakeholderConsultation> consultations = List.of(new StakeholderConsultation());
    Request updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    when(consultationFactory.buildConsultations(updatedRequest)).thenReturn(consultations);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getConsultations()).isEqualTo(consultations);
  }

  @Test
  void shouldNotBuildConsultationsIfRequestIsDeltaExcluded() {
    var originalRequest = buildOriginalRequest();
    var update =
        RequestCreateUpdateDTOMother.builder(originalRequest.getId())
            .startStakeholderConsultation(true)
            .build();
    var updatedRequest = buildUpdatedRequest();
    updatedRequest.setDeltaExcluded(true);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    updateParamsCalculator.toUpdateParams(originalRequest, update);

    verifyNoInteractions(consultationFactory);
  }

  @Test
  void shouldOnlyUpdateChildFieldsWhenChildRequestUpdatedDirectly() {
    var parentRequest = buildOriginalRequest(17);
    var batch = buildBatch(parentRequest);
    parentRequest.setBatch(batch);
    var originalChildRequest = buildOriginalRequest(18);
    originalChildRequest.setBatch(batch);
    var update = RequestCreateUpdateDTOMother.build(originalChildRequest.getId());
    var updatedChildRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalChildRequest, update)).thenReturn(updatedChildRequest);
    givenNoNextActivitiesRequired(originalChildRequest, updatedChildRequest);

    var params = updateParamsCalculator.toUpdateParams(originalChildRequest, update);

    assertThat(params.getUpdatedRequest()).isEqualTo(updatedChildRequest);
    assertThat(params.getUpdatedChildRequests()).isEmpty();
  }

  @Test
  void shouldApplyParentUpdatesToChildRequestWhenBatchParentRequestUpdated() {
    var originalParentRequest = buildOriginalRequest(19);
    var batch = buildBatch(originalParentRequest);
    originalParentRequest.setBatch(batch);
    var childRequest = buildOriginalRequest(20);
    childRequest.setBatch(batch);
    var update = RequestCreateUpdateDTOMother.build(originalParentRequest.getId());
    when(batchRequestFinder.getBatchChildRequests(originalParentRequest))
        .thenReturn(List.of(childRequest));
    var updatedParentRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalParentRequest, update))
        .thenReturn(updatedParentRequest);
    var updatedChildRequest = mock(Request.class);
    when(updateApplier.applyParentUpdates(childRequest, update)).thenReturn(updatedChildRequest);
    givenNoNextActivitiesRequired(originalParentRequest, updatedParentRequest);

    var params = updateParamsCalculator.toUpdateParams(originalParentRequest, update);

    assertThat(params.getUpdatedRequest()).isEqualTo(updatedParentRequest);
    assertThat(params.getUpdatedChildRequests()).containsExactly(updatedChildRequest);
  }

  @Test
  void shouldNotSaveAnyRequestActivitiesIfNoCurrentActivityPresentAndNextActivityNotRequired() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getActivities()).isEmpty();
  }

  @Test
  void shouldSaveRequestCurrentActivityIfPresent() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    var currentActivity = mock(RequestActivity.class);
    when(updatedRequest.getCurrentActivity()).thenReturn(Optional.of(currentActivity));
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getActivities()).containsExactly(currentActivity);
  }

  @Test
  void shouldSaveNextActivitiesIfRequired() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    var nextActivities = buildNextActivities();
    when(activityPathwayPolicy.createNextActivitiesIfRequired(originalRequest, updatedRequest))
        .thenReturn(nextActivities);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getActivities()).containsExactlyElementsOf(nextActivities);
  }

  @Test
  void shouldSaveCurrentRoundClosedIfRequired() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);

    var closedRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.CLOSED));
    when(roundOfReviewFactory.toRoundsOfReview(originalRequest, updatedRequest))
        .thenReturn(List.of(closedRound));
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getRounds()).containsExactly(closedRound);
  }

  @Test
  void shouldSaveNewRoundWhenNoOpenRound() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);

    var newRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.OPEN));
    when(roundOfReviewFactory.toRoundsOfReview(originalRequest, updatedRequest))
        .thenReturn(List.of(newRound));
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getRounds()).containsExactly(newRound);
  }

  @Test
  void shouldSaveClosedRoundAndNewOpenRound() {
    var originalRequest = buildOriginalRequest();
    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    var updatedRequest = mock(Request.class);
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    var closedRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.CLOSED));
    var newRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.OPEN));
    when(roundOfReviewFactory.toRoundsOfReview(originalRequest, updatedRequest))
        .thenReturn(List.of(closedRound, newRound));
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getRounds()).containsExactly(closedRound, newRound);
  }

  @Test
  void shouldCloseConversationWhenRequestClosed() {
    var originalRequest = buildOriginalRequest();
    originalRequest.setCurrentRequestStatus(new RequestStatus(IN_ROUND_COMPLETION_ID));
    var updatedRequest = buildUpdatedRequest();
    updatedRequest.setCurrentRequestStatus(new RequestStatus(COMPLETED_APPROVED_ID));

    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var conversation = buildConversation(updatedRequest.getId(), ConversationStatus.buildOpen());
    when(conversationFinder.findByRequestId(updatedRequest.getId()))
        .thenReturn(Optional.of(conversation));

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    var updatedConversation = params.getConversation().orElseThrow();
    assertThat(updatedConversation)
        .usingRecursiveComparison()
        .ignoringFields("status")
        .isEqualTo(conversation);
    assertThat(updatedConversation.getStatus().isClosed()).isTrue();
  }

  @Test
  void shouldNotCloseConversationWhenRequestNotClosed() {
    var originalRequest = buildOriginalRequest();
    originalRequest.setCurrentRequestStatus(new RequestStatus(IN_PROGRESS_ID));
    var updatedRequest = buildUpdatedRequest();
    updatedRequest.setCurrentRequestStatus(new RequestStatus(IN_ROUND_COMPLETION_ID));

    var update = RequestCreateUpdateDTOMother.build(originalRequest.getId());
    when(updateApplier.applyUpdates(originalRequest, update)).thenReturn(updatedRequest);
    givenNoNextActivitiesRequired(originalRequest, updatedRequest);

    var conversation = buildConversation(updatedRequest.getId(), ConversationStatus.buildOpen());
    when(conversationFinder.findByRequestId(updatedRequest.getId()))
        .thenReturn(Optional.of(conversation));

    var params = updateParamsCalculator.toUpdateParams(originalRequest, update);

    assertThat(params.getConversation()).isEmpty();
  }

  private void givenNoNextActivitiesRequired(Request originalRequest, Request updatedRequest) {
    when(activityPathwayPolicy.createNextActivitiesIfRequired(originalRequest, updatedRequest))
        .thenReturn(new RequestActivities());
  }

  private static RequestActivities buildNextActivities() {
    return RequestActivities.builder()
        .next(mock(RequestActivity.class))
        .others(List.of(mock(RequestActivity.class), mock(RequestActivity.class)))
        .build();
  }

  private static Batch buildBatch(Request parentRequest) {
    return Batch.builder().batchType(new BatchType(0)).parentRequest(parentRequest).id(0).build();
  }

  private static Request buildOriginalRequest() {
    return buildOriginalRequest(1);
  }

  private static Request buildOriginalRequest(int id) {
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
    request.setRequestType(mock(RequestType.class));
    return request;
  }

  private static Request buildUpdatedRequest() {
    var request = new Request();
    request.setCurrentRequestStatus(new RequestStatus(3));
    return request;
  }

  private static RequestRound buildRequestRound(RequestRoundStatus requestRoundStatus) {
    return RequestRound.builder().id(1001).status(requestRoundStatus).build();
  }

  private static Conversation buildConversation(Integer requestId, ConversationStatus status) {
    return Conversation.builder().id(1).requestId(requestId).status(status).build();
  }
}
