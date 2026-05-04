package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Request;

@Entity(name = "v_categorised_request_document")
@Getter
@Setter
public class CategorisedRequestDocumentView {
  @EmbeddedId private CategorisedRequestDocumentViewId categorisedRequestDocumentViewId;

  @ManyToOne
  @JoinColumn(name = "requestId", nullable = false, insertable = false, updatable = false)
  private Request request;

  @OneToOne
  @JoinColumn(name = "documentId", nullable = false, insertable = false, updatable = false)
  private Document document;

  private Integer documentDisplayCategoryId;

  public Integer getDocumentTypeId() {
    return document.getDocumentTypeId();
  }

  private String piiScanStatusCode;
  private String piiActionCode;
}
