package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.converter.PathwayGroupTimelineConverter;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.PathwayGroupTimeline;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.goldstandard.assurance.api.service.activity.PathwayGroupTimelineAccessPolicy;
import org.goldstandard.assurance.api.service.activity.RequestActivityDisplayNameFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathwayGroupTimelineConverterTest {

  @InjectMocks PathwayGroupTimelineConverter converter;

  @Mock RequestActivityDisplayNameFactory displayNameFactory;

  @Mock Clock clock;

  @Mock Request request;

  @Mock WeekdayCalculator weekdayCalculator;

  @Mock PathwayGroupTimelineAccessPolicy accessPolicy;

  @BeforeEach
  void setUp() {
    givenRedactedTimeline();
  }

  @Test
  void shouldPopulateFutureActivityDto() {
    var now = mockTimeNow();
    var pathwayActivity = pathwayActivityBuilder().internal(true).build();
    var timeline =
        PathwayGroupTimeline.builder()
            .request(request)
            .pathwayActivities(List.of(pathwayActivity))
            .requestActivities(Collections.emptyList())
            .build();
    var expectedDisplayName = givenActivityDisplayName(pathwayActivity);
    var expectedDueDate = givenDueDate(now, pathwayActivity.getTotalDays());

    var dtos = converter.toActivityDtos(timeline);

    assertThat(dtos).hasSize(1);
    var dto = IterableUtils.get(dtos, 0);
    assertThat(dto.getName()).isEqualTo(expectedDisplayName);
    assertThat(dto.getStartDate()).isEqualTo(now);
    assertThat(dto.getDueDate()).isEqualTo(expectedDueDate);
    assertThat(dto.getEndDate()).isNull();
    assertThat(dto.isCompleted()).isFalse();
    assertThat(dto.isExternalResponsibility()).isFalse();
    assertThat(dto.isCurrent()).isFalse();
    assertThat(dto.isSkipped()).isFalse();
  }

  @Test
  void shouldPopulateCompleteActivityDto() {
    var now = mockTimeNow();
    var pathwayActivity = buildPathwayActivity();
    var requestActivity =
        RequestActivity.builder()
            .activity(pathwayActivity)
            .startDate(now.plusHours(2))
            .endDate(now.plusDays(2))
            .plannedEndDate(now.plusDays(pathwayActivity.getTotalDays()))
            .skipped(true)
            .build();
    var timeline =
        PathwayGroupTimeline.builder()
            .request(request)
            .pathwayActivities(List.of(pathwayActivity))
            .requestActivities(List.of(requestActivity))
            .build();

    var dtos = converter.toActivityDtos(timeline);

    assertThat(dtos).hasSize(1);
    var dto = IterableUtils.get(dtos, 0);
    assertThat(dto.getName()).isEqualTo(requestActivity.getDisplayName());
    assertThat(dto.getStartDate()).isEqualTo(requestActivity.getStartDate());
    assertThat(dto.getDueDate()).isEqualTo(requestActivity.getPlannedEndDate());
    assertThat(dto.getEndDate()).isEqualTo(requestActivity.getEndDate());
    assertThat(dto.isCompleted()).isTrue();
    assertThat(dto.isExternalResponsibility()).isTrue();
    assertThat(dto.isCurrent()).isFalse();
    assertThat(dto.isSkipped()).isTrue();
  }

  @Test
  void shouldPopulateCurrentActivityDto() {
    var now = mockTimeNow();
    var pathwayActivity = buildPathwayActivity();
    var requestActivity =
        RequestActivity.builder()
            .activity(pathwayActivity)
            .startDate(now.plusHours(2))
            .plannedEndDate(now.plusDays(pathwayActivity.getTotalDays()))
            .displayName("request-activity-display-name")
            .build();
    when(request.getCurrentActivity()).thenReturn(Optional.of(requestActivity));
    var timeline =
        PathwayGroupTimeline.builder()
            .request(request)
            .pathwayActivities(List.of(pathwayActivity))
            .requestActivities(List.of(requestActivity))
            .build();

    var dtos = converter.toActivityDtos(timeline);

    assertThat(dtos).hasSize(1);
    var dto = IterableUtils.get(dtos, 0);
    assertThat(dto.getName()).isEqualTo(requestActivity.getDisplayName());
    assertThat(dto.getStartDate()).isEqualTo(requestActivity.getStartDate());
    assertThat(dto.getDueDate()).isEqualTo(requestActivity.getPlannedEndDate());
    assertThat(dto.getEndDate()).isNull();
    assertThat(dto.isCompleted()).isFalse();
    assertThat(dto.isExternalResponsibility()).isTrue();
    assertThat(dto.isCurrent()).isTrue();
    assertThat(dto.isSkipped()).isFalse();
  }

  private String givenActivityDisplayName(Activity activity) {
    var displayName = "mock-display-name";
    when(displayNameFactory.toDisplayName(request, activity)).thenReturn(displayName);
    return displayName;
  }

  private ZonedDateTime mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private ZonedDateTime givenDueDate(ZonedDateTime startDate, int numberOfDays) {
    var dueDate = startDate.plusDays(numberOfDays);
    when(weekdayCalculator.addWeekdays(startDate, numberOfDays)).thenReturn(dueDate);
    return dueDate;
  }

  private void givenRedactedTimeline() {
    when(accessPolicy.redactIfRequired(anyCollection())).thenAnswer(i -> i.getArguments()[0]);
  }

  private static Activity buildPathwayActivity() {
    return pathwayActivityBuilder().build();
  }

  private static Activity.ActivityBuilder pathwayActivityBuilder() {
    return Activity.builder().id(1).totalDays(10);
  }
}
