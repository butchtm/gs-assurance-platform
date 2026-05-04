package org.goldstandard.assurance.api.service.form;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.RequestFormConverter;
import org.goldstandard.assurance.api.dto.RequestFormCreateDTO;
import org.goldstandard.assurance.api.dto.RequestFormDTO;
import org.goldstandard.assurance.api.dto.RequestFormFiltersDTO;
import org.goldstandard.assurance.api.dto.RequestFormSubmitDTO;
import org.goldstandard.assurance.api.dto.RequestFormUpdateDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestFormService {

  private final RequestFinder requestFinder;
  private final RequestFormFactory factory;
  private final RequestFormTransaction transaction;
  private final RequestFormFinder formFinder;
  private final RequestFormUpdater updater;
  private final RequestFormSubmitter submitter;
  private final RequestFormConverter converter;

  public Integer createRequestForm(Integer requestId, RequestFormCreateDTO dto) {
    var request = requestFinder.getRequestOrBatchParentRequestById(requestId);
    var form = factory.create(request, dto);
    return transaction.create(form);
  }

  public void updateRequestForm(Integer requestId, Integer formId, RequestFormUpdateDTO dto) {
    var existing = findExistingForm(requestId, formId);
    var updated = updater.update(existing, dto);
    transaction.update(updated);
  }

  public void submitRequestForm(Integer requestId, Integer formId, RequestFormSubmitDTO dto) {
    var existing = findExistingForm(requestId, formId);
    var submitted = submitter.submit(existing, dto);
    transaction.submit(submitted);
  }

  public RequestFormDTO getRequestForm(Integer requestId, Integer formId) {
    var form = findExistingForm(requestId, formId);
    return converter.toDTO(form);
  }

  public PagedResource<RequestFormDTO> getRequestForms(
      Integer requestId, RequestFormFiltersDTO filters, Pageable pageable) {
    var lookupRequestId = requestFinder.getParentRequestIdOrRequestId(requestId);
    return converter.toPagedResource(
        formFinder.getRequestForms(lookupRequestId, filters, pageable));
  }

  public Collection<RequestFormDTO> getRequestForms(
      Integer requestId, RequestFormFiltersDTO filters) {
    var lookupRequestId = requestFinder.getParentRequestIdOrRequestId(requestId);
    return converter.toDTOs(formFinder.getRequestForms(lookupRequestId, filters));
  }

  private RequestForm findExistingForm(Integer requestId, Integer formId) {
    var lookupRequestId = requestFinder.getParentRequestIdOrRequestId(requestId);
    return formFinder.getForm(lookupRequestId, formId);
  }
}
