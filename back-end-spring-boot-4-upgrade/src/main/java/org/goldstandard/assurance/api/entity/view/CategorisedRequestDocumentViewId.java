package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
public class CategorisedRequestDocumentViewId implements Serializable {
  private Integer requestId;
  private UUID documentId;
}
