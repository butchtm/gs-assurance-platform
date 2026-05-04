package org.goldstandard.assurance.api.service.form;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.entity.RequestFormStatus;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.RequestFormRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestFormValidatorTest {

  @InjectMocks RequestFormValidator validator;

  @Mock RequestFinder requestFinder;
  @Mock RequestFormRepository formRepository;
  @Mock RequestFormDataValidator dataValidator;

  @Test
  void shouldValidateRequestExists() {
    var requestId = 999;
    var expectedError = new NotFoundException("boom!");
    doThrow(expectedError).when(requestFinder).validateRequestExists(requestId);

    var error = catchThrowable(() -> validator.validateRequestExists(requestId));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void cannotBeCreatedIfFormOfSameTypeAlreadyExistsForRequest() {
    var requestId = 998;
    var formTypeId = 0;
    when(formRepository.existsByRequestIdAndDefinition_Type_Id(requestId, formTypeId))
        .thenReturn(true);

    var error = catchThrowable(() -> validator.validateCanBeCreated(requestId, formTypeId));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("A request form already exists of form type 0 for request 998");
  }

  @Test
  void canBeCreatedIfFormOfSameTypeDoesNotAlreadyExistForRequest() {
    var requestId = 997;
    var formTypeId = 1;
    when(formRepository.existsByRequestIdAndDefinition_Type_Id(requestId, formTypeId))
        .thenReturn(false);

    ThrowingCallable call = () -> validator.validateCanBeCreated(requestId, formTypeId);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void cannotBeUpdatedIfAlreadySubmitted() {
    var form = RequestForm.builder().id(2).status(RequestFormStatus.submitted()).build();

    var error = catchThrowable(() -> validator.validateCanBeUpdated(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Request form 2 is submitted so cannot be updated");
  }

  @Test
  void canBeUpdatedIfNotAlreadySubmitted() {
    var form = RequestForm.builder().status(RequestFormStatus.draft()).build();

    ThrowingCallable call = () -> validator.validateCanBeUpdated(form);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void cannotBeSubmittedIfAlreadySubmitted() {
    var form = RequestForm.builder().id(3).status(RequestFormStatus.submitted()).build();

    var error = catchThrowable(() -> validator.validateCanBeSubmitted(form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Request form 3 is already submitted");
  }

  @Test
  void canBeSubmittedIfNotAlreadySubmitted() {
    var form = RequestForm.builder().status(RequestFormStatus.draft()).build();

    ThrowingCallable call = () -> validator.validateCanBeSubmitted(form);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionIfFormDoesNotBelongToRequest() {
    var requestId = 996;
    var form = RequestForm.builder().id(4).requestId(321).build();

    var error = catchThrowable(() -> validator.validateFormBelongsToRequest(requestId, form));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Form 4 belongs to request 321 not request 996");
  }

  @Test
  void shouldNotThrowExceptionIfFormDoesBelongToRequest() {
    var requestId = 995;
    var form = RequestForm.builder().id(5).requestId(requestId).build();

    ThrowingCallable call = () -> validator.validateFormBelongsToRequest(requestId, form);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldValidateFormDataAgainstSchema() {
    var requestId = 994;
    var form = RequestForm.builder().id(6).requestId(requestId).build();

    validator.validateDataAgainstSchema(form);

    var captor = ArgumentCaptor.forClass(RequestForm.class);
    verify(dataValidator).validateDataAgainstSchema(captor.capture());
    assertThat(captor.getValue()).isEqualTo(form);
  }
}
