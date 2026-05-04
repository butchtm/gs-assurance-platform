package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;
import software.amazon.awssdk.services.comprehend.model.PiiEntityType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PiiEntityMother {

  public static PiiEntity build(float entityScore) {
    return PiiEntity.builder()
        .type(PiiEntityType.NAME)
        .score(entityScore)
        .beginOffset(0)
        .endOffset(9)
        .build();
  }
}
