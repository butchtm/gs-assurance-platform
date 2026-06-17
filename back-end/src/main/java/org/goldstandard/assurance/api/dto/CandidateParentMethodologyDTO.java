package org.goldstandard.assurance.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class CandidateParentMethodologyDTO {

  private final Integer id;
  private final String code;
  private final String description;
}
