package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExtractedTextS3Writer {
  private final FileStore fileStore;

  private static final String S3_PATH_SEPARATOR = "/";

  public ExtractedTextS3Writer(@Qualifier("piiScanningFileStore") final FileStore fileStore) {
    this.fileStore = fileStore;
  }

  public void writePagesToS3(List<ExtractedText> extractedText, String s3InputFolder) {
    var pageNumbers = getUniquePageNumbers(extractedText);
    for (var pageNumber : pageNumbers) {
      var textForPage = getTextForPage(extractedText, pageNumber);
      uploadOrAppendToS3Page(textForPage, s3InputFolder, pageNumber);
    }
  }

  private static String getTextForPage(List<ExtractedText> extractedText, int pageNumber) {
    return extractedText.stream()
        .filter(page -> page.getPageNumber() == pageNumber)
        .map(ExtractedText::getText)
        .collect(Collectors.joining(" "));
  }

  private static List<Integer> getUniquePageNumbers(List<ExtractedText> extractedText) {
    return extractedText.stream()
        .mapToInt(ExtractedText::getPageNumber)
        .distinct()
        .sorted()
        .boxed()
        .toList();
  }

  private void uploadOrAppendToS3Page(String text, String s3InputFolder, int pageNumber) {
    var pageText = new StringBuilder(text);
    var filePath = s3InputFolder + S3_PATH_SEPARATOR + pageNumber;
    prependExistingFileIfExists(filePath, pageText);
    fileStore.uploadStream(
        filePath,
        new ByteArrayInputStream(pageText.toString().getBytes()),
        (long) pageText.length());
  }

  private void prependExistingFileIfExists(String filePath, StringBuilder pageText) {
    try (var existingFile = fileStore.getObjectAsStream(filePath)) {
      if (existingFile != null) {
        pageText.insert(0, new String(existingFile.readAllBytes(), StandardCharsets.UTF_8) + " ");
      }
    } catch (IOException | FileStoreException e) {
      // No file currently uploaded for this page, file will be created anew
    }
  }
}
