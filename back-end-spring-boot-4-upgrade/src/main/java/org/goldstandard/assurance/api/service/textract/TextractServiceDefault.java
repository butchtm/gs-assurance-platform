package org.goldstandard.assurance.api.service.textract;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.JobStatus;

@Profile("!local && !test")
@Slf4j
@Service
@RequiredArgsConstructor
public class TextractServiceDefault implements TextractService {

  private final TextractClient textractClient;
  private final TextractServiceRequestFactory requestFactory;
  private final TextractDelayHandler delayHandler;
  private final TextractResultProcessor resultProcessor;

  @Override
  public String doDetectTextJob(String bucketName, String documentName) {
    var jobId = startDetectTextJob(bucketName, documentName);
    log.info("Started Textract job with ID: {}", jobId);
    awaitJobCompletion(jobId);
    log.info("Textract job with ID {} completed", jobId);
    return jobId;
  }

  private String startDetectTextJob(String bucketName, String documentName) {
    var request = requestFactory.startDetectionRequest(bucketName, documentName);
    var response = textractClient.startDocumentTextDetection(request);
    return response.jobId();
  }

  private void awaitJobCompletion(String jobId) {
    var getRequest = requestFactory.getDetectionRequest(jobId);
    while (true) {
      delayHandler.delayBeforeNextPoll();
      var result = textractClient.getDocumentTextDetection(getRequest);
      var jobStatus = result.jobStatus();

      if (JobStatus.SUCCEEDED.equals(jobStatus)) {
        return;
      } else if (JobStatus.FAILED.equals(jobStatus)) {
        log.error("Job with ID {} was not successful.", jobId);
        throw new DocumentPiiScanException("Job with ID " + jobId + " failed");
      }
    }
  }

  @Override
  public Stream<List<ExtractedText>> extractWords(String jobId) {
    return resultProcessor.streamByResponseToken(jobId);
  }
}
