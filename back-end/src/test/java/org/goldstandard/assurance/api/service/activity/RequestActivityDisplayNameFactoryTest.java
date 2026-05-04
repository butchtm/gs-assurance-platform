package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestActivityDisplayNameFactoryTest {

  private final RequestActivityDisplayNameFactory factory = new RequestActivityDisplayNameFactory();

  @Mock Activity activity;

  @Mock Request request;

  @Test
  void shouldDoNothingIfDisplayNameDoesNotContainPlaceholder() {
    String originalDisplayName = "Assigning for Review";
    when(activity.isOnReviewPathway()).thenReturn(true);
    when(activity.getDisplayName()).thenReturn(originalDisplayName);
    when(request.getRequestRounds()).thenReturn(buildRequestRoundsWithLatestIndex(2));

    String displayName = factory.toDisplayName(request, activity);

    assertThat(displayName).isEqualTo(originalDisplayName);
  }

  @Test
  void shouldNotReplacePlusTwoPlaceholderInDisplayNameIfActivityIsNotReview() {
    String originalDisplayName = "Round 2+ Completeness Check";
    when(activity.isOnReviewPathway()).thenReturn(false);
    when(activity.getDisplayName()).thenReturn(originalDisplayName);

    String displayName = factory.toDisplayName(request, activity);

    assertThat(displayName).isEqualTo(originalDisplayName);
  }

  @Test
  void shouldReplaceOnePlaceholderInDisplayNameWithLatestReviewRoundIfActivityIsReview() {
    String originalDisplayName = "Round 1 Review";
    when(activity.isOnReviewPathway()).thenReturn(true);
    when(activity.getDisplayName()).thenReturn(originalDisplayName);
    when(request.getRequestRounds()).thenReturn(buildRequestRoundsWithLatestIndex(3));

    String displayName = factory.toDisplayName(request, activity);

    assertThat(displayName).isEqualTo("Round 3 Review");
  }

  @Test
  void shouldReplacePlusTwoPlaceholderInDisplayNameWithLatestReviewRoundIfActivityIsReview() {
    String originalDisplayName = "Round 2+ Review";
    when(activity.isOnReviewPathway()).thenReturn(true);
    when(activity.getDisplayName()).thenReturn(originalDisplayName);
    when(request.getRequestRounds()).thenReturn(buildRequestRoundsWithLatestIndex(3));

    String displayName = factory.toDisplayName(request, activity);

    assertThat(displayName).isEqualTo("Round 3 Review");
  }

  private static List<RequestRound> buildRequestRoundsWithLatestIndex(int latestIndex) {
    return IntStream.rangeClosed(0, latestIndex)
        .mapToObj(RequestActivityDisplayNameFactoryTest::buildRequestRound)
        .toList();
  }

  private static RequestRound buildRequestRound(int index) {
    return RequestRound.builder().roundIndex(index).build();
  }
}
