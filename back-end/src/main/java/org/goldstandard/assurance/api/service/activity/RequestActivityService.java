package org.goldstandard.assurance.api.service.activity;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.activity.PathwayGroupTimeline;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestActivityService {

  private final RequestActivityCompleter completer;
  private final RequestActivityFinder activityFinder;
  private final PathwayGroupTimelineFinder timelineFinder;

  public void complete(Integer requestActivityId) {
    completer.complete(requestActivityId);
  }

  public PathwayGroupTimeline getCurrentPathwayGroupTimeline(Integer requestId) {
    return timelineFinder.getCurrentPathwayGroupTimeline(requestId);
  }

  public Page<RequestActivity> findByRequestId(Integer requestId, Pageable pageable) {
    return activityFinder.findByRequestId(requestId, pageable);
  }
}
