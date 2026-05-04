package org.goldstandard.assurance.api.service.textract;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("local || test")
@Service
@Slf4j
public class TextractServiceStubbed implements TextractService {

  @Override
  public String doDetectTextJob(String bucketName, String documentName) {
    log.info(
        "Returning dummy job ID for doDetectTextJob for bucket {} and document {}",
        bucketName,
        documentName);
    return "job1";
  }

  @Override
  public Stream<List<ExtractedText>> extractWords(String jobId) {
    log.info("Returning empty list for Textract stubbed service extractWords");
    return Stream.of(Collections.emptyList());
  }
}
