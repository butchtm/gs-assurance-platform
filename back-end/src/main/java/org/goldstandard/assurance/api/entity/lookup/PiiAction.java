package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class PiiAction extends ReferenceDataType {
  public static final Integer NEW_STATUS_ID = 0;
  public static final Integer UNDER_INVESTIGATION_ID = 1;
  public static final Integer ACCEPTED_ID = 2;
  public static final Integer REMOVED_ID = 5;
  public static final Integer PII_NOT_ACCEPTABLE_ID = 6;
  public static final Integer DOCUMENT_REMOVED_ID = 7;
  public static final Integer NO_PII_FOUND = 8;
  public static final Integer OBSOLETE_ID = 9;

  public PiiAction(Integer id) {
    this.id = id;
  }

  public boolean isNew() {
    return Objects.equals(this.id, NEW_STATUS_ID);
  }

  public boolean isUnderInvestigation() {
    return Objects.equals(this.id, UNDER_INVESTIGATION_ID);
  }

  public boolean publicVisibilityAcceptable() {
    return isAccepted() || noPiiFound() || isObsolete();
  }

  public boolean isAccepted() {
    return Objects.equals(this.id, ACCEPTED_ID);
  }

  public boolean noPiiFound() {
    return Objects.equals(this.id, NO_PII_FOUND);
  }

  public boolean isObsolete() {
    return Objects.equals(this.id, OBSOLETE_ID);
  }

  public boolean isPiiNotAcceptable() {
    return Objects.equals(this.id, PII_NOT_ACCEPTABLE_ID);
  }

  public boolean isDocumentRemoved() {
    return Objects.equals(this.id, DOCUMENT_REMOVED_ID);
  }

  public boolean isActioned() {
    return !this.isNew() && !this.isUnderInvestigation();
  }

  public boolean hasPiiOfConcern() {
    return this.isDocumentRemoved() || this.isPiiNotAcceptable();
  }
}
