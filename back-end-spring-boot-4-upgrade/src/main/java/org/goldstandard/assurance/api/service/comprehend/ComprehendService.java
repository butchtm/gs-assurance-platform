package org.goldstandard.assurance.api.service.comprehend;

import java.util.Collection;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.service.document.pii.analysis.AnalysisStartResult;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;

public interface ComprehendService {
  Collection<ProbablePii> detectEntities(Collection<FieldTextAnalysisDTO> text);

  Collection<ProbablePii> detectDocumentEntities(Collection<ExtractedText> textCollection);

  AnalysisStartResult detectDocumentEntitiesAsync(String s3InputFolder);

  PiiEntitiesDetectionJobProperties getDetectPiiJob(String jobId);
}
