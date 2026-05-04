package org.goldstandard.assurance.api.service.document.pii.analysis;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DocumentPiiScanCriteria {
  private final String contentType;
  private final Long fileSizeMb;
}
