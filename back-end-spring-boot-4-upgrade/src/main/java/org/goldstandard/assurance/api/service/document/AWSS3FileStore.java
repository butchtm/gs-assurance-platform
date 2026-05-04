package org.goldstandard.assurance.api.service.document;

import java.io.InputStream;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.exception.FileStoreException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Builder
@Slf4j
public class AWSS3FileStore implements FileStore {

  private final S3Client s3client;
  private final S3Presigner s3Presigner;
  private final String bucketName;
  private final Clock clock;
  private final PathPrefixHandler prefixHandler;

  @Override
  public boolean objectExists(String key) {
    return getHeadObject(key).isPresent();
  }

  @Override
  public InputStream getObjectAsStream(String key) {
    try {
      var request = GetObjectRequest.builder().bucket(bucketName).key(key).build();
      return s3client.getObject(request);
    } catch (S3Exception e) {
      throw new FileStoreException(e.getMessage(), e);
    }
  }

  @Override
  public List<String> getKeysInFolder(String s3FolderUri) {
    try {
      var request =
          ListObjectsV2Request.builder()
              .bucket(bucketName)
              .prefix(prefixHandler.toPrefix(s3FolderUri))
              .build();
      return s3client.listObjectsV2(request).contents().stream().map(S3Object::key).toList();
    } catch (S3Exception e) {
      throw new FileStoreException(
          String.format(
              "Failed to list keys in folder: %s: %s",
              s3FolderUri, e.awsErrorDetails().errorMessage()),
          e);
    }
  }

  @Override
  public PresignedUrlResult generatePresignedUrl(
      String key, ConvertibleOutputStream outputStream, ZonedDateTime expiry) {
    return generatePresignedUrl(
        key, outputStream.toInputStream(), (long) outputStream.size(), expiry);
  }

  @Override
  public PresignedUrlResult generatePresignedUrl(
      String path, InputStream inputStream, Long contentLength, ZonedDateTime expiry) {
    var checksum = uploadStream(path, inputStream, contentLength);
    var url = generatePresignedUrl(path, expiry);
    return PresignedUrlResult.builder()
        .key(path)
        .expiry(expiry)
        .checksum(checksum)
        .url(url)
        .build();
  }

  @Override
  public String uploadStream(String key, InputStream inputStream, Long contentLength) {
    try {
      var request =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .checksumAlgorithm(ChecksumAlgorithm.SHA256)
              .key(key)
              .build();
      log.info("putting to s3 from input stream with contentLength {}", contentLength);
      var response =
          s3client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
      return response.checksumSHA256();
    } catch (S3Exception e) {
      throw new FileStoreException(e.getMessage(), e);
    }
  }

  private Optional<HeadObjectResponse> getHeadObject(String key) {
    try {
      var request = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
      var response = s3client.headObject(request);
      return Optional.of(response);
    } catch (NoSuchKeyException e) {
      log.debug("object key {} not found in bucket so cannot return head response", key, e);
      return Optional.empty();
    } catch (S3Exception e) {
      throw new FileStoreException(e.getMessage(), e);
    }
  }

  private String generatePresignedUrl(String path, ZonedDateTime expiry) {
    var presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(calculateSignatureDuration(expiry))
            .getObjectRequest(r -> r.bucket(bucketName).key(path))
            .build();
    var presignedRequest = s3Presigner.presignGetObject(presignRequest);
    return presignedRequest.url().toExternalForm();
  }

  private Duration calculateSignatureDuration(ZonedDateTime expiry) {
    var now = ZonedDateTime.now(clock);
    var duration = Duration.between(now, expiry);
    log.debug("calculated duration {} from expiry {} and current time {}", duration, expiry, now);
    return duration;
  }
}
