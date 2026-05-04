package org.goldstandard.assurance.api.service.comprehend;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.configuration.S3ConfigurationProperties;
import org.goldstandard.assurance.api.util.S3Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.comprehend.model.DescribePiiEntitiesDetectionJobRequest;
import software.amazon.awssdk.services.comprehend.model.InputFormat;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionMode;
import software.amazon.awssdk.services.comprehend.model.StartPiiEntitiesDetectionJobRequest;

@Component
@RequiredArgsConstructor
public class ComprehendRequestFactory {

  @Value("${comprehend.dataAccessRoleArn}")
  private String dataAccessRoleArn;

  private final S3ConfigurationProperties s3ConfigurationProperties;

  public StartPiiEntitiesDetectionJobRequest startDetectPiiEntitiesRequest(String s3InputFolder) {
    var bucketName = s3ConfigurationProperties.getPiiScanningBucketName();
    return StartPiiEntitiesDetectionJobRequest.builder()
        .dataAccessRoleArn(dataAccessRoleArn)
        .inputDataConfig(
            inputData ->
                inputData
                    .s3Uri(S3Utils.toS3Uri(bucketName, s3InputFolder))
                    .inputFormat(InputFormat.ONE_DOC_PER_FILE))
        .outputDataConfig(outputData -> outputData.s3Uri(S3Utils.toS3Uri(bucketName, "output")))
        .mode(PiiEntitiesDetectionMode.ONLY_OFFSETS)
        .languageCode("en")
        .build();
  }

  public DescribePiiEntitiesDetectionJobRequest describeDetectEntitiesJobRequest(String jobId) {
    return DescribePiiEntitiesDetectionJobRequest.builder().jobId(jobId).build();
  }
}
