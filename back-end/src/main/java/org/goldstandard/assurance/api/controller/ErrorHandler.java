package org.goldstandard.assurance.api.controller;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.exception.AccountTypeIdNotSupportedException;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.exception.CannotCompleteActivityException;
import org.goldstandard.assurance.api.exception.CompetenciesException;
import org.goldstandard.assurance.api.exception.ConflictException;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.DocumentNotAssociatedWithRequestException;
import org.goldstandard.assurance.api.exception.FileNotFoundInStoreException;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.goldstandard.assurance.api.exception.JsonConversionException;
import org.goldstandard.assurance.api.exception.LoginFailedException;
import org.goldstandard.assurance.api.exception.NoAssignedGroupsException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.exception.RequestDoesNotBelongToProjectException;
import org.goldstandard.assurance.api.exception.ReviewerNotAssignedToRequestException;
import org.goldstandard.assurance.api.exception.UserAccountNotAssignedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ProblemDetail> handle(NotFoundException e) {
    log.warn(e.getMessage(), e);
    return toResponse(NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(DataException.class)
  public ResponseEntity<ProblemDetail> handle(DataException e) {
    log.warn(e.getMessage());
    return toResponse(NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> handle(NoResourceFoundException e) {
    if (e.getResourcePath().endsWith("download-urls")) {
      // something is spamming a non-existent download-urls endpoint in production
      // demoting this log to warning so that we stop getting spammed with error alerts
      log.warn(e.getMessage(), e);
    } else {
      log.error(e.getMessage(), e);
    }
    return toResponse(NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(BatchRequestException.class)
  public ResponseEntity<ProblemDetail> handle(BatchRequestException e) {
    log.error(e.getMessage(), e);
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ProblemDetail> handle(BadRequestException e) {
    log.warn(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(RequestDoesNotBelongToProjectException.class)
  public ResponseEntity<ProblemDetail> handle(RequestDoesNotBelongToProjectException e) {
    log.error(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(AccountTypeIdNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handle(AccountTypeIdNotSupportedException e) {
    log.error(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(DocumentNotAssociatedWithRequestException.class)
  public ResponseEntity<ProblemDetail> handle(DocumentNotAssociatedWithRequestException e) {
    log.error(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(CompetenciesException.class)
  public ResponseEntity<ProblemDetail> handle(CompetenciesException e) {
    log.error(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handle(MethodArgumentNotValidException e) {
    log.warn(e.getMessage());
    return toResponse(BAD_REQUEST, toFriendlyErrorMessage(e));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ProblemDetail> handle(EntityNotFoundException e) {
    log.error(e.getMessage());
    return toResponse(BAD_REQUEST, "An associated reference type was not found");
  }

  @ExceptionHandler(LoginFailedException.class)
  public ResponseEntity<ProblemDetail> handle(LoginFailedException e) {
    log.warn(concatCauseMessageToMessageIfPresent(e));
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(JwtValidationException.class)
  public ResponseEntity<ProblemDetail> handle(JwtValidationException e) {
    log.warn(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handle(ConstraintViolationException e) {
    log.warn(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(CannotCompleteActivityException.class)
  public ResponseEntity<ProblemDetail> handle(CannotCompleteActivityException e) {
    log.warn(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(ReviewerNotAssignedToRequestException.class)
  public ResponseEntity<ProblemDetail> handle(ReviewerNotAssignedToRequestException e) {
    log.warn(e.getMessage());
    return toResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(PermissionException.class)
  public ResponseEntity<ProblemDetail> handle(PermissionException e) {
    log.error(e.getMessage());
    return toResponse(UNAUTHORIZED, e.getMessage());
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ProblemDetail> handle(AuthorizationDeniedException e) {
    log.warn(e.getMessage());
    return toResponse(FORBIDDEN, e.getMessage());
  }

  @ExceptionHandler(UserAccountNotAssignedException.class)
  public ResponseEntity<ProblemDetail> handle(UserAccountNotAssignedException e) {
    log.warn(e.getMessage(), e);
    return toResponse(CONFLICT, e.getMessage());
  }

  @ExceptionHandler(NoAssignedGroupsException.class)
  public ResponseEntity<ProblemDetail> handle(NoAssignedGroupsException e) {
    log.warn(e.getMessage());
    return toResponse(CONFLICT, e.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ProblemDetail> handle(ConflictException e) {
    log.warn(e.getMessage());
    return toResponse(CONFLICT, e.getMessage());
  }

  @ExceptionHandler(FileNotFoundInStoreException.class)
  public ResponseEntity<ProblemDetail> handle(FileNotFoundInStoreException e) {
    log.warn(e.getMessage());
    return toResponse(INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(FileStoreException.class)
  public ResponseEntity<ProblemDetail> handle(FileStoreException e) {
    log.error(e.getMessage());
    return toResponse(INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(JsonConversionException.class)
  public ResponseEntity<ProblemDetail> handle(JsonConversionException e) {
    log.error(e.getMessage(), e);
    return toResponse(INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<ProblemDetail> handle(MultipartException e) {
    log.warn(e.getMessage());
    return toResponse(INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(AsyncRequestNotUsableException.class)
  public ResponseEntity<ProblemDetail> handle(AsyncRequestNotUsableException t) {
    log.warn(t.getMessage(), t);
    return toResponse(INTERNAL_SERVER_ERROR, "an unexpected error occurred");
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ProblemDetail> handle(Throwable t) {
    log.error(t.getMessage(), t);
    return toResponse(INTERNAL_SERVER_ERROR, "an unexpected error occurred");
  }

  private static ResponseEntity<ProblemDetail> toResponse(HttpStatus status, String errorDetail) {
    return ResponseEntity.of(forStatusAndDetail(status, errorDetail))
        .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  private static String concatCauseMessageToMessageIfPresent(Throwable t) {
    var message = t.getMessage();
    var causeMessage = Optional.ofNullable(t.getCause()).map(Throwable::getMessage).orElse("");
    return String.join(" - ", message, causeMessage);
  }

  private static String toFriendlyErrorMessage(MethodArgumentNotValidException e) {
    var message =
        e.getBindingResult().getAllErrors().stream()
            .map(error -> error.unwrap(ConstraintViolation.class))
            .map(
                violation ->
                    String.format("%s->%s", violation.getPropertyPath(), violation.getMessage()))
            .collect(Collectors.joining(","));
    if (StringUtils.isEmpty(message)) {
      return "Validation error.";
    }
    return message;
  }
}
