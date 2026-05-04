package org.goldstandard.assurance.api.service.activity;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RequestActivityFinder {

  private final RequestFinder requestFinder;
  private final RequestActivityRepository repository;

  public RequestActivity forceFindById(Integer id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Request activity with id %s not found", id));
  }

  public ZonedDateTime findPathwayGroupStartDateForRequest(
      RequestActivity requestActivity, ZonedDateTime now) {
    return findPathwayGroupStartDateForRequest(
        requestActivity.getRequestId(), requestActivity.getActivity(), now);
  }

  public ZonedDateTime findPathwayGroupStartDateForRequest(
      Integer requestId, Activity activity, ZonedDateTime now) {
    if (activity.getIndex() == 0) {
      return now;
    }
    return repository
        .findFirstByRequestIdAndActivity_PathwayGroupAndActivity_IndexOrderByStartDateDesc(
            requestId, activity.getPathwayGroup(), 0)
        .map(RequestActivity::getStartDate)
        .orElse(now);
  }

  public Optional<RequestActivity> getLatestRequestActivity(Request request) {
    return Optional.ofNullable(requestFinder.toBatchParentIfChild(request))
        .flatMap(Request::getCurrentActivity)
        .filter(activity -> !activity.isComplete());
  }

  public Page<RequestActivity> findByRequestId(Integer requestId, Pageable pageable) {
    return repository.findAllByRequestId(requestId, pageable);
  }

  public Collection<RequestActivity> findByRequestId(Integer requestId) {
    return repository.findAllByRequestId(requestId);
  }
}
