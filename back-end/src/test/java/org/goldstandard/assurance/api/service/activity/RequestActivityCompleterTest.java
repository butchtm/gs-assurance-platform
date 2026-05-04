package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.exception.CannotCompleteActivityException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestActivityCompleterTest {

  @InjectMocks RequestActivityCompleter completer;

  @Mock RequestActivityFinder requestActivityFinder;
  @Mock Clock clock;
  @Mock RequestFinder requestFinder;
  @Mock ActivityFinder activityFinder;
  @Mock RequestActivityCreator creator;
  @Mock RequestActivityTransaction transaction;

  @Captor ArgumentCaptor<RequestActivity> requestActivityCaptor;

  @Test
  void shouldThrowErrorIfActivityNotFound() {
    int id = 1;
    Throwable expectedError = new NotFoundException("request-activity-not-found");
    doThrow(expectedError).when(requestActivityFinder).forceFindById(id);

    Throwable error = catchThrowable(() -> completer.complete(id));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldThrowErrorIfActivityIsNotAssignmentOfCompletenessCheck() {
    int id = 1;
    var originalActivity = mock(RequestActivity.class);
    when(originalActivity.isAssignmentOfCompletenessCheck()).thenReturn(false);
    when(requestActivityFinder.forceFindById(id)).thenReturn(originalActivity);

    Throwable error = catchThrowable(() -> completer.complete(id));

    assertThat(error)
        .isInstanceOf(CannotCompleteActivityException.class)
        .hasMessageContaining(Integer.toString(id));
  }

  @Test
  void shouldSetEndDateOnOriginalActivityAndSaveIfNextActivityNotFound() {
    int id = 2;
    var now = givenTimeNow();
    var originalActivity = buildOriginalActivity();
    when(requestActivityFinder.forceFindById(id)).thenReturn(originalActivity);

    completer.complete(id);

    verify(transaction).update(requestActivityCaptor.capture());
    var savedRequestActivity = requestActivityCaptor.getValue();
    assertThat(savedRequestActivity)
        .usingRecursiveComparison()
        .ignoringFields("endDate")
        .isEqualTo(originalActivity);
    assertThat(savedRequestActivity.getEndDate()).isEqualTo(now);
  }

  @Test
  void shouldSetEndDateOnOriginalActivityAndSaveIfNextActivityFound() {
    int id = 2;
    var now = givenTimeNow();
    var originalActivity = buildOriginalActivity();
    when(requestActivityFinder.forceFindById(id)).thenReturn(originalActivity);
    var request = new Request();
    when(requestFinder.getRequestById(originalActivity.getRequestId())).thenReturn(request);
    var nextActivity = givenNextActivity(originalActivity, request);

    completer.complete(id);

    verify(transaction).update(requestActivityCaptor.capture(), eq(nextActivity), eq(request));
    var savedOriginalActivity = requestActivityCaptor.getValue();
    assertThat(savedOriginalActivity)
        .usingRecursiveComparison()
        .ignoringFields("endDate")
        .isEqualTo(originalActivity);
    assertThat(savedOriginalActivity.getEndDate()).isEqualTo(now);
  }

  @Test
  void shouldSaveNextActivityIfFound() {
    int id = 3;
    givenTimeNow();
    var originalActivity = buildOriginalActivity();
    when(requestActivityFinder.forceFindById(id)).thenReturn(originalActivity);
    var request = new Request();
    when(requestFinder.getRequestById(originalActivity.getRequestId())).thenReturn(request);
    var nextActivity = givenNextActivity(originalActivity, request);

    completer.complete(id);

    verify(transaction)
        .update(any(RequestActivity.class), requestActivityCaptor.capture(), eq(request));
    var savedNextActivity = requestActivityCaptor.getValue();
    assertThat(savedNextActivity).isEqualTo(nextActivity);
  }

  private ZonedDateTime givenTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private RequestActivity givenNextActivity(RequestActivity originalActivity, Request request) {
    Activity nextActivity = new Activity();
    when(activityFinder.findNextActivity(originalActivity.getActivity()))
        .thenReturn(Optional.of(nextActivity));
    RequestActivity nextRequestActivity = new RequestActivity();
    when(creator.buildRequestActivity(nextActivity, request)).thenReturn(nextRequestActivity);
    return nextRequestActivity;
  }

  private static RequestActivity buildOriginalActivity() {
    var activity = Activity.builder().name("Assignment of CC").build();
    return RequestActivity.builder().activity(activity).requestId(999).build();
  }
}
