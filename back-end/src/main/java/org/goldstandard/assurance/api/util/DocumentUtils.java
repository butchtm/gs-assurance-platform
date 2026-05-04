package org.goldstandard.assurance.api.util;

import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.springframework.http.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentUtils {

  public static final String IMAGE_TIFF = "image/tiff";
  public static final String WORD_DOCX =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  public static final String WORD_DOC = "application/msword";
  public static final List<String> WORD_DOC_TYPES = List.of(WORD_DOCX, WORD_DOC);

  public static final String EXCEL_XLSX =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  public static long getFileSizeMb(Document document) {
    return Optional.ofNullable(document.getFileSize()).map(DocumentUtils::toMegaBytes).orElse(0L);
  }

  public static long toMegaBytes(long bytes) {
    return (long) (bytes / (1024.0 * 1024.0));
  }

  public static boolean isPagedDocument(String contentType) {
    return MediaType.APPLICATION_PDF_VALUE.equals(contentType)
        || IMAGE_TIFF.equals(contentType)
        || isWordDoc(contentType);
  }

  public static boolean isPdfOrTiff(String contentType) {
    return MediaType.APPLICATION_PDF_VALUE.equals(contentType) || IMAGE_TIFF.equals(contentType);
  }

  public static boolean isJpegOrPng(String contentType) {
    return MediaType.IMAGE_JPEG_VALUE.equals(contentType)
        || MediaType.IMAGE_PNG_VALUE.equals(contentType);
  }

  public static boolean isWordDoc(String contentType) {
    return WORD_DOC_TYPES.contains(contentType);
  }

  public static boolean isTextractScannable(String contentType) {
    return isPdfOrTiff(contentType) || isJpegOrPng(contentType);
  }
}
