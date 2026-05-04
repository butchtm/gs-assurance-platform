package org.goldstandard.assurance.api.entity.activity;

import java.util.function.Predicate;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@Component
public class PathwayGroupTimelinePredicateFactory {

  public Predicate<RequestActivity> build(Request request) {
    return new PathwayGroupTimelinePredicate(request);
  }
}
