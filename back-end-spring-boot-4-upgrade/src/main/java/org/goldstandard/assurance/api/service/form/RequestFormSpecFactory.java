package org.goldstandard.assurance.api.service.form;

import org.goldstandard.assurance.api.dto.RequestFormFiltersDTO;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestFormView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RequestFormSpecFactory {

  public Specification<CategorisedRequestFormView> toSpec(
      Integer requestId, RequestFormFiltersDTO dto) {
    var spec = belongsToRequest(requestId);
    var displayCategoryId = dto.getDisplayCategoryId();
    if (displayCategoryId.isPresent()) {
      spec = spec.and(hasDisplayCategoryId(displayCategoryId.get()));
    }
    return spec;
  }

  private static Specification<CategorisedRequestFormView> belongsToRequest(Integer requestId) {
    return (view, cq, cb) -> cb.equal(view.get("requestId"), requestId);
  }

  private static Specification<CategorisedRequestFormView> hasDisplayCategoryId(
      Integer displayCategoryId) {
    return (view, cq, cb) -> cb.equal(view.get("displayCategoryId"), displayCategoryId);
  }
}
