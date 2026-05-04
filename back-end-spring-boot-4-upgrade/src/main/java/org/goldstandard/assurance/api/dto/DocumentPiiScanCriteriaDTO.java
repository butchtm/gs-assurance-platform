package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentPiiScanCriteriaDTO {
  private String contentType;
  private Long documentSizeBytes;
}
