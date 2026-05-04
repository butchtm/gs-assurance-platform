package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestActivityTransactionTest {

  @InjectMocks RequestActivityTransaction transaction;

  @Mock RequestActivityRepository activityRepository;
  @Mock RequestRepository requestRepository;

  @Captor ArgumentCaptor<RequestActivity> activityCaptor;
  @Captor ArgumentCaptor<Request> requestCaptor;

  @Test
  void shouldSaveOriginalActivity() {
    RequestActivity originalActivity = new RequestActivity();

    transaction.update(originalActivity);

    verify(activityRepository).save(activityCaptor.capture());
    assertThat(activityCaptor.getValue()).isEqualTo(originalActivity);
  }

  @Test
  void shouldSaveOriginalAndNextActivity() {
    Request request = new Request();
    RequestActivity originalActivity = new RequestActivity();
    givenSaved(originalActivity);
    RequestActivity nextActivity = new RequestActivity();
    givenSaved(nextActivity);

    transaction.update(originalActivity, nextActivity, request);

    verify(activityRepository, times(2)).save(activityCaptor.capture());
    assertThat(activityCaptor.getAllValues()).containsExactly(originalActivity, nextActivity);
  }

  @Test
  void shouldSetSavedNextActivityAsCurrentActivityOnRequestAndSaveRequest() {
    Request request = new Request();
    RequestActivity originalActivity = new RequestActivity();
    givenSaved(originalActivity);
    RequestActivity nextActivity = new RequestActivity();
    RequestActivity savedNextActivity = givenSaved(nextActivity);

    transaction.update(originalActivity, nextActivity, request);

    verify(requestRepository).save(requestCaptor.capture());
    Request savedRequest = requestCaptor.getValue();
    assertThat(savedRequest.getCurrentActivity()).contains(savedNextActivity);
  }

  private RequestActivity givenSaved(RequestActivity activity) {
    RequestActivity savedActivity = new RequestActivity();
    when(activityRepository.save(activity)).thenReturn(savedActivity);
    return savedActivity;
  }
}
