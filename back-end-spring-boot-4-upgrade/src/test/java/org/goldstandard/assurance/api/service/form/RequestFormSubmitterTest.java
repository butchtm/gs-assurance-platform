package org.goldstandard.assurance.api.service.form;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.dto.RequestFormSubmitDTO;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.entity.RequestFormStatus;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.node.ArrayNode;

@ExtendWith(MockitoExtension.class)
class RequestFormSubmitterTest {

  @InjectMocks RequestFormSubmitter submitter;

  @Mock AuthenticatedUserSupplier userSupplier;
  @Mock RequestFormValidator validator;
  @Mock Clock clock;

  @Test
  void shouldThrowExceptionIfValidatorThrowsException() {
    var originalForm = new RequestForm();
    var dto = RequestFormSubmitDTO.builder().build();
    var expectedError = givenValidatorErrorThrown(originalForm);

    var error = catchThrowable(() -> submitter.submit(originalForm, dto));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldPopulateSubmittedStatus() {
    mockTimeNow();
    var originalForm = new RequestForm();
    var dto = RequestFormSubmitDTO.builder().build();

    var submittedForm = submitter.submit(originalForm, dto);

    assertThat(submittedForm.getStatus())
        .usingRecursiveComparison()
        .isEqualTo(RequestFormStatus.submitted());
    assertThat(submittedForm)
        .usingRecursiveComparison()
        .ignoringFields("status", "submittedAt")
        .isEqualTo(originalForm);
  }

  @Test
  void shouldPopulateSubmittedAtNow() {
    var now = mockTimeNow();
    var originalForm = new RequestForm();
    var dto = RequestFormSubmitDTO.builder().build();

    var submittedForm = submitter.submit(originalForm, dto);

    assertThat(submittedForm.getSubmittedAt()).isEqualTo(now);
    assertThat(submittedForm)
        .usingRecursiveComparison()
        .ignoringFields("status", "submittedAt")
        .isEqualTo(originalForm);
  }

  @Test
  void shouldPopulateSubmittedByUser() {
    mockTimeNow();
    var originalForm = new RequestForm();
    var dto = RequestFormSubmitDTO.builder().build();
    var expectedUser = givenAuthenticatedUser();

    var submittedForm = submitter.submit(originalForm, dto);

    assertThat(submittedForm.getSubmittedBy()).isEqualTo(expectedUser);
    assertThat(submittedForm)
        .usingRecursiveComparison()
        .ignoringFields("status", "submittedAt", "submittedBy")
        .isEqualTo(originalForm);
  }

  @Test
  void shouldNotPopulateDataOrUpdatedAtIfDataNotProvided() {
    mockTimeNow();
    var originalForm = new RequestForm();
    var dto = RequestFormSubmitDTO.builder().build();

    var submittedForm = submitter.submit(originalForm, dto);

    assertThat(submittedForm.getData()).isNull();
    assertThat(submittedForm.getUpdatedAt()).isNull();
    assertThat(submittedForm)
        .usingRecursiveComparison()
        .ignoringFields("status", "submittedAt")
        .isEqualTo(originalForm);
  }

  @Test
  void shouldNotPopulateDataOrUpdatedAtIfDataEmpty() {
    mockTimeNow();
    var originalForm = new RequestForm();
    var data = mock(ArrayNode.class);
    when(data.isEmpty()).thenReturn(true);
    var dto = RequestFormSubmitDTO.builder().data(data).build();

    var submittedForm = submitter.submit(originalForm, dto);

    assertThat(submittedForm.getData()).isNull();
    assertThat(submittedForm.getUpdatedAt()).isNull();
    assertThat(submittedForm)
        .usingRecursiveComparison()
        .ignoringFields("status", "submittedAt")
        .isEqualTo(originalForm);
  }

  @Test
  void shouldPopulateDataIfProvided() {
    mockTimeNow();
    var originalForm = new RequestForm();
    var data = mock(ArrayNode.class);
    when(data.isEmpty()).thenReturn(false);
    var dto = RequestFormSubmitDTO.builder().data(data).build();

    var submittedForm = submitter.submit(originalForm, dto);

    assertThat(submittedForm.getData()).isEqualTo(data);
    assertThat(submittedForm)
        .usingRecursiveComparison()
        .ignoringFields("status", "submittedAt", "data", "updatedAt")
        .isEqualTo(originalForm);
  }

  @Test
  void shouldPopulateUpdatedAtNowIfDataProvided() {
    var now = mockTimeNow();
    var originalForm = new RequestForm();
    var data = mock(ArrayNode.class);
    when(data.isEmpty()).thenReturn(false);
    var dto = RequestFormSubmitDTO.builder().data(data).build();

    var submittedForm = submitter.submit(originalForm, dto);

    assertThat(submittedForm.getUpdatedAt()).isEqualTo(now);
    assertThat(submittedForm)
        .usingRecursiveComparison()
        .ignoringFields("status", "submittedAt", "data", "updatedAt")
        .isEqualTo(originalForm);
  }

  private Exception givenValidatorErrorThrown(RequestForm form) {
    var error = new BadRequestException("boom!");
    doThrow(error).when(validator).validateCanBeSubmitted(form);
    return error;
  }

  private ZonedDateTime mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private User givenAuthenticatedUser() {
    var user = new User();
    when(userSupplier.forceGetAuthenticatedUser()).thenReturn(user);
    return user;
  }
}
