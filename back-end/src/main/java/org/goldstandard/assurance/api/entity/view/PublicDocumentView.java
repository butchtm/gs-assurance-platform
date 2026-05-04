package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "v_public_documents")
public class PublicDocumentView {
  @Id private UUID documentId;
  private String gsid;
  private String projectName;
  private Integer requestId;
  private String requestName;
  private Integer requestIndex;
  private Integer requestTypeId;
  private String requestTypeCode;
  private String requestTypeDescription;
  private Integer documentTypeId;
  private String documentTypeCode;
  private String documentTypeDescription;
  private String documentFileName;
  private Integer documentFileSize;
  private String documentContentType;
  private Integer documentDisplayCategoryId;
  private String documentDisplayCategoryDescription;
  private ZonedDateTime documentUploaded;
  private ZonedDateTime documentModified;
  private Boolean documentIsMissing;
}
