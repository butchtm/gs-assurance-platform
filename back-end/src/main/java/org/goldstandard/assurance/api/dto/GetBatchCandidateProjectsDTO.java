package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class GetBatchCandidateProjectsDTO {

  private final Integer requestTypeId;
  private final Integer batchTypeId;
  private final Integer projectId;
}
