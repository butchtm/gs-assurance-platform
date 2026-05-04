package org.goldstandard.assurance.api.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequiredDocumentStatus {

  private final long provided;
  private final long required;
}
