package org.goldstandard.assurance.api.service.comprehend;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.dto.FieldTextAnalysisDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.service.document.pii.analysis.AnalysisStartResult;
import org.goldstandard.assurance.api.service.pii.ProbablePiiFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectPiiEntitiesRequest;
import software.amazon.awssdk.services.comprehend.model.LanguageCode;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@Slf4j
@Profile("!local && !test")
@Service
@RequiredArgsConstructor
public class ComprehendServiceDefault implements ComprehendService {

  private final ComprehendClient comprehendClient;
  private final ComprehendRequestFactory requestFactory;
  private final ComprehendPiiConfigFilter filter;

  @Override
  public List<ProbablePii> detectEntities(Collection<FieldTextAnalysisDTO> textCollection) {
    return textCollection.stream().flatMap(text -> detectEntities(text).stream()).toList();
  }

  @Override
  public List<ProbablePii> detectDocumentEntities(Collection<ExtractedText> textCollection) {
    return textCollection.stream()
        .flatMap(extractedText -> detectEntities(extractedText).stream())
        .toList();
  }

  @Override
  public AnalysisStartResult detectDocumentEntitiesAsync(String s3InputFolder) {
    var request = requestFactory.startDetectPiiEntitiesRequest(s3InputFolder);
    var response = comprehendClient.startPiiEntitiesDetectionJob(request);
    return AnalysisStartResult.builder()
        .comprehendJobId(response.jobId())
        .comprehendJobStatus(response.jobStatusAsString())
        .textFound(true)
        .build();
  }

  @Override
  public PiiEntitiesDetectionJobProperties getDetectPiiJob(String jobId) {
    var getRequest = requestFactory.describeDetectEntitiesJobRequest(jobId);
    var result = comprehendClient.describePiiEntitiesDetectionJob(getRequest);
    return result.piiEntitiesDetectionJobProperties();
  }

  private List<ProbablePii> detectEntities(FieldTextAnalysisDTO fieldText) {
    return detectEntities(fieldText.getText()).stream()
        .map(piiEntity -> ProbablePiiFactory.buildDetectedPii(fieldText, piiEntity))
        .toList();
  }

  private List<PiiEntity> detectEntities(String text) {
    var request = buildRequest(text);
    var result = comprehendClient.detectPiiEntities(request);
    return filter.filterByConfigProperties(result.entities());
  }

  private DetectPiiEntitiesRequest buildRequest(String text) {
    return DetectPiiEntitiesRequest.builder().text(text).languageCode(LanguageCode.EN).build();
  }

  private List<ProbablePii> detectEntities(ExtractedText extractedText) {
    return detectEntities(extractedText.getText()).stream()
        .map(piiEntity -> ProbablePiiFactory.buildDetectedPii(extractedText, piiEntity))
        .toList();
  }
}
