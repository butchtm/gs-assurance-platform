package org.goldstandard.assurance.api.service.textract;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.textract.model.GetDocumentTextDetectionRequest;
import software.amazon.awssdk.services.textract.model.StartDocumentTextDetectionRequest;

@Component
public class TextractServiceRequestFactory {

  public GetDocumentTextDetectionRequest getDetectionRequest(String jobId) {
    return GetDocumentTextDetectionRequest.builder().jobId(jobId).build();
  }

  public GetDocumentTextDetectionRequest getDetectionRequestWithToken(String jobId, String token) {
    return GetDocumentTextDetectionRequest.builder().jobId(jobId).nextToken(token).build();
  }

  public StartDocumentTextDetectionRequest startDetectionRequest(
      String bucketName, String documentName) {
    return StartDocumentTextDetectionRequest.builder()
        .documentLocation(
            docLocation ->
                docLocation.s3Object(s3Obj -> s3Obj.bucket(bucketName).name(documentName)))
        .build();
  }
}
