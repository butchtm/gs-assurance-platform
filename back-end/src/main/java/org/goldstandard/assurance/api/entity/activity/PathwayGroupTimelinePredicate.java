package org.goldstandard.assurance.api.entity.activity;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;

@RequiredArgsConstructor
public class PathwayGroupTimelinePredicate implements Predicate<RequestActivity> {

  private final Request request;

  @Override
  public boolean test(RequestActivity other) {
    return request.getCurrentActivity().map(current -> test(current, other)).orElse(false);
  }

  private static boolean test(RequestActivity current, RequestActivity other) {
    if (current.hasActivityId(other.getActivityId())) {
      return true;
    }
    return other.isBefore(current);
  }
}
