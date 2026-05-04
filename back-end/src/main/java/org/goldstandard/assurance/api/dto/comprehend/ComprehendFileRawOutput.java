package org.goldstandard.assurance.api.dto.comprehend;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonDeserialize(builder = ComprehendFileRawOutput.ComprehendFileRawOutputBuilder.class)
public class ComprehendFileRawOutput {
  @JsonProperty("Entities")
  private final List<ComprehendPiiEntity> entities;

  @JsonProperty("File")
  private final String file;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ComprehendFileRawOutputBuilder {}
}
