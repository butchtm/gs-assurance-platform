package org.goldstandard.assurance.api.fixtures;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileOutput;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

public class ComprehendOutputMother {

  public static List<ComprehendFileOutput> buildList(int pageNumber, String entity) {
    return Collections.singletonList(buildOutput(pageNumber, List.of(buildEntity(entity))));
  }

  private static PiiEntity buildEntity(String entity) {
    return PiiEntity.builder().type(entity).score(0.99f).build();
  }

  private static ComprehendFileOutput buildOutput(int pageNumber, List<PiiEntity> entities) {
    return ComprehendFileOutput.builder()
        .entities(entities)
        .fileName(String.format("%d.out", pageNumber))
        .build();
  }
}
