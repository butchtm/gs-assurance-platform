package org.goldstandard.assurance.api.service.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.springframework.context.annotation.Profile;

@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class StubbedFileStore implements FileStore {

  private final PathPrefixHandler prefixHandler;
  private final Map<String, byte[]> files;
  private final Supplier<String> checksumSupplier;

  public StubbedFileStore() {
    this(new PathPrefixHandler());
  }

  public StubbedFileStore(PathPrefixHandler prefixHandler) {
    this(prefixHandler, new HashMap<>(), StubbedFileStore::generateChecksum);
  }

  @Override
  public boolean objectExists(String key) {
    return getObject(key).isPresent();
  }

  @Override
  public InputStream getObjectAsStream(String key) {
    return getObject(key)
        .map(ByteArrayInputStream::new)
        .orElseThrow(
            () -> new FileStoreException(String.format("object with key %s not found", key)));
  }

  @Override
  public List<String> getKeysInFolder(String uri) {
    var prefix = prefixHandler.toPrefix(uri);
    var keys = files.keySet();
    return keys.stream().filter(key -> key.startsWith(prefix)).toList();
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
      var bytes = inputStream.readAllBytes();
      files.put(key, bytes);
      var data = new String(bytes, StandardCharsets.UTF_8);
      var checksum = checksumSupplier.get();
      log.info("{} persisted to file store with checksum {} for data {}", key, checksum, data);
      return checksum;
    } catch (IOException e) {
      throw new FileStoreException(e.getMessage(), e);
    }
  }

  private Optional<byte[]> getObject(String key) {
    return Optional.ofNullable(files.get(key));
  }

  private String generatePresignedUrl(String path, ZonedDateTime expiry) {
    var uri = String.format("http://stubbed-presigned-url?path=%s&expiry=%s", path, expiry);
    return URLEncoder.encode(uri, StandardCharsets.UTF_8);
  }

  private static String generateChecksum() {
    return UUID.randomUUID().toString();
  }
}
