package org.goldstandard.assurance.api.entity.view.stakeholder.attributes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity(name = "v_public_stakeholder_documents")
@NoArgsConstructor
@AllArgsConstructor
public class RequestDocumentView {
  @Id private UUID documentId;
  private Integer requestId;
  private String filename;
  private Boolean access;
  private Boolean category;
  private String type;
  private ZonedDateTime uploadedTimestamp;
  private ZonedDateTime modifiedTimestamp;
  private Boolean isLatest;
  private Boolean isMissing;
  private String displayCategoryType;
}
