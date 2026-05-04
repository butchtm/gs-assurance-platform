package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.FIRST_FROM_PATHWAY;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.FIRST_FROM_PATHWAY_GROUP;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.NEXT_ACTIVITY;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.NONE;
import static org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity.SKIP_NEXT_ACTIVITY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequiredActivityCalculatorTest {

  private final RequiredActivityCalculator calculator = new RequiredActivityCalculator();

  @Mock Request request;

  @Mock RequestStatus status;

  @BeforeEach
  void setUp() {
    when(request.getCurrentRequestStatus()).thenReturn(status);
  }

  @Nested
  class FirstFromPathwayTest {

    @Test
    void shouldReturnFirstFromPathwayIfInCompletenessCheckWithoutCurrentActivity() {
      when(status.isInCompletenessCheck()).thenReturn(true);
      when(request.hasCurrentActivity()).thenReturn(false);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(FIRST_FROM_PATHWAY);
    }

    @Test
    void shouldReturnFirstFromPathwayIfInAssignment() {
      when(status.isInAssignment()).thenReturn(true);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(FIRST_FROM_PATHWAY);
    }
  }

  @Nested
  class FirstFromPathwayGroupTest {

    @Test
    void shouldReturnFirstFromPathwayGroupIfHasCurrentActivityAndInCompletenessCheck() {
      when(request.hasCurrentActivity()).thenReturn(true);
      when(status.isInCompletenessCheck()).thenReturn(true);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(FIRST_FROM_PATHWAY_GROUP);
    }

    @Test
    void
        shouldReturnFirstFromPathwayGroupIfHasCurrentActivityAndInProgressWithMoreThanOneRequestRound() {
      when(request.hasCurrentActivity()).thenReturn(true);
      when(status.isInProgress()).thenReturn(true);
      givenNumberOfRequestRounds(2);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(FIRST_FROM_PATHWAY_GROUP);
    }

    @Test
    void
        shouldReturnFirstFromPathwayGroupIfHasCurrentActivityAndInRoundCompletionWithMoreThanOneRequestRoundAndFastTracked() {
      when(request.hasCurrentActivity()).thenReturn(true);
      when(status.isInRoundCompletion()).thenReturn(true);
      givenNumberOfRequestRounds(2);
      when(request.isFastTracked()).thenReturn(true);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(FIRST_FROM_PATHWAY_GROUP);
    }
  }

  @Nested
  class NextActivityTest {

    @Test
    void shouldReturnNextActivityIfHasCurrentActivityAndInProgressAndOneRequestRound() {
      when(request.hasCurrentActivity()).thenReturn(true);
      when(status.isInProgress()).thenReturn(true);
      givenNumberOfRequestRounds(1);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(NEXT_ACTIVITY);
    }

    @Test
    void shouldReturnNextActivityIfHasCurrentActivityAndInRoundCompletionWithOneRequestRound() {
      when(request.hasCurrentActivity()).thenReturn(true);
      when(status.isInRoundCompletion()).thenReturn(true);
      givenNumberOfRequestRounds(1);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(NEXT_ACTIVITY);
    }

    @Test
    void
        shouldReturnNextActivityIfHasCurrentActivityAndInRoundCompletionAndMoreThanOneRequestRoundAndNotFastTracked() {
      when(request.hasCurrentActivity()).thenReturn(true);
      when(status.isInRoundCompletion()).thenReturn(true);
      givenNumberOfRequestRounds(2);
      when(request.isFastTracked()).thenReturn(false);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(NEXT_ACTIVITY);
    }
  }

  @Nested
  class SkipNextActivityTest {

    @Test
    void shouldSkipNextActivityIfHasCurrentActivityAndWasInAssignmentAndNowInRoundCompletion() {
      when(request.hasCurrentActivity()).thenReturn(true);
      var updatedStatus = mock(RequestStatus.class);
      when(request.getCurrentRequestStatus()).thenReturn(updatedStatus);
      when(updatedStatus.isInRoundCompletion()).thenReturn(true);
      when(status.isInAssignment()).thenReturn(true);

      var requiredActivity = calculator.calculateRequiredActivity(request, status);

      assertThat(requiredActivity).isEqualTo(SKIP_NEXT_ACTIVITY);
    }
  }

  @Test
  void shouldReturnNoneForAnyOtherCombination() {
    when(request.hasCurrentActivity()).thenReturn(false);

    var requiredActivity = calculator.calculateRequiredActivity(request, status);

    assertThat(requiredActivity).isEqualTo(NONE);
  }

  private void givenNumberOfRequestRounds(int numberOfRounds) {
    List<RequestRound> rounds =
        IntStream.range(0, numberOfRounds).mapToObj(i -> mock(RequestRound.class)).toList();
    when(request.getRequestRounds()).thenReturn(rounds);
  }
}
