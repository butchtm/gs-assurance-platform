package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.controller.ProblemDetailAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.exception.AccountTypeIdNotSupportedException;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.exception.CannotCompleteActivityException;
import org.goldstandard.assurance.api.exception.CompetenciesException;
import org.goldstandard.assurance.api.exception.ConflictException;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.DocumentNotAssociatedWithRequestException;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import org.goldstandard.assurance.api.exception.LoginFailedException;
import org.goldstandard.assurance.api.exception.NoAssignedGroupsException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.exception.RequestDoesNotBelongToProjectException;
import org.goldstandard.assurance.api.exception.ReviewerNotAssignedToRequestException;
import org.goldstandard.assurance.api.exception.UserAccountNotAssignedException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class ErrorHandlerTest {

  private final ErrorHandler handler = new ErrorHandler();

  @Test
  void shouldHandleNotFoundException() {
    var exception = new NotFoundException("not-found");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.NOT_FOUND)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleDataException() {
    var exception = new DataException("data-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.NOT_FOUND)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldNoResourceFoundException() {
    var exception =
        new NoResourceFoundException(
            HttpMethod.POST, "http://localhost:8080/some-endpoint", "/some-endpoint");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.NOT_FOUND)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleBatchRequestException() {
    var exception = new BatchRequestException("batch-request");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleCompetenciesException() {
    var exception = new CompetenciesException("competencies-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleBadRequestException() {
    var exception = new BadRequestException("bad-request");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleRequestDoesNotBelongToProjectException() {
    var exception = new RequestDoesNotBelongToProjectException(1, 2);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleAccountTypeIdNotSupportedException() {
    var exception = new AccountTypeIdNotSupportedException(1);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleDocumentNotAssociatedWithRequestException() {
    var exception = new DocumentNotAssociatedWithRequestException(UUID.randomUUID(), 1);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleEndpointValidationExceptionWithoutBindingResultErrors() {
    var exception = mock(MethodArgumentNotValidException.class);
    var bindingResult = mock(BindingResult.class);
    when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());
    when(exception.getBindingResult()).thenReturn(bindingResult);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail("Validation error.");
  }

  @Test
  void shouldHandleEndpointValidationExceptionWithBindingResultErrors() {
    var error1 = buildObjectError("path-1", "error message 1");
    var error2 = buildObjectError("path-2", "error message 2");
    var bindingResult = mock(BindingResult.class);
    when(bindingResult.getAllErrors()).thenReturn(List.of(error1, error2));
    var exception = mock(MethodArgumentNotValidException.class);
    when(exception.getBindingResult()).thenReturn(bindingResult);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail("path-1->error message 1,path-2->error message 2");
  }

  @Test
  void shouldHandleEntityNotFoundException() {
    var exception = new EntityNotFoundException("entity-not-found-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail("An associated reference type was not found");
  }

  @Test
  void shouldHandleLoginFailedException() {
    var exception = new LoginFailedException(new Exception("login-failed"));

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleJwtValidationException() {
    var exception =
        new JwtValidationException("jwt-validation-exception", List.of(mock(OAuth2Error.class)));

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleConstraintViolationException() {
    var exception = new ConstraintViolationException("constraint violation", null);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleCannotCompleteActivityException() {
    var exception = new CannotCompleteActivityException(0);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleReviewerNotAssignedToRequestException() {
    var exception = new ReviewerNotAssignedToRequestException(1);

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandlePermissionException() {
    var exception = new PermissionException("permission-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.UNAUTHORIZED)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleAuthorizationDeniedException() {
    var exception =
        new AuthorizationDeniedException("auth-denied-exception", mock(AuthorizationResult.class));

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.FORBIDDEN)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleUserAccountNotAssignedException() {
    var exception = new UserAccountNotAssignedException();

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.CONFLICT)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleNoRolesAssignedException() {
    var exception = new NoAssignedGroupsException("no-assigned-groups-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.CONFLICT)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleConflictException() {
    var exception = new ConflictException("conflict-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.CONFLICT)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleFileStoreException() {
    var exception = new FileStoreException("file-store-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleJsonConversionException() {
    var exception = new JsonConversionException(mock(Throwable.class));

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .hasDetail("Failed to convert between object and json");
  }

  @Test
  void shouldHandleMultipartException() {
    var exception = new MultipartException("multi-part-exception");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .hasDetail(exception.getMessage());
  }

  @Test
  void shouldHandleAsyncRequestNotUsableException() {
    var exception = mock(AsyncRequestNotUsableException.class);
    when(exception.getMessage()).thenReturn("any-error");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .hasDetail("an unexpected error occurred");
  }

  @Test
  void shouldHandleThrowable() {
    var exception = mock(Throwable.class);
    when(exception.getMessage()).thenReturn("any-error");

    var response = handler.handle(exception);

    assertThat(response.getBody())
        .isNotNull()
        .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .hasDetail("an unexpected error occurred");
  }

  private static ObjectError buildObjectError(String path, String message) {
    var propertyPath = mock(PathImpl.class);
    when(propertyPath.toString()).thenReturn(path);
    var violation = mock(ConstraintViolation.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(violation.getMessage()).thenReturn(message);
    var error = mock(ObjectError.class);
    when(error.unwrap(ConstraintViolation.class)).thenReturn(violation);
    return error;
  }
}
