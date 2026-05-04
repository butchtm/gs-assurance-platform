package org.goldstandard.assurance.api.service.activity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestActivityTransaction {

  private final RequestActivityRepository activityRepository;
  private final RequestRepository requestRepository;

  public void update(RequestActivity originalActivity) {
    activityRepository.save(originalActivity);
  }

  @Transactional
  public void update(
      RequestActivity originalActivity, RequestActivity nextActivity, Request request) {
    activityRepository.save(originalActivity);
    RequestActivity savedNextActivity = activityRepository.save(nextActivity);
    request.setCurrentActivity(savedNextActivity);
    requestRepository.save(request);
  }
}
