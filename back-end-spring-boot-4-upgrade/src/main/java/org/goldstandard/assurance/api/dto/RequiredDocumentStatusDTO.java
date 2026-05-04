package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequiredDocumentStatusDTO {

  private final long provided;
  private final long required;
}
