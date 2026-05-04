package org.goldstandard.assurance.api.service.finance;

import com.azure.storage.blob.BlobContainerClient;
import com.nimbusds.jose.util.Base64;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.service.document.WritableFileStore;

@Slf4j
@RequiredArgsConstructor
public class AzureFileStore implements WritableFileStore {

  private final BlobContainerClient client;

  @Override
  public String uploadStream(String path, InputStream inputStream, Long contentSize) {
    var blob = client.getBlobClient(path);
    blob.upload(inputStream, contentSize);
    var checksum = Base64.encode(blob.getProperties().getContentMd5()).decodeToString();
    log.info("blob uploaded {} with checksum {} from path {}", blob.getBlobUrl(), checksum, path);
    return checksum;
  }
}
