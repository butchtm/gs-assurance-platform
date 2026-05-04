package org.goldstandard.assurance.api.service.comprehend;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.goldstandard.assurance.api.service.document.pii.analysis.AnalysisStartResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;

@Profile("local || test")
@Service
@Slf4j
public class ComprehendServiceStubbed implements ComprehendService {

  @Override
  public Collection<ProbablePii> detectEntities(Collection<FieldTextAnalysisDTO> fields) {
    log.info(
        "Comprehend entity detection stubbed for text: {}",
        fields.stream().map(FieldTextAnalysisDTO::getText).toList());
    return fields.stream()
        .map(ComprehendServiceStubbed::toProbablePii)
        .flatMap(Optional::stream)
        .toList();
  }

  @Override
  public Collection<ProbablePii> detectDocumentEntities(Collection<ExtractedText> textCollection) {
    log.info(
        "Comprehend entity detection stubbed for extracted text: {}",
        textCollection.stream().map(ExtractedText::getText).toList());
    return Collections.emptyList();
  }

  @Override
  public AnalysisStartResult detectDocumentEntitiesAsync(String s3InputFolder) {
    log.info("Start entity detection job stubbed for file: {}", s3InputFolder);
    return AnalysisStartResult.builder()
        .comprehendJobId("StubJobId")
        .comprehendJobStatus("StubJobStatus")
        .build();
  }

  @Override
  public PiiEntitiesDetectionJobProperties getDetectPiiJob(String jobId) {
    log.info("Get current pii job status stubbed for job: {}", jobId);
    return PiiEntitiesDetectionJobProperties.builder()
        .jobStatus(jobId)
        .jobStatus("COMPLETED")
        .build();
  }

  private static Optional<ProbablePii> toProbablePii(FieldTextAnalysisDTO fieldText) {
    if (!containsStubbedPii(fieldText)) {
      return Optional.empty();
    }
    return Optional.of(
        ProbablePii.builder()
            .entity("STUBBED_ENTITY")
            .probability(PiiProbability.HIGH)
            .field(fieldText.getField())
            .identifiedString(fieldText.getText())
            .build());
  }

  private static boolean containsStubbedPii(FieldTextAnalysisDTO fieldText) {
    return StringUtils.isNotEmpty(fieldText.getText())
        && fieldText.getText().contains("stubbed-pii");
  }
}
