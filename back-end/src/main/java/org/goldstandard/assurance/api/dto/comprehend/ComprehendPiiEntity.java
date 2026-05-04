package org.goldstandard.assurance.api.dto.comprehend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ComprehendPiiEntity {
  @JsonProperty("BeginOffset")
  private final int beginOffset;

  @JsonProperty("EndOffset")
  private final int endOffset;

  @JsonProperty("Type")
  private final String type;

  @JsonProperty("Score")
  private final float score;
}
