package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.service.activity.PathwayGroupDeadlineCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoundOfReviewFactoryTest {

  @InjectMocks RoundOfReviewFactory factory;

  @Mock Clock clock;

  @Mock PathwayGroupDeadlineCalculator pathwayGroupDeadlineCalculator;

  @Mock RoundOfReviewPolicy policy;

  @Test
  void closeRoundOfReviewIfRequired() {
    var originalRequest = buildRequest();
    var updatedRequest =
        requestWithCurrentRound(buildRequestRound(RequestRoundStatus.OPEN)).build();
    when(policy.shouldCloseRoundOfReview(originalRequest, updatedRequest)).thenReturn(true);

    var rounds = factory.toRoundsOfReview(originalRequest, updatedRequest);

    assertThat(rounds)
        .map(RequestRound::getStatus)
        .map(ReferenceDataType::getId)
        .containsExactly(RequestRoundStatus.CLOSED);
  }

  @Test
  void startRoundOfReviewWhenRequired() {
    var originalRequest = buildRequest();
    var closedRound = buildRequestRound(RequestRoundStatus.CLOSED);
    var updatedRequest = requestWithCurrentRound(closedRound).build();
    when(policy.shouldStartRoundOfReview(originalRequest, updatedRequest)).thenReturn(true);
    var now = mockTimeNow();

    var rounds = factory.toRoundsOfReview(originalRequest, updatedRequest);

    assertThat(rounds).hasSize(1);
    var newRound = rounds.stream().findFirst().orElseThrow();
    assertThat(newRound.getRequest()).isEqualTo(updatedRequest);
    assertThat(newRound.getRoundIndex()).isEqualTo(closedRound.getRoundIndex() + 1);
    assertThat(newRound.getStartDate()).isEqualTo(now);
    assertThat(newRound.getEndDate()).isNull();
    assertThat(newRound.getStatus().getId()).isEqualTo(RequestRoundStatus.OPEN);
  }

  @Test
  void shouldNotSetRoundOfReviewEndDateIfAlreadySet() {
    var originalRequest = buildRequest();
    var endDate = ZonedDateTime.now();
    var round = buildRequestRound(RequestRoundStatus.OPEN);
    round.setEndDate(endDate);
    var updatedRequest = requestWithCurrentRound(round).build();

    factory.setRoundOfReviewEndDate(originalRequest, updatedRequest);

    assertThat(updatedRequest.getCurrentRoundEndDate()).isEqualTo(endDate);
  }

  @Test
  void shouldSetRoundOfReviewEndDateForNewRound() {
    var originalRequest =
        requestBuilder().currentRequestStatus(RequestStatus.inPreparation()).build();
    var requestActivity = new RequestActivity();
    var updatedRequest =
        requestWithCurrentRound(buildRequestRound(RequestRoundStatus.OPEN))
            .currentActivity(requestActivity)
            .currentRequestStatus(RequestStatus.inAssignment())
            .build();
    var endDate = ZonedDateTime.now().plusDays(15);
    when(pathwayGroupDeadlineCalculator.calculateCurrentPathwayGroupEndDate(requestActivity))
        .thenReturn(endDate);

    factory.setRoundOfReviewEndDate(originalRequest, updatedRequest);

    assertThat(updatedRequest.getCurrentRoundEndDate()).isEqualTo(endDate);
  }

  private Request.RequestBuilder requestWithCurrentRound(RequestRound round) {
    return requestBuilder()
        .requestRounds(new ArrayList<>(List.of(round)))
        .currentRoundIndex(round.getRoundIndex());
  }

  private Request buildRequest() {
    return requestBuilder().build();
  }

  private Request.RequestBuilder requestBuilder() {
    return Request.builder().id(101);
  }

  private RequestRound buildRequestRound(int roundStatusId) {
    return RequestRound.builder()
        .id(1001)
        .roundIndex(1)
        .status(new RequestRoundStatus(roundStatusId))
        .build();
  }

  private ZonedDateTime mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }
}
