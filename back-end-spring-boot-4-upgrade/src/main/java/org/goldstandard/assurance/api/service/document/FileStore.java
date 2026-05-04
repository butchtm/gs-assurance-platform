package org.goldstandard.assurance.api.service.document;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;

public interface FileStore extends WritableFileStore {

  boolean objectExists(String path);

  InputStream getObjectAsStream(String path);

  PresignedUrlResult generatePresignedUrl(
      String key, ConvertibleOutputStream outputStream, ZonedDateTime expiry);

  PresignedUrlResult generatePresignedUrl(
      String path, InputStream inputStream, Long contentSize, ZonedDateTime expiry);

  List<String> getKeysInFolder(String s3FolderUri);
}
