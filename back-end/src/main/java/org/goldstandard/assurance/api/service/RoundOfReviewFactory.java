package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.service.activity.PathwayGroupDeadlineCalculator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoundOfReviewFactory {

  private final PathwayGroupDeadlineCalculator pathwayGroupDeadlineCalculator;
  private final Clock clock;
  private final WeekdayCalculator weekdayCalculator;
  private final RoundOfReviewPolicy policy;

  public Collection<RequestRound> toRoundsOfReview(
      Request originalRequest, Request updatedRequest) {
    Collection<RequestRound> rounds = new ArrayList<>();
    closeRoundOfReviewIfRequired(originalRequest, updatedRequest).ifPresent(rounds::add);
    startRoundOfReviewIfRequired(originalRequest, updatedRequest).ifPresent(rounds::add);
    return rounds;
  }

  public void setRoundOfReviewEndDate(Request original, Request updated) {
    var round = updated.findCurrentRound();
    if (round.isEmpty()) {
      return;
    }
    setRoundOfReviewEndDateIfRequired(original, updated, round.get());
  }

  private Optional<RequestRound> closeRoundOfReviewIfRequired(
      Request originalRequest, Request updatedRequest) {
    if (policy.shouldCloseRoundOfReview(originalRequest, updatedRequest)) {
      return closeRoundOfReview(updatedRequest);
    }
    return Optional.empty();
  }

  private Optional<RequestRound> startRoundOfReviewIfRequired(
      Request originalRequest, Request updatedRequest) {
    if (policy.shouldStartRoundOfReview(originalRequest, updatedRequest)) {
      var newRound = startRoundOfReview(updatedRequest);
      updatedRequest.getRequestRounds().add(newRound);
      return Optional.of(newRound);
    }
    return Optional.empty();
  }

  private void setRoundOfReviewEndDateIfRequired(
      Request original, Request updated, RequestRound round) {
    if (Objects.nonNull(round.getEndDate())) {
      return;
    }
    var currentActivity = updated.getCurrentActivity();
    if (currentActivity.isPresent()) {
      var roundEndDate =
          pathwayGroupDeadlineCalculator.calculateCurrentPathwayGroupEndDate(currentActivity.get());
      round.setEndDate(roundEndDate);
      return;
    }
    round.setEndDate(calculateLegacyRoundEndDate(original, updated));
  }

  private ZonedDateTime calculateLegacyRoundEndDate(Request original, Request updated) {
    int daysToAdd = calculateLegacyDaysToAdd(original, updated);
    var now = ZonedDateTime.now(clock);
    return weekdayCalculator.addWeekdays(now, daysToAdd);
  }

  private int calculateLegacyDaysToAdd(Request original, Request updated) {
    if (hasMovedToStatus(original, updated, RequestStatus::isInAssignment)) {
      return 21;
    }
    return 15;
  }

  private Optional<RequestRound> closeRoundOfReview(Request request) {
    var round = request.findCurrentRound();
    round.ifPresent(
        requestRound -> requestRound.setStatus(new RequestRoundStatus(RequestRoundStatus.CLOSED)));
    return round;
  }

  private RequestRound startRoundOfReview(Request request) {
    var rounds = request.getRequestRounds();
    var currentMaxIndex = maxRoundIndex(rounds);
    var newCurrentIndex = currentMaxIndex + 1;
    request.setCurrentRoundIndex(newCurrentIndex);
    return RequestRound.builder()
        .request(request)
        .roundIndex(newCurrentIndex)
        .startDate(ZonedDateTime.now(clock))
        .status(new RequestRoundStatus(RequestRoundStatus.OPEN))
        .build();
  }

  private boolean hasMovedToStatus(
      Request original, Request updated, Predicate<RequestStatus> hasStatus) {
    return !hasStatus.test(original.getCurrentRequestStatus())
        && hasStatus.test(updated.getCurrentRequestStatus());
  }

  private Integer maxRoundIndex(List<RequestRound> rounds) {
    return rounds.stream().map(RequestRound::getRoundIndex).reduce(0, Integer::max);
  }
}
