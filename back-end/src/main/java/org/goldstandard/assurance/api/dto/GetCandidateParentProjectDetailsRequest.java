package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class GetCandidateParentProjectDetailsRequest {

  private final Integer accountId;
  private final Integer classificationId;
  private final Integer candidateProjectId;
  private final Integer masterProjectId;
}
