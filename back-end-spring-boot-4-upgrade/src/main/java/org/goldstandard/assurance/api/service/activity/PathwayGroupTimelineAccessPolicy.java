package org.goldstandard.assurance.api.service.activity;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.PathwayGroupTimelineActivityDTO;
import org.goldstandard.assurance.api.entity.activity.PathwayGroupTimeline;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PathwayGroupTimelineAccessPolicy {

  private final PermissionsService permissionsService;

  public void validateAccess(PathwayGroupTimeline timeline) {
    if (timeline.isForCompletenessCheckPathway() && permissionsService.isProjectDeveloperOrVVB()) {
      throw new PermissionException(
          "project developers and vvbs are not permitted to view completeness check pathways");
    }
  }

  public Collection<PathwayGroupTimelineActivityDTO> redactIfRequired(
      Collection<PathwayGroupTimelineActivityDTO> timeline) {
    if (permissionsService.isProjectDeveloperOrVVB()) {
      return timeline.stream().map(PathwayGroupTimelineAccessPolicy::redact).toList();
    }
    return timeline;
  }

  private static PathwayGroupTimelineActivityDTO redact(PathwayGroupTimelineActivityDTO activity) {
    var builder = activity.toBuilder().startDate(null);
    if (!activity.isHarmonisationOfFeedback()) {
      builder.dueDate(null).endDate(null);
    }
    return builder.build();
  }
}
