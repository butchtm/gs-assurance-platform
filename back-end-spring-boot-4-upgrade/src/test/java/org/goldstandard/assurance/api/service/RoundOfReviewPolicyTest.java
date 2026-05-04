package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.util.RequestStatusChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RoundOfReviewPolicyTest {

  private final RoundOfReviewPolicy policy = new RoundOfReviewPolicy(new RequestStatusChecker());

  @Test
  void shouldNotStartRoundOfReviewWhenMovingBackToInProgressFromInRoundCompletion() {
    var originalRequest = buildRequest(RequestStatus.inRoundOfCompletion());
    var updatedRequest = buildRequest(RequestStatus.inProgress());

    var start = policy.shouldStartRoundOfReview(originalRequest, updatedRequest);

    assertThat(start).isFalse();
  }

  @ParameterizedTest
  @MethodSource("startRoundFromStatusAndToStatus")
  void shouldStartRoundOfReviewWhenRequestEntersReviewStatusWithNoOpenRound(
      Integer fromStatusId, Integer toStatusId) {
    var originalRequest = buildRequest(new RequestStatus(fromStatusId));
    var updatedRequest = buildRequest(new RequestStatus(toStatusId));
    var closedRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.CLOSED));
    updatedRequest.setRequestRounds(new ArrayList<>(List.of(closedRound)));

    var start = policy.shouldStartRoundOfReview(originalRequest, updatedRequest);

    assertThat(start).isTrue();
  }

  @ParameterizedTest
  @MethodSource("startRoundFromStatusAndToStatus")
  void shouldNotStartRoundOfReviewWhenRoundIsAlreadyOpen(Integer fromStatusId, Integer toStatusId) {
    var originalRequest = buildRequest(new RequestStatus(fromStatusId));
    var updatedRequest = buildRequest(new RequestStatus(toStatusId));
    var closedRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.OPEN));
    updatedRequest.setRequestRounds(new ArrayList<>(List.of(closedRound)));

    var start = policy.shouldStartRoundOfReview(originalRequest, updatedRequest);

    assertThat(start).isFalse();
  }

  @Test
  void shouldCloseRoundOfReviewWhenRequestLeavesInRoundCompletion() {
    var originalRequest = buildRequest(RequestStatus.inRoundOfCompletion());
    var updatedRequest = buildRequest(RequestStatus.completedApproved());
    var openRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.OPEN));
    updatedRequest.setRequestRounds(new ArrayList<>(List.of(openRound)));
    updatedRequest.setCurrentRoundIndex(openRound.getRoundIndex());

    var close = policy.shouldCloseRoundOfReview(originalRequest, updatedRequest);

    assertThat(close).isTrue();
  }

  @Test
  void shouldNotCloseRoundOfReviewWhenRoundIsAlreadyClosed() {
    var originalRequest = buildRequest(RequestStatus.inRoundOfCompletion());
    var updatedRequest = buildRequest(RequestStatus.completedApproved());
    var openRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.CLOSED));
    updatedRequest.setRequestRounds(new ArrayList<>(List.of(openRound)));
    updatedRequest.setCurrentRoundIndex(openRound.getRoundIndex());

    var close = policy.shouldCloseRoundOfReview(originalRequest, updatedRequest);

    assertThat(close).isFalse();
  }

  @Test
  void shouldNotCloseRoundOfReviewWhenRequestMovingBackToInProgressFromInRoundCompletion() {
    var originalRequest = buildRequest(RequestStatus.inRoundOfCompletion());
    var updatedRequest = buildRequest(RequestStatus.inProgress());
    var openRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.OPEN));
    updatedRequest.setRequestRounds(new ArrayList<>(List.of(openRound)));
    updatedRequest.setCurrentRoundIndex(openRound.getRoundIndex());

    var close = policy.shouldCloseRoundOfReview(originalRequest, updatedRequest);

    assertThat(close).isFalse();
  }

  @Test
  void shouldCloseRoundOfReviewWhenRequestFroInRoundCompletion() {
    var originalRequest = buildRequest(RequestStatus.inRoundOfCompletion());
    var updatedRequest = buildRequest(RequestStatus.inPreparation());
    var openRound = buildRequestRound(new RequestRoundStatus(RequestRoundStatus.OPEN));
    updatedRequest.setRequestRounds(new ArrayList<>(List.of(openRound)));
    updatedRequest.setCurrentRoundIndex(openRound.getRoundIndex());

    var close = policy.shouldCloseRoundOfReview(originalRequest, updatedRequest);

    assertThat(close).isTrue();
  }

  private Request buildRequest(RequestStatus requestStatus) {
    return Request.builder()
        .id(101)
        .currentRequestStatus(requestStatus)
        .requestRounds(new ArrayList<>())
        .build();
  }

  private RequestRound buildRequestRound(RequestRoundStatus requestRoundStatus) {
    return RequestRound.builder().id(1001).roundIndex(1).status(requestRoundStatus).build();
  }

  private static Stream<Arguments> startRoundFromStatusAndToStatus() {
    return Stream.of(
        Arguments.of(RequestStatus.COMPLETENESS_CHECK_ID, RequestStatus.IN_ASSIGNMENT_ID),
        Arguments.of(RequestStatus.IN_PREPARATION_ID, RequestStatus.IN_PROGRESS_ID));
  }
}
