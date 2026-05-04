package org.goldstandard.assurance.api.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.entity.Document;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DocumentUtilsTest {

  @Test
  void shouldReturnFileSizeInMb() {
    var document = Document.builder().fileSize(2097152L).build();
    assertThat(DocumentUtils.getFileSizeMb(document)).isEqualTo(2L);
  }

  @Test
  void shouldReturnTrueForPagedDocuments() {
    assertThat(DocumentUtils.isPagedDocument(MediaType.APPLICATION_PDF_VALUE)).isTrue();
    assertThat(DocumentUtils.isPagedDocument(DocumentUtils.IMAGE_TIFF)).isTrue();
    assertThat(DocumentUtils.isPagedDocument(DocumentUtils.WORD_DOCX)).isTrue();
    assertThat(DocumentUtils.isPagedDocument(DocumentUtils.WORD_DOC)).isTrue();
  }

  @Test
  void shouldReturnFalseForNonPagedDocuments() {
    assertThat(DocumentUtils.isPagedDocument(MediaType.IMAGE_JPEG_VALUE)).isFalse();
    assertThat(DocumentUtils.isPagedDocument(MediaType.IMAGE_PNG_VALUE)).isFalse();
  }

  @Test
  void shouldReturnTrueForTextractScannableFileTypes() {
    assertThat(DocumentUtils.isTextractScannable(MediaType.APPLICATION_PDF_VALUE)).isTrue();
    assertThat(DocumentUtils.isTextractScannable(DocumentUtils.IMAGE_TIFF)).isTrue();
    assertThat(DocumentUtils.isTextractScannable(MediaType.IMAGE_JPEG_VALUE)).isTrue();
    assertThat(DocumentUtils.isTextractScannable(MediaType.IMAGE_PNG_VALUE)).isTrue();
  }

  @Test
  void shouldReturnFalseForNonTextractScannableFileTypes() {
    assertThat(DocumentUtils.isTextractScannable(DocumentUtils.WORD_DOCX)).isFalse();
  }
}
