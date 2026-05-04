package org.goldstandard.assurance.api.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.entity.Document;
import org.junit.jupiter.api.Test;

class S3UtilsTest {

  @Test
  void shouldReturnS3KeyForDocument() {
    var document = Document.builder().fileName("file1.txt").containerName("GS12").build();
    assertThat(S3Utils.toKey(document)).isEqualTo("GS12/file1.txt");
  }

  @Test
  void shouldReturnS3KeyForFileAndContainerName() {
    assertThat(S3Utils.toKey("GS12", "file1.txt")).isEqualTo("GS12/file1.txt");
  }

  @Test
  void shouldReturnS3UriForBucketAndFolderName() {
    assertThat(S3Utils.toS3Uri("scan-bucket", "doc-folder"))
        .isEqualTo("s3://scan-bucket/doc-folder");
  }
}
