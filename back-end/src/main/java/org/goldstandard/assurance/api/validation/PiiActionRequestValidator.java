package org.goldstandard.assurance.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.goldstandard.assurance.api.dto.PiiActionRequestDTO;

public class PiiActionRequestValidator
    implements ConstraintValidator<ValidPiiActionRequest, PiiActionRequestDTO> {

  @Override
  public boolean isValid(PiiActionRequestDTO request, ConstraintValidatorContext context) {
    if (request == null) {
      return false;
    }
    if (projectIdNotPresent(request)) {
      return false;
    }
    if (neitherCommentIdOrDocumentIdPresent(request) && !requestIdPresent(request)) {
      return true;
    }
    if (documentIdAndCommentIdPresent(request)) {
      return false;
    }
    if (requestIdPresent(request) && neitherCommentIdOrDocumentIdPresent(request)) {
      return false;
    }
    return documentIdOrCommentIdAndRequestIdAndProjectIdPresent(request);
  }

  private static boolean projectIdNotPresent(PiiActionRequestDTO request) {
    return Objects.isNull(request.getProjectId());
  }

  private static boolean neitherCommentIdOrDocumentIdPresent(PiiActionRequestDTO request) {
    return Objects.isNull(request.getDocumentId()) && Objects.isNull(request.getCommentId());
  }

  private static boolean requestIdPresent(PiiActionRequestDTO request) {
    return Objects.nonNull(request.getRequestId());
  }

  private static boolean documentIdAndCommentIdPresent(PiiActionRequestDTO request) {
    return Objects.nonNull(request.getCommentId()) && Objects.nonNull(request.getDocumentId());
  }

  private static boolean documentIdOrCommentIdAndRequestIdAndProjectIdPresent(
      PiiActionRequestDTO request) {
    return (Objects.nonNull(request.getDocumentId()) || Objects.nonNull(request.getCommentId()))
        && (Objects.nonNull(request.getRequestId()) && Objects.nonNull(request.getProjectId()));
  }
}
