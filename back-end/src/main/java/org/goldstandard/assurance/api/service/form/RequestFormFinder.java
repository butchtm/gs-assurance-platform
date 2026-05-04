package org.goldstandard.assurance.api.service.form;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestFormFiltersDTO;
import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestFormView;
import org.goldstandard.assurance.api.repository.CategorisedRequestFormViewRepository;
import org.goldstandard.assurance.api.repository.RequestFormRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestFormFinder {

  private final RequestFormValidator validator;
  private final RequestFormRepository formRepository;
  private final CategorisedRequestFormViewRepository categorisedFormRepository;
  private final RequestFormSpecFactory specFactory;

  public Page<RequestForm> getRequestForms(
      Integer requestId, RequestFormFiltersDTO filters, Pageable pageable) {
    var spec = specFactory.toSpec(requestId, filters);
    var categorisedForms = categorisedFormRepository.findAll(spec, pageable);
    return categorisedForms.map(CategorisedRequestFormView::getForm);
  }

  public Collection<RequestForm> getRequestForms(Integer requestId, RequestFormFiltersDTO filters) {
    var spec = specFactory.toSpec(requestId, filters);
    var categorisedForms = categorisedFormRepository.findAll(spec);
    return categorisedForms.stream().map(CategorisedRequestFormView::getForm).toList();
  }

  public RequestForm getForm(Integer requestId, Integer formId) {
    validator.validateRequestExists(requestId);
    var form = formRepository.forceFindById(formId);
    validator.validateFormBelongsToRequest(requestId, form);
    return form;
  }
}
