package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.PathwayGroup;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestActivityFinderTest {

  @InjectMocks RequestActivityFinder activityFinder;

  @Mock RequestActivityRepository repository;

  @Test
  void shouldReturnNowIfNoActivityIndexIsZero() {
    var activity = Activity.builder().id(49).pathwayGroup(new PathwayGroup()).index(0).build();
    var requestActivity = requestActivityBuilder().activity(activity).build();
    var now = ZonedDateTime.now();

    var startDate = activityFinder.findPathwayGroupStartDateForRequest(requestActivity, now);

    assertThat(startDate).isEqualTo(now);
  }

  @Test
  void shouldReturnNowIfNoActivityFoundForPathway() {
    var activity = Activity.builder().id(50).pathwayGroup(new PathwayGroup()).index(1).build();
    var requestActivity = requestActivityBuilder().activity(activity).build();
    var requestId = requestActivity.getRequestId();
    var pathwayGroup = requestActivity.getActivity().getPathwayGroup();
    when(repository
            .findFirstByRequestIdAndActivity_PathwayGroupAndActivity_IndexOrderByStartDateDesc(
                requestId, pathwayGroup, 0))
        .thenReturn(Optional.empty());
    var now = ZonedDateTime.now();

    var startDate = activityFinder.findPathwayGroupStartDateForRequest(requestActivity, now);

    assertThat(startDate).isEqualTo(now);
  }

  @Test
  void shouldReturnStartDateOfFirstActivityInPathwayIfFoundAndActivityNotPresent() {
    var activity = Activity.builder().id(52).pathwayGroup(new PathwayGroup()).index(1).build();
    var requestActivity = requestActivityBuilder().activity(activity).build();
    var now = ZonedDateTime.now();
    var expectedStartDate = now.minusDays(5);
    var firstActivity =
        requestActivityBuilder()
            .activity(Activity.builder().build())
            .startDate(expectedStartDate)
            .build();
    var requestId = requestActivity.getRequestId();
    var pathwayGroup = requestActivity.getActivity().getPathwayGroup();
    when(repository
            .findFirstByRequestIdAndActivity_PathwayGroupAndActivity_IndexOrderByStartDateDesc(
                requestId, pathwayGroup, 0))
        .thenReturn(Optional.of(firstActivity));

    var startDate = activityFinder.findPathwayGroupStartDateForRequest(requestActivity, now);

    assertThat(startDate).isEqualTo(expectedStartDate);
  }

  private static RequestActivity.RequestActivityBuilder requestActivityBuilder() {
    return RequestActivity.builder().requestId(99);
  }
}
