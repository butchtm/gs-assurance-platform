package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestUpdateValidatorTest {

  public static final int PROJECT_ID = 1;

  @InjectMocks RequestUpdateValidator validator;

  @Mock RequestAccessPolicy accessPolicy;

  @Test
  void shouldPermitUpdatesToAllFieldsForNonBatchChildRequest() {
    var request = givenNonBatchChildRequest();
    givenRequestBelongsToProject(request);
    var update = RequestCreateUpdateDTOMother.build();

    ThrowingCallable call = () -> validator.validate(request, update, PROJECT_ID);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotPermitUpdateToStartDateForBatchChildRequest() {
    var request = givenBatchChildRequest(91);
    var update =
        RequestCreateUpdateDTOMother.updateBuilder().startDate(ZonedDateTime.now()).build();
    givenRequestBelongsToProject(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage("Updates: [start date], are not permitted for batch child request 91");
  }

  @Test
  void shouldNotPermitUpdateToManuallyAddedForBatchChildRequest() {
    var request = givenBatchChildRequest(94);
    var update = RequestCreateUpdateDTO.builder().manuallyAdded(true).build();
    givenRequestBelongsToProject(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage("Updates: [manually added], are not permitted for batch child request 94");
  }

  @Test
  void shouldNotPermitUpdateToRequestStatusIdForBatchChildRequest() {
    var request = givenBatchChildRequest(95);
    var update = RequestCreateUpdateDTO.builder().requestStatusId(6).build();
    givenRequestBelongsToProject(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage("Updates: [request status], are not permitted for batch child request 95");
  }

  @Test
  void shouldNotPermitUpdateToStartStakeholderConsultationForBatchChildRequest() {
    var request = givenBatchChildRequest(96);
    var update = RequestCreateUpdateDTO.builder().startStakeholderConsultation(true).build();
    givenRequestBelongsToProject(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage(
            "Updates: [stakeholder consultation], are not permitted for batch child request 96");
  }

  @Test
  void shouldReturnAllErrorsInUpdateForBatchChildRequest() {
    var request = givenBatchChildRequest(95);
    var update =
        RequestCreateUpdateDTOMother.builder(request.getId())
            .startStakeholderConsultation(true)
            .build();
    givenRequestBelongsToProject(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error)
        .isInstanceOf(BatchRequestException.class)
        .hasMessage(
            "Updates: [start date, manually added, request status, "
                + "stakeholder consultation, fast track], are not permitted for batch child request 95");
  }

  @Test
  void shouldReturnIfFastTrackTrueForPreliminaryReviewRequestType() {
    var request = givenNonBatchChildRequest();
    when(request.isPreliminaryReview()).thenReturn(true);
    var update = RequestCreateUpdateDTOMother.updateBuilder().fastTrack(true).build();
    givenRequestBelongsToProject(request);

    ThrowingCallable call = () -> validator.validate(request, update, PROJECT_ID);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldReturnExceptionIfFastTrackTrueForNonPreliminaryReviewRequestType() {
    var request = givenNonBatchChildRequest();
    when(request.isPreliminaryReview()).thenReturn(false);
    var update = RequestCreateUpdateDTOMother.updateBuilder().fastTrack(true).build();
    givenRequestBelongsToProject(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error).isInstanceOf(BadRequestException.class).hasMessage(error.getMessage());
  }

  @Test
  void shouldReturnIfInternalNotNullForNonFastTrackRequest() {
    var request = givenNonBatchChildRequest();
    var update =
        RequestCreateUpdateDTOMother.updateBuilder().fastTrack(false).internal(true).build();
    givenRequestBelongsToProject(request);

    ThrowingCallable call = () -> validator.validate(request, update, PROJECT_ID);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldReturnExceptionIfInternalNotNullForFastTrackRequest() {
    var request = givenNonBatchChildRequest();
    var update =
        RequestCreateUpdateDTOMother.updateBuilder().fastTrack(true).internal(true).build();
    givenRequestBelongsToProject(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error).isInstanceOf(BadRequestException.class).hasMessage(error.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCannotUpdateRequest() {
    var request = givenStandardRequest();
    var update = mock(RequestCreateUpdateDTO.class);
    givenRequestBelongsToProject(request);
    var expectedError = new PermissionException("not-permitted");
    doThrow(expectedError).when(accessPolicy).validateCanUpdateRequest(request);

    Throwable error = catchThrowable(() -> validator.validate(request, update, PROJECT_ID));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldReturnIfUpdateDoesNotContainMethodologies() {
    var request = givenStandardRequest();
    givenRequestBelongsToProject(request);
    var requestMethodology = mock(RequestMethodologyDTO.class);
    var update =
        RequestCreateUpdateDTOMother.updateBuilder()
            .methodologies(List.of(requestMethodology))
            .build();

    ThrowingCallable call = () -> validator.validate(request, update, PROJECT_ID);

    assertThatCode(call).doesNotThrowAnyException();
  }

  private static void givenRequestBelongsToProject(Request request) {
    when(request.getProject().getId()).thenReturn(RequestUpdateValidatorTest.PROJECT_ID);
  }

  private static Request givenNonBatchChildRequest() {
    var request = givenStandardRequest();
    when(request.isBatchChild()).thenReturn(false);
    return request;
  }

  private static Request givenBatchChildRequest(int id) {
    var request = givenStandardRequest();
    when(request.getId()).thenReturn(id);
    when(request.isBatchChild()).thenReturn(true);
    return request;
  }

  private static Request givenStandardRequest() {
    var request = mock(Request.class);
    var project = mock(Project.class);
    when(request.getProject()).thenReturn(project);
    return request;
  }
}
