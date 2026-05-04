package org.goldstandard.assurance.api.converter;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.PathwayGroupTimelineActivityDTO;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.PathwayGroupTimeline;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.goldstandard.assurance.api.service.activity.PathwayGroupTimelineAccessPolicy;
import org.goldstandard.assurance.api.service.activity.RequestActivityDisplayNameFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PathwayGroupTimelineConverter {

  private final RequestActivityDisplayNameFactory activityDisplayNameFactory;
  private final Clock clock;
  private final PathwayGroupTimelineAccessPolicy accessPolicy;
  private final WeekdayCalculator weekdayCalculator;

  public Collection<PathwayGroupTimelineActivityDTO> toActivityDtos(PathwayGroupTimeline timeline) {
    var activities = convertToActivityDtos(timeline);
    return accessPolicy.redactIfRequired(activities);
  }

  private Collection<PathwayGroupTimelineActivityDTO> convertToActivityDtos(
      PathwayGroupTimeline timeline) {
    Collection<PathwayGroupTimelineActivityDTO> activities = new ArrayList<>();
    var startDate = now();
    for (Activity pathwayActivity : timeline.getPathwayActivities()) {
      var requestActivity = timeline.findLatestRequestActivityFor(pathwayActivity);
      if (requestActivity.isPresent()) {
        activities.add(
            toPathwayGroupTimelineActivity(timeline, pathwayActivity, requestActivity.get()));
        startDate = toNextActivityStartDate(requestActivity.get());
      } else {
        var endDate = addActivityDays(startDate, pathwayActivity);
        activities.add(
            toPathwayGroupTimelineActivity(timeline, pathwayActivity, startDate, endDate));
        startDate = endDate;
      }
    }
    return Collections.unmodifiableCollection(activities);
  }

  private PathwayGroupTimelineActivityDTO toPathwayGroupTimelineActivity(
      PathwayGroupTimeline timeline, Activity pathwayActivity, RequestActivity requestActivity) {
    return PathwayGroupTimelineActivityDTO.builder()
        .index(pathwayActivity.getIndex())
        .name(requestActivity.getDisplayName())
        .externalResponsibility(!pathwayActivity.isInternal())
        .startDate(requestActivity.getStartDate())
        .dueDate(requestActivity.getPlannedEndDate())
        .endDate(requestActivity.getEndDate())
        .current(timeline.isCurrentActivity(requestActivity))
        .completed(requestActivity.isComplete())
        .skipped(requestActivity.isSkipped())
        .harmonisationOfFeedback(pathwayActivity.isHarmonisationOfFeedback())
        .build();
  }

  private ZonedDateTime addActivityDays(ZonedDateTime original, Activity activity) {
    return weekdayCalculator.addWeekdays(original, activity.getTotalDays());
  }

  private PathwayGroupTimelineActivityDTO toPathwayGroupTimelineActivity(
      PathwayGroupTimeline timeline,
      Activity pathwayActivity,
      ZonedDateTime startDate,
      ZonedDateTime dueDate) {
    return PathwayGroupTimelineActivityDTO.builder()
        .index(pathwayActivity.getIndex())
        .name(activityDisplayNameFactory.toDisplayName(timeline.getRequest(), pathwayActivity))
        .externalResponsibility(!pathwayActivity.isInternal())
        .startDate(startDate)
        .dueDate(dueDate)
        .current(false)
        .harmonisationOfFeedback(pathwayActivity.isHarmonisationOfFeedback())
        .build();
  }

  private ZonedDateTime now() {
    return ZonedDateTime.now(clock);
  }

  private static ZonedDateTime toNextActivityStartDate(RequestActivity requestActivity) {
    if (Objects.nonNull(requestActivity.getEndDate())) {
      return requestActivity.getEndDate();
    }
    return requestActivity.getPlannedEndDate();
  }
}
