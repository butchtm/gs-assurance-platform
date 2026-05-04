package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;

@Entity
@IdClass(RequestDocumentId.class)
public class RequestDocument {
  @Id
  @Column(insertable = false, updatable = false)
  private Integer requestId;

  @Id
  @Column(insertable = false, updatable = false)
  private UUID documentId;

  @OneToOne
  @JoinColumn(name = "documentId")
  private Document document;

  private Integer requestTypeId;

  private Integer documentTypeId;

  public Integer getRequestId() {
    return requestId;
  }

  public void setRequestId(Integer requestId) {
    this.requestId = requestId;
  }

  public UUID getDocumentId() {
    return documentId;
  }

  public void setDocumentId(UUID documentId) {
    this.documentId = documentId;
  }

  public Document getDocument() {
    return document;
  }

  public void setDocument(Document document) {
    this.document = document;
  }

  public Integer getRequestTypeId() {
    return requestTypeId;
  }

  public void setRequestTypeId(Integer requestTypeId) {
    this.requestTypeId = requestTypeId;
  }

  public Integer getDocumentTypeId() {
    return documentTypeId;
  }

  public void setDocumentTypeId(Integer documentTypeId) {
    this.documentTypeId = documentTypeId;
  }
}
