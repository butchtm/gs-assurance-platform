package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TextToAnalyseDTO {
  private final String field;
  private final String text;
}
