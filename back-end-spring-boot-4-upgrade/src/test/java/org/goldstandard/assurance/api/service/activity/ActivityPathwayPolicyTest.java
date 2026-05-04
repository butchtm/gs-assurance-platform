package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.FIRST_FROM_PATHWAY;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.FIRST_FROM_PATHWAY_GROUP;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.NEXT_ACTIVITY;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.NONE;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.SKIP_NEXT_ACTIVITY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivities;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.service.activity.PathwayIndexCalculator.PathwayIndexes;
import org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityPathwayPolicyTest {

  @InjectMocks ActivityPathwayPolicy policy;

  @Mock RequestActivityCreator requestActivityCreator;
  @Mock PathwayIndexCalculator pathwayIndexCalculator;
  @Mock RequiredActivityCalculator requiredActivityCalculator;
  @Mock ActivityFinder activityFinder;
  @Mock AdditionalActivityCreator additionalActivityCreator;

  @Mock Request originalRequest;
  @Mock RequestStatus originalStatus;
  @Mock Request updatedRequest;
  @Mock PathwayIndexes pathwayIndexes;
  @Mock Activity activity;

  @BeforeEach
  void setUp() {
    when(originalRequest.getCurrentRequestStatus()).thenReturn(originalStatus);
  }

  @Test
  void shouldNotReturnActivitiesIfNoPathwayIndexesReturned() {
    when(pathwayIndexCalculator.calculatePathwayIndexes(updatedRequest, originalStatus))
        .thenReturn(Optional.empty());

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).isEmpty();
  }

  @Test
  void shouldNotReturnActivitiesIfRequiredActivityIsNone() {
    givenRequiredActivity(NONE);

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).isEmpty();
  }

  @Test
  void shouldReturnFirstActivityFromPathwayIfRequired() {
    givenRequiredActivity(FIRST_FROM_PATHWAY);
    when(activityFinder.findFirstActivityFromPathway(pathwayIndexes.getPathwayIndex()))
        .thenReturn(Optional.of(activity));
    var expectedRequestActivity = givenExpectedRequestActivity();
    givenNoAdditionalActivitiesRequired(expectedRequestActivity);

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).containsExactly(expectedRequestActivity);
  }

  @Test
  void shouldReturnFirstActivityFromPathwayGroupIfRequired() {
    givenRequiredActivity(FIRST_FROM_PATHWAY_GROUP);
    when(activityFinder.findFirstActivityFromPathwayGroup(
            pathwayIndexes.getPathwayIndex(), pathwayIndexes.getRoundTwoPathwayGroupIndex()))
        .thenReturn(Optional.of(activity));
    var expectedRequestActivity = givenExpectedRequestActivity();
    givenNoAdditionalActivitiesRequired(expectedRequestActivity);

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).containsExactly(expectedRequestActivity);
  }

  @Test
  void shouldReturnNextActivityIfRequiredAndCurrentActivityIsComplete() {
    RequestActivity currentRequestActivity = givenRequestHasCurrentRequestActivity();
    when(currentRequestActivity.isComplete()).thenReturn(true);
    Activity currentActivity = givenActivity(currentRequestActivity);
    givenRequiredActivity(NEXT_ACTIVITY);
    when(activityFinder.findNextActivity(currentActivity)).thenReturn(Optional.of(activity));
    var expectedRequestActivity = givenExpectedRequestActivity();
    givenNoAdditionalActivitiesRequired(expectedRequestActivity);

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).containsExactly(expectedRequestActivity);
  }

  @Test
  void shouldNotReturnNextActivityIfRequiredAndCurrentActivityIsNotComplete() {
    RequestActivity currentRequestActivity = givenRequestHasCurrentRequestActivity();
    when(currentRequestActivity.isComplete()).thenReturn(false);
    Activity currentActivity = givenActivity(currentRequestActivity);
    givenRequiredActivity(NEXT_ACTIVITY);
    when(activityFinder.findNextActivity(currentActivity)).thenReturn(Optional.of(activity));

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).isEmpty();
  }

  @Test
  void shouldReturnActivityAfterNextIfSkipRequiredAndCurrentActivityIsComplete() {
    RequestActivity currentRequestActivity = givenRequestHasCurrentRequestActivity();
    when(currentRequestActivity.isComplete()).thenReturn(true);
    Activity currentActivity = givenActivity(currentRequestActivity);
    givenRequiredActivity(SKIP_NEXT_ACTIVITY);
    when(activityFinder.findActivityAfterNext(currentActivity)).thenReturn(Optional.of(activity));
    var expectedRequestActivity = givenExpectedRequestActivity();
    givenNoAdditionalActivitiesRequired(expectedRequestActivity);

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).containsExactly(expectedRequestActivity);
  }

  @Test
  void shouldNotReturnActivityAfterNextIfSkipRequiredAndCurrentActivityNotComplete() {
    RequestActivity currentRequestActivity = givenRequestHasCurrentRequestActivity();
    when(currentRequestActivity.isComplete()).thenReturn(false);
    Activity currentActivity = givenActivity(currentRequestActivity);
    givenRequiredActivity(SKIP_NEXT_ACTIVITY);
    when(activityFinder.findActivityAfterNext(currentActivity)).thenReturn(Optional.of(activity));

    var requestActivities = policy.createNextActivitiesIfRequired(originalRequest, updatedRequest);

    assertThat(requestActivities).isEmpty();
  }

  private void givenRequiredActivity(RequiredActivity requiredActivity) {
    when(pathwayIndexCalculator.calculatePathwayIndexes(updatedRequest, originalStatus))
        .thenReturn(Optional.of(pathwayIndexes));
    when(requiredActivityCalculator.calculateRequiredActivity(updatedRequest, originalStatus))
        .thenReturn(requiredActivity);
  }

  private RequestActivity givenRequestHasCurrentRequestActivity() {
    RequestActivity requestActivity = mock(RequestActivity.class);
    when(updatedRequest.getCurrentActivity()).thenReturn(Optional.of(requestActivity));
    return requestActivity;
  }

  private Activity givenActivity(RequestActivity requestActivity) {
    Activity activity = mock(Activity.class);
    when(requestActivity.getActivity()).thenReturn(activity);
    return activity;
  }

  private RequestActivity givenExpectedRequestActivity() {
    RequestActivity requestActivity = new RequestActivity();
    when(requestActivityCreator.buildRequestActivity(activity, updatedRequest))
        .thenReturn(requestActivity);
    return requestActivity;
  }

  private void givenNoAdditionalActivitiesRequired(RequestActivity requestActivity) {
    when(additionalActivityCreator.includeAdditionalActivitiesIfRequired(
            originalRequest, updatedRequest, requestActivity))
        .thenReturn(new RequestActivities(requestActivity));
  }
}
