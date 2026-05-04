package org.goldstandard.assurance.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3ConfigurationProperties {

  private String regionName;
  private String endpointOverride;
  private BucketName bucketName;

  public String getDocumentBucketName() {
    return bucketName.getDocument();
  }

  public String getDownloadBucketName() {
    return bucketName.getDownload();
  }

  public String getPiiScanningBucketName() {
    return bucketName.getPiiScanning();
  }

  @Getter
  @Setter
  private static class BucketName {
    private String document;
    private String download;
    private String piiScanning;
  }
}
