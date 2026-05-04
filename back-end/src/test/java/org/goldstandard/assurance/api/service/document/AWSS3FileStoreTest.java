package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ExtendWith(MockitoExtension.class)
class AWSS3FileStoreTest {
  private static final String BUCKET_NAME = "bucket_name";
  private static final String BLOB_NAME = "test_blob";
  private static final String ERROR_MESSAGE = "error message";
  private static final String BLOB = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
  private static final String BLOB_SHA256SUM =
      "a58dd8680234c1f8cc2ef2b325a43733605a7f16f288e072de8eae81fd8d6433";
  private static final Long BLOB_LENGTH = (long) BLOB.getBytes().length;

  private AWSS3FileStore fileStore;

  @Mock S3Client s3client;

  @Mock S3Presigner s3Presigner;

  @Mock PathPrefixHandler prefixHandler;

  @BeforeEach
  void setUp() {
    fileStore =
        AWSS3FileStore.builder()
            .s3client(s3client)
            .s3Presigner(s3Presigner)
            .prefixHandler(prefixHandler)
            .bucketName(BUCKET_NAME)
            .build();
  }

  @Test
  void objectExistsShouldReturnTrueIfBlobPresent() {
    var matches = new HeadObjectRequestMatcher(BLOB_NAME, BUCKET_NAME);
    var response = HeadObjectResponse.builder().build();
    when(s3client.headObject(argThat(matches))).thenReturn(response);

    var exists = fileStore.objectExists(BLOB_NAME);

    assertThat(exists).isTrue();
  }

  @Test
  void objectExistsShouldReturnFalseIfBlobNotPresent() {
    var noSuchKeyException = NoSuchKeyException.builder().build();
    when(s3client.headObject(any(HeadObjectRequest.class))).thenThrow(noSuchKeyException);

    var exists = fileStore.objectExists(BLOB_NAME);

    assertThat(exists).isFalse();
  }

  @Test
  void objectExistsShouldThrowFileStoreExceptionIfS3ExceptionThrown() {
    var s3Exception = S3Exception.builder().message(ERROR_MESSAGE).build();
    when(s3client.headObject(any(HeadObjectRequest.class))).thenThrow(s3Exception);

    var exception = catchThrowable(() -> fileStore.objectExists(BLOB_NAME));

    assertThat(exception).isInstanceOf(FileStoreException.class).hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldGetObjectAsStream() throws IOException {
    try (var testStream = new ByteArrayInputStream(BLOB.getBytes())) {
      var matches = new GetObjectRequestMatcher(BLOB_NAME, BUCKET_NAME);
      var response = GetObjectResponse.builder().build();
      when(s3client.getObject(argThat(matches)))
          .thenReturn(new ResponseInputStream<>(response, testStream));

      var result = fileStore.getObjectAsStream(BLOB_NAME);

      assertThat(result.readAllBytes()).isEqualTo(BLOB.getBytes());
    }
  }

  @Test
  void getStreamShouldThrowFileStoreExceptionIfS3ExceptionThrown() {
    var s3Exception = S3Exception.builder().message(ERROR_MESSAGE).build();
    when(s3client.getObject(any(GetObjectRequest.class))).thenThrow(s3Exception);

    var exception = catchThrowable(() -> fileStore.getObjectAsStream(BLOB_NAME));

    assertThat(exception).isInstanceOf(FileStoreException.class).hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldPassPrefixWhenGettingKeysForFolder() {
    var folderName = "test_folder";
    var path = String.format("%s/%s", folderName, BLOB_NAME);
    var uri = String.format("s3://%s/%s", BUCKET_NAME, path);
    when(prefixHandler.toPrefix(uri)).thenReturn(path);
    var response = ListObjectsV2Response.builder().build();
    when(s3client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

    fileStore.getKeysInFolder(uri);

    var captor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
    verify(s3client).listObjectsV2(captor.capture());
    var requestUsed = captor.getValue();
    assertThat(requestUsed.bucket()).isEqualTo(BUCKET_NAME);
    assertThat(requestUsed.prefix()).isEqualTo(path);
  }

  @Test
  void shouldGetKeysWithinSpecifiedFolder() {
    var response =
        ListObjectsV2Response.builder()
            .contents(List.of(S3Object.builder().key("1.out").build()))
            .build();
    when(s3client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

    var keys = fileStore.getKeysInFolder("any-uri");

    assertThat(keys).hasSize(1);
    assertThat(keys.getFirst()).isEqualTo("1.out");
  }

  @Test
  void shouldReturnChecksumForSuccessfulUpload() throws IOException {
    try (var blobStream = new ByteArrayInputStream(BLOB.getBytes())) {
      var matches = new PutObjectRequestMatcher(BLOB_NAME, BUCKET_NAME);
      var response = PutObjectResponse.builder().checksumSHA256(BLOB_SHA256SUM).build();
      when(s3client.putObject(argThat(matches), any(RequestBody.class))).thenReturn(response);

      var sha256Sum = fileStore.uploadStream(BLOB_NAME, blobStream, BLOB_LENGTH);

      assertThat(sha256Sum).isEqualTo(BLOB_SHA256SUM);
    }
  }

  @Test
  void uploadShouldThrowFileStoreExceptionIfS3ExceptionThrown() throws IOException {
    try (var blobStream = new ByteArrayInputStream(BLOB.getBytes())) {
      var s3Exception = S3Exception.builder().message(ERROR_MESSAGE).build();
      when(s3client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
          .thenThrow(s3Exception);

      var exception =
          catchThrowable(() -> fileStore.uploadStream(BLOB_NAME, blobStream, BLOB_LENGTH));

      assertThat(exception).isInstanceOf(FileStoreException.class).hasMessage(ERROR_MESSAGE);
    }
  }

  @RequiredArgsConstructor
  private static class HeadObjectRequestMatcher implements ArgumentMatcher<HeadObjectRequest> {

    private final HeadObjectRequest expected;

    public HeadObjectRequestMatcher(String expectedKey, String expectedBucket) {
      this(HeadObjectRequest.builder().key(expectedKey).bucket(expectedBucket).build());
    }

    @Override
    public boolean matches(HeadObjectRequest supplied) {
      return Objects.nonNull(supplied)
          && Objects.equals(supplied.bucket(), expected.bucket())
          && Objects.equals(supplied.key(), expected.key());
    }
  }

  @RequiredArgsConstructor
  private static class GetObjectRequestMatcher implements ArgumentMatcher<GetObjectRequest> {

    private final GetObjectRequest expected;

    public GetObjectRequestMatcher(String expectedKey, String expectedBucket) {
      this(GetObjectRequest.builder().key(expectedKey).bucket(expectedBucket).build());
    }

    @Override
    public boolean matches(GetObjectRequest supplied) {
      return Objects.nonNull(supplied)
          && Objects.equals(supplied.bucket(), expected.bucket())
          && Objects.equals(supplied.key(), expected.key());
    }
  }

  @RequiredArgsConstructor
  private static class PutObjectRequestMatcher implements ArgumentMatcher<PutObjectRequest> {

    private final PutObjectRequest expected;

    public PutObjectRequestMatcher(String expectedKey, String expectedBucket) {
      this(PutObjectRequest.builder().key(expectedKey).bucket(expectedBucket).build());
    }

    @Override
    public boolean matches(PutObjectRequest supplied) {
      return Objects.nonNull(supplied)
          && Objects.equals(supplied.bucket(), expected.bucket())
          && Objects.equals(supplied.key(), expected.key());
    }
  }
}
