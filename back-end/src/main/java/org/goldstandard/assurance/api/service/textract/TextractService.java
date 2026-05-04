package org.goldstandard.assurance.api.service.textract;

import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.dto.ExtractedText;

public interface TextractService {
  /**
   * @return Job ID to extract results from
   */
  String doDetectTextJob(String bucketName, String documentName);

  Stream<List<ExtractedText>> extractWords(String jobId);
}
