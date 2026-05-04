package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class DocumentType extends ExtendedReferenceDataType {

  private static final int OTHER_ID = 999999;
  private static final int REVIEW_FEEDBACK_FORM_ID = 1017;

  public DocumentType(Integer id) {
    this.id = id;
  }

  public DocumentType(Integer id, String description) {
    this.id = id;
    this.description = description;
  }

  public boolean isOther() {
    return hasId(OTHER_ID);
  }

  public boolean isReviewFeedbackForm() {
    return hasId(REVIEW_FEEDBACK_FORM_ID);
  }

  private boolean hasId(Integer idToCheck) {
    return Objects.equals(id, idToCheck);
  }
}
