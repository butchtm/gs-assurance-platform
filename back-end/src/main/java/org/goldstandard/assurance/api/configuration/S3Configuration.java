package org.goldstandard.assurance.api.configuration;

import java.net.URI;
import java.time.Clock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.service.document.AWSS3FileStore;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.document.PathPrefixHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * For local development AWS credentials can be set as environment variables in the IntelliJ run
 * configuration. AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY with session token if required for
 * temporary credentials AWS_SESSION_TOKEN
 */
@Slf4j
@Configuration
@Profile("!local")
public class S3Configuration {

  @Autowired private S3ConfigurationProperties properties;

  @Bean
  public S3Client s3Client() {
    var credentialsProvider = DefaultCredentialsProvider.builder().build();
    var regionName = properties.getRegionName();
    var builder =
        S3Client.builder().credentialsProvider(credentialsProvider).region(Region.of(regionName));
    log.info("configured S3 client with region {}", regionName);
    var endpointOverride = properties.getEndpointOverride();
    if (StringUtils.isNotEmpty(endpointOverride)) {
      builder.endpointOverride(URI.create(endpointOverride)).forcePathStyle(true);
      log.info("configured S3 client with endpoint override {}", endpointOverride);
    }
    return builder.build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    var credentialsProvider = DefaultCredentialsProvider.builder().build();
    var regionName = properties.getRegionName();
    var builder =
        S3Presigner.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(regionName));
    log.info("configured S3 presigner with region {}", regionName);
    var endpointOverride = properties.getEndpointOverride();
    if (StringUtils.isNotEmpty(endpointOverride)) {
      builder.endpointOverride(URI.create(endpointOverride));
      log.info("configured S3 presigner with endpoint override {}", endpointOverride);
    }
    return builder.build();
  }

  @Bean
  public FileStore documentFileStore(
      S3Client client, S3Presigner presigner, Clock clock, PathPrefixHandler prefixHandler) {
    var bucketName = properties.getDocumentBucketName();
    log.info("configuring document file store with bucket name {}", bucketName);
    return AWSS3FileStore.builder()
        .s3client(client)
        .s3Presigner(presigner)
        .bucketName(bucketName)
        .clock(clock)
        .prefixHandler(prefixHandler)
        .build();
  }

  @Bean
  public FileStore downloadFileStore(
      S3Client client, S3Presigner presigner, Clock clock, PathPrefixHandler prefixHandler) {
    var bucketName = properties.getDownloadBucketName();
    log.info("configuring download file store with bucket name {}", bucketName);
    return AWSS3FileStore.builder()
        .s3client(client)
        .s3Presigner(presigner)
        .bucketName(bucketName)
        .clock(clock)
        .prefixHandler(prefixHandler)
        .build();
  }

  @Bean
  public FileStore piiScanningFileStore(
      S3Client client, S3Presigner presigner, Clock clock, PathPrefixHandler prefixHandler) {
    var bucketName = properties.getPiiScanningBucketName();
    log.info("configuring scanning file store with bucket name {}", bucketName);
    return AWSS3FileStore.builder()
        .s3client(client)
        .s3Presigner(presigner)
        .bucketName(bucketName)
        .clock(clock)
        .prefixHandler(prefixHandler)
        .build();
  }
}
