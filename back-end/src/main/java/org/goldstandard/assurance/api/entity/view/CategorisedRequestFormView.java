package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestForm;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Entity(name = "v_categorised_request_form")
@Getter
public class CategorisedRequestFormView {
  @Id private final Integer formId;
  private final Integer requestId;
  private final Integer displayCategoryId;

  @OneToOne
  @JoinColumn(name = "formId")
  private final RequestForm form;
}
