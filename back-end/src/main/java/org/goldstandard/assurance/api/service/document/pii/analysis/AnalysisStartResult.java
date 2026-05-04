package org.goldstandard.assurance.api.service.document.pii.analysis;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AnalysisStartResult {
  private final String comprehendJobId;
  private final String comprehendJobStatus;
  private final boolean textFound;
}
