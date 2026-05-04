package org.goldstandard.assurance.api.service.document;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
@Component
@Slf4j
public class DocumentStreamer {

  private final UniqueFilenameCalculator uniqueFilenameCalculator;
  private final DocumentStoreService documentStoreService;

  public void streamDocument(Document document, HttpServletResponse response) throws IOException {
    try (InputStream inStream = documentStoreService.retrieveDocumentAsStream(document);
        OutputStream outStream = response.getOutputStream()) {
      StreamUtils.copy(inStream, outStream);
    }
  }

  public StreamingResponseBody toZipStream(Collection<Document> documents) {
    validateDocumentsExist(documents);
    var uniqueFilenames = uniqueFilenameCalculator.toSortedUniqueFilenames(documents);
    return responseStream -> {
      var start = Instant.now();
      log.info("streaming {} documents to zip", documents.size());
      try (ZipOutputStream zos = new ZipOutputStream(responseStream)) {
        for (Map.Entry<String, Document> document : uniqueFilenames.entrySet()) {
          writeToZip(zos, document.getKey(), document.getValue());
        }
      } finally {
        var duration = Duration.between(start, Instant.now());
        log.info("streaming {} documents to zip took {}ms", documents.size(), duration.toMillis());
      }
    };
  }

  private void validateDocumentsExist(Collection<Document> documents) {
    var start = Instant.now();
    try {
      log.info("validating {} documents exist", documents.size());
      documentStoreService.validateFilesExist(documents);
    } finally {
      var duration = Duration.between(start, Instant.now());
      log.info("validating {} documents exist took {}ms", documents.size(), duration.toMillis());
    }
  }

  private void writeToZip(ZipOutputStream zos, String name, Document document) {
    try (var fis = documentStoreService.forceRetrieveDocumentAsStream(document)) {
      addToZipFile(zos, name, fis);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void addToZipFile(ZipOutputStream zos, String name, InputStream fis)
      throws IOException {
    var zipEntry = new ZipEntry(name);
    zos.putNextEntry(zipEntry);
    StreamUtils.copy(fis, zos);
    zos.closeEntry();
  }
}
