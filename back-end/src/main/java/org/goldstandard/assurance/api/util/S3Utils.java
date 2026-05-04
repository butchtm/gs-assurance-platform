package org.goldstandard.assurance.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class S3Utils {

  public static String toKey(Document document) {
    return toKey(document.getContainerName(), document.getFileName());
  }

  public static String toKey(String container, String file) {
    return String.format("%s/%s", container, file);
  }

  public static String toS3Uri(String bucket, String folder) {
    return String.format("s3://%s/%s", bucket, folder);
  }
}
