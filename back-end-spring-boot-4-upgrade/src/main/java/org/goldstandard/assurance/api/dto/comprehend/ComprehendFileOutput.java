package org.goldstandard.assurance.api.dto.comprehend;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@Getter
@Builder(toBuilder = true)
public class ComprehendFileOutput {
  private final List<PiiEntity> entities;
  private final String fileName;
}
