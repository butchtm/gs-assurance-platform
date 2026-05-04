package org.goldstandard.assurance.api.service.activity;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.PathwayGroupTimeline;
import org.goldstandard.assurance.api.entity.activity.PathwayGroupTimelinePredicateFactory;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PathwayGroupTimelineFinder {

  private final RequestFinder requestFinder;
  private final ActivityFinder activityFinder;
  private final RequestActivityFinder requestActivityFinder;
  private final PathwayGroupTimelinePredicateFactory predicateFactory;
  private final PathwayGroupTimelineAccessPolicy accessPolicy;

  public PathwayGroupTimeline getCurrentPathwayGroupTimeline(Integer requestId) {
    var request = requestFinder.getRequestOrBatchParentRequestById(requestId);
    var timeline =
        request
            .getCurrentActivity()
            .filter(currentActivity -> !currentActivity.isComplete())
            .map(currentActivity -> toPathwayGroupTimeline(request, currentActivity))
            .orElse(new PathwayGroupTimeline(request));
    accessPolicy.validateAccess(timeline);
    return timeline;
  }

  private PathwayGroupTimeline toPathwayGroupTimeline(
      Request request, RequestActivity currentActivity) {
    return PathwayGroupTimeline.builder()
        .request(request)
        .pathwayActivities(activityFinder.findActivities(currentActivity.getPathwayGroup()))
        .requestActivities(getRequestActivities(request))
        .build();
  }

  private Collection<RequestActivity> getRequestActivities(Request request) {
    return requestActivityFinder.findByRequestId(request.getId()).stream()
        .filter(predicateFactory.build(request))
        .toList();
  }
}
