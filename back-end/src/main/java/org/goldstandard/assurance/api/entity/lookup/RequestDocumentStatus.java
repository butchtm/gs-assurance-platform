package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class RequestDocumentStatus extends ReferenceDataType {

  public RequestDocumentStatus() {}

  public RequestDocumentStatus(Integer id) {
    this.id = id;
  }
}
