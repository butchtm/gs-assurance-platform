package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.fixtures.ActivityMother.createActivity;
import static org.goldstandard.assurance.api.fixtures.PathwayMother.createPathway;
import static org.goldstandard.assurance.api.fixtures.PathwayMother.createPathwayGroup;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.repository.activity.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityFinderTest {

  @InjectMocks ActivityFinder finder;

  @Mock ActivityRepository repository;
  @Mock PathwayGroupFinder pathwayGroupFinder;

  @Test
  void shouldReturnFirstActivityFromPathway() {
    var pathway = createPathway(1);
    var group = createPathwayGroup(0, pathway.getIndex());
    var firstActivity = createActivity(0, group.getIndex(), pathway.getIndex());
    when(pathwayGroupFinder.findFirstPathwayGroup(pathway.getIndex()))
        .thenReturn(Optional.of(group));
    when(repository.findFirstByPathwayGroupOrderByIndexAsc(group))
        .thenReturn(Optional.of(firstActivity));

    var activity = finder.findFirstActivityFromPathway(pathway.getIndex());

    assertThat(activity).contains(firstActivity);
  }

  @Test
  void shouldReturnFirstActivityFromPathwayGroup() {
    var pathway = createPathway(1);
    var group = createPathwayGroup(1, pathway.getIndex());
    var firstActivity = createActivity(0, group.getIndex(), pathway.getIndex());
    when(pathwayGroupFinder.findByPathwayAndIndex(pathway.getIndex(), group.getIndex()))
        .thenReturn(Optional.of(group));
    when(repository.findFirstByPathwayGroupOrderByIndexAsc(group))
        .thenReturn(Optional.of(firstActivity));

    var activity = finder.findFirstActivityFromPathwayGroup(pathway.getIndex(), group.getIndex());

    assertThat(activity).contains(firstActivity);
  }

  @Test
  void shouldReturnNextActivityIfExists() {
    var activity = createActivity(0, 0, 0);
    var expectedActivity = createActivity(1, 0, 0);
    when(repository.findByPathwayGroupAndIndex(activity.getPathwayGroup(), activity.getIndex() + 1))
        .thenReturn(Optional.of(expectedActivity));

    var nextActivity = finder.findNextActivity(activity);

    assertThat(nextActivity).contains(expectedActivity);
  }

  @Test
  void shouldReturnEmptyIfNextActivityDoesNotExist() {
    var activity = createActivity(0, 0, 0);
    when(repository.findByPathwayGroupAndIndex(activity.getPathwayGroup(), activity.getIndex() + 1))
        .thenReturn(Optional.empty());

    var nextActivity = finder.findNextActivity(activity);

    assertThat(nextActivity).isEmpty();
  }

  @Test
  void shouldReturnActivityAfterNextIfSkipped() {
    var activity = createActivity(0, 0, 0);
    var expectedActivity = createActivity(2, 0, 0);
    when(repository.findByPathwayGroupAndIndex(activity.getPathwayGroup(), activity.getIndex() + 2))
        .thenReturn(Optional.of(expectedActivity));

    var skippedNextActivity = finder.findActivityAfterNext(activity);

    assertThat(skippedNextActivity).contains(expectedActivity);
  }

  @ParameterizedTest
  @MethodSource("activityIndexAndExpectedDurationDays")
  void shouldReturnElapsedPlannedDaysAtEndOfActivity(int activityIndex, int expectedDays) {
    var activity = createActivity(activityIndex, 0, 0);
    var groupActivities = buildPathwayGroupActivities();
    when(repository.findByPathwayGroupOrderByIndexAsc(activity.getPathwayGroup()))
        .thenReturn(groupActivities);

    int days = finder.getElapsedPlannedDaysAtEndOfActivity(activity);

    assertThat(days).isEqualTo(expectedDays);
  }

  private static Stream<Arguments> activityIndexAndExpectedDurationDays() {
    return Stream.of(
        Arguments.of(0, 1),
        Arguments.of(1, 3),
        Arguments.of(2, 6),
        Arguments.of(3, 10),
        Arguments.of(4, 10));
  }

  private static Collection<Activity> buildPathwayGroupActivities() {
    return List.of(
        buildPathwayGroupActivity(0, 1),
        buildPathwayGroupActivity(1, 2),
        buildPathwayGroupActivity(2, 3),
        buildPathwayGroupActivity(3, 4));
  }

  private static Activity buildPathwayGroupActivity(int index, int totalDays) {
    return Activity.builder().index(index).totalDays(totalDays).build();
  }
}
