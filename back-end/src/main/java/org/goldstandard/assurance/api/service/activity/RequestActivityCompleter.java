package org.goldstandard.assurance.api.service.activity;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.exception.CannotCompleteActivityException;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestActivityCompleter {

  private final RequestActivityFinder requestActivityFinder;
  private final Clock clock;
  private final RequestFinder requestFinder;
  private final ActivityFinder activityFinder;
  private final RequestActivityCreator creator;
  private final RequestActivityTransaction transaction;

  public void complete(Integer id) {
    var originalActivity = requestActivityFinder.forceFindById(id);
    var request = requestFinder.getRequestById(originalActivity.getRequestId());
    validateCanComplete(originalActivity, id);
    var completedActivity = originalActivity.withEndDate(ZonedDateTime.now(clock));
    createNextActivity(completedActivity, request)
        .ifPresentOrElse(
            nextActivity -> transaction.update(completedActivity, nextActivity, request),
            () -> transaction.update(completedActivity));
  }

  private Optional<RequestActivity> createNextActivity(
      RequestActivity completedActivity, Request request) {
    return activityFinder
        .findNextActivity(completedActivity.getActivity())
        .map(nextActivity -> creator.buildRequestActivity(nextActivity, request));
  }

  private void validateCanComplete(RequestActivity activity, Integer id) {
    if (!activity.isAssignmentOfCompletenessCheck()) {
      throw new CannotCompleteActivityException(id);
    }
  }
}
