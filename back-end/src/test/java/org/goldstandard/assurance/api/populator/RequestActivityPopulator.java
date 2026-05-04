package org.goldstandard.assurance.api.populator;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RequestActivityPopulator {

  public static final int NUMBER_OF_REQUEST_ACTIVITIES = 3;
  public static final ZonedDateTime FIXED_DATE = ZonedDateTime.now();
  public static List<RequestActivity> TEST_REQUEST_ACTIVITIES;

  @Autowired private RequestActivityRepository requestActivityRepository;

  public boolean isPopulated() {
    return requestActivityRepository.count() > 0;
  }

  public void populate() {
    var requestActivities = new ArrayList<RequestActivity>();
    for (int i = 0; i < NUMBER_OF_REQUEST_ACTIVITIES; i++) {
      var activity = Activity.builder().id(i).name(String.format("activity name %d", i)).build();
      var account = new Account();
      account.setId(1 + i % 2);
      var requestActivity =
          RequestActivity.builder()
              .requestId((i + 1) << 1)
              .activity(activity)
              .startDate(FIXED_DATE.minusDays(1))
              .responsibleAccount(account)
              .build();
      requestActivities.add(requestActivity);
    }

    TEST_REQUEST_ACTIVITIES =
        IterableUtils.toList(requestActivityRepository.saveAll(requestActivities));
    TEST_REQUEST_ACTIVITIES.forEach(RequestActivityPopulator::logRequestActivity);
  }

  private static void logRequestActivity(RequestActivity requestActivity) {
    var requestId = requestActivity.getRequestId();
    var activity = requestActivity.getActivity();
    var responsibleAccount = requestActivity.getResponsibleAccount();

    log.info(
        "populated requestactivity {} for request {} with activity {} with responsible account {}",
        requestActivity.getId(),
        requestId,
        activity.getId(),
        responsibleAccount.getId());
  }
}
