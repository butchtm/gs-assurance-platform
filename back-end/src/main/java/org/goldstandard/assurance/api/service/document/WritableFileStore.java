package org.goldstandard.assurance.api.service.document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public interface WritableFileStore {

  default String uploadString(String path, String input) {
    byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
    return uploadStream(path, new ByteArrayInputStream(bytes), (long) bytes.length);
  }

  String uploadStream(String path, InputStream inputStream, Long contentSize);
}
