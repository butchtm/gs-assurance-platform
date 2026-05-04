package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.PRELIMINARY_REVIEW_ID;
import static org.mockito.Mockito.mock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RequestUpdatePopulatorTest {

  private final RequestUpdatePopulator populator = new RequestUpdatePopulator();

  @Test
  void shouldNotDefaultFastTrackedIfRequestIsNotPreliminaryReview() {
    var request = new Request();
    request.setRequestType(new RequestType(RequestType.INCLUSION_REGULAR_VPA));
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .fastTrack(null)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getFastTrack()).isNull();
  }

  @Test
  void shouldNotDefaultFastTrackedIfUpdatedStatusIsNotInAssignment() {
    var request = new Request();
    request.setRequestType(new RequestType(PRELIMINARY_REVIEW_ID));
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .fastTrack(null)
            .requestStatusId(RequestStatus.COMPLETENESS_CHECK_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getFastTrack()).isNull();
  }

  @Test
  void shouldNotDefaultFastTrackFlagIfAlreadySetOnRequest() {
    var request = new Request();
    request.setRequestType(new RequestType(PRELIMINARY_REVIEW_ID));
    request.setFastTracked(false);
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .fastTrack(null)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getFastTrack()).isNull();
  }

  @Test
  void shouldNotDefaultFastTrackFlagIfAlreadySetOnUpdate() {
    var request = new Request();
    request.setRequestType(new RequestType(PRELIMINARY_REVIEW_ID));
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .fastTrack(false)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getFastTrack()).isFalse();
  }

  @Test
  void
      shouldDefaultFastTrackFlagIfRequestIsPreliminaryReviewUpdatedStatusIsInAssignmentAndFlagNotSet() {
    var request = new Request();
    request.setRequestType(new RequestType(PRELIMINARY_REVIEW_ID));
    request.setFastTracked(null);
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .fastTrack(null)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getFastTrack()).isTrue();
  }

  @Test
  void shouldNotDefaultInternalIfAlreadySetOnRequest() {
    var request = new Request();
    request.setRequestType(mock(RequestType.class));
    request.setInternal(false);
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .internal(null)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getInternal()).isNull();
  }

  @Test
  void shouldNotDefaultInternalIfRequestIsFastTracked() {
    var request = new Request();
    request.setRequestType(mock(RequestType.class));
    request.setFastTracked(true);
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .internal(null)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getInternal()).isNull();
  }

  @Test
  void shouldNotDefaultInternalIfFastTrackedIsSetOnUpdate() {
    var request = new Request();
    request.setRequestType(mock(RequestType.class));
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .internal(null)
            .fastTrack(true)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getInternal()).isNull();
  }

  @Test
  void shouldNotDefaultInternalIfUpdatedStatusIsNotInAssignment() {
    var request = new Request();
    request.setRequestType(mock(RequestType.class));
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .internal(null)
            .requestStatusId(RequestStatus.COMPLETENESS_CHECK_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getInternal()).isNull();
  }

  @Test
  void shouldNotDefaultInternalIfAlreadySetOnUpdate() {
    var request = new Request();
    request.setRequestType(mock(RequestType.class));
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .internal(false)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getInternal()).isFalse();
  }

  @Test
  void
      shouldDefaultInternalIfNotSetOnRequestOrUpdateAndNotFastTrackedAndStatusUpdatedToInAssignment() {
    var request = new Request();
    request.setRequestType(mock(RequestType.class));
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .internal(null)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getInternal()).isTrue();
  }

  @ParameterizedTest
  @EnumSource(RequestUpdateDefaultInternalTestCase.class)
  void shouldDefaultInternalToCorrectValueOnRequestUpdateWithInAssignmentStatus(
      RequestUpdateDefaultInternalTestCase testCase) {
    var request = new Request();
    request.setRequestType(new RequestType(RequestType.DESIGN_CHANGE_WITH_VVB));
    request.setFastTracked(testCase.originalFastTrack);
    request.setInternal(testCase.originalInternal);
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .internal(null)
            .fastTrack(null)
            .requestStatusId(RequestStatus.IN_ASSIGNMENT_ID)
            .build();

    var populatedUpdate = populator.populateDefaultsIfRequired(request, update);

    assertThat(populatedUpdate.getInternal()).isEqualTo(testCase.expectedUpdate);
  }

  @Getter
  @AllArgsConstructor
  public enum RequestUpdateDefaultInternalTestCase {
    INTERNAL_NULL_FAST_TRACK_NULL(null, null, true),
    FAST_TRACKED_REQUEST(null, true, null),
    INTERNAL_ALREADY_SET(false, false, null);
    private final Boolean originalInternal;
    private final Boolean originalFastTrack;
    private final Boolean expectedUpdate;
  }
}
