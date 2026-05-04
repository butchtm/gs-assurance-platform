package org.goldstandard.assurance.api.service.form;

import java.util.Objects;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.repository.RequestFormRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Builder
@Slf4j
public class RequestFormValidator {

  private final RequestFinder requestFinder;
  private final RequestFormRepository formRepository;
  private final RequestFormDataValidator dataValidator;

  public void validateRequestExists(Integer requestId) {
    requestFinder.validateRequestExists(requestId);
  }

  public void validateCanBeCreated(Integer requestId, Integer typeId) {
    if (formRepository.existsByRequestIdAndDefinition_Type_Id(requestId, typeId)) {
      throw new BadRequestException(
          "A request form already exists of form type %d for request %d", typeId, requestId);
    }
  }

  public void validateCanBeUpdated(RequestForm form) {
    if (form.isSubmitted()) {
      throw new BadRequestException(
          "Request form %d is submitted so cannot be updated", form.getId());
    }
  }

  public void validateCanBeSubmitted(RequestForm form) {
    if (form.isSubmitted()) {
      throw new BadRequestException("Request form %s is already submitted", form.getId());
    }
  }

  public void validateFormBelongsToRequest(Integer requestId, RequestForm form) {
    var formRequestId = form.getRequestId();
    if (!Objects.equals(formRequestId, requestId)) {
      throw new BadRequestException(
          "Form %d belongs to request %d not request %d", form.getId(), formRequestId, requestId);
    }
  }

  public void validateDataAgainstSchema(RequestForm form) {
    dataValidator.validateDataAgainstSchema(form);
  }
}
