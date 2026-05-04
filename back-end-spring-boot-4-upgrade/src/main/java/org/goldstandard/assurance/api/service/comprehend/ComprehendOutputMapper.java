package org.goldstandard.assurance.api.service.comprehend;

import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileOutput;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendFileRawOutput;
import org.goldstandard.assurance.api.dto.comprehend.ComprehendPiiEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@Component
public class ComprehendOutputMapper {

  public ComprehendFileOutput mapFromRawOutput(ComprehendFileRawOutput rawOutput) {
    return ComprehendFileOutput.builder()
        .entities(rawOutput.getEntities().stream().map(this::toPiiEntity).toList())
        .fileName(rawOutput.getFile())
        .build();
  }

  private PiiEntity toPiiEntity(ComprehendPiiEntity comprehendPiiEntity) {
    return PiiEntity.builder()
        .beginOffset(comprehendPiiEntity.getBeginOffset())
        .endOffset(comprehendPiiEntity.getEndOffset())
        .type(comprehendPiiEntity.getType())
        .score(comprehendPiiEntity.getScore())
        .build();
  }
}
