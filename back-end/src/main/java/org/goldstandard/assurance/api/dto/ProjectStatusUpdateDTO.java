package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Getter
public class ProjectStatusUpdateDTO {
  @JsonProperty("id")
  private final Integer projectStatusId;

  private final Integer requestId;
}
