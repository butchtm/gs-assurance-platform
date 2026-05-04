package org.goldstandard.assurance.api.dto.pub.stakeholder.attributes;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.view.stakeholder.attributes.RequestDocumentView;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DocumentDTO {
  private UUID id;
  private String filename;
  private Boolean access;
  private Boolean category;
  private String type;
  private ZonedDateTime uploadedTimestamp;
  private ZonedDateTime modifiedTimestamp;
  private Boolean isLatest;
  private Boolean isMissing;
  private String displayCategoryType;

  public DocumentDTO(RequestDocumentView view) {
    this.id = view.getDocumentId();
    this.filename = view.getFilename();
    this.access = view.getAccess();
    this.category = view.getCategory();
    this.type = view.getType();
    this.uploadedTimestamp = view.getUploadedTimestamp();
    this.modifiedTimestamp = view.getModifiedTimestamp();
    this.isLatest = view.getIsLatest();
    this.isMissing = view.getIsMissing();
    this.displayCategoryType = view.getDisplayCategoryType();
  }
}
