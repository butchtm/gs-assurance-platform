package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import org.goldstandard.assurance.api.entity.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DocumentPiiAnalyserFactoryTest {

  private DocumentPiiAnalyserFactory factory;
  private TextractDocumentPiiAnalyser pdfAnalyser;

  @BeforeEach
  void setup() throws IllegalAccessException, NoSuchFieldException {
    pdfAnalyser = new TextractDocumentPiiAnalyser(null, null, null, null);
    factory = new DocumentPiiAnalyserFactory(List.of(pdfAnalyser));
    // The below code can be removed once the documentPiiScanningEnabled flag is no longer needed
    Field documentPiiScanningFlagField =
        DocumentPiiAnalyserFactory.class.getDeclaredField("documentPiiScanningEnabled");
    documentPiiScanningFlagField.setAccessible(true);
    documentPiiScanningFlagField.set(factory, true);
  }

  @Test
  void shouldReturnAnalyserMatchingCriteria() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.APPLICATION_PDF_VALUE)
            .fileSizeMb(499L)
            .build();

    assertThat(factory.getAnalyser(criteria)).isPresent().get().isEqualTo(pdfAnalyser);
  }

  @Test
  void shouldReturnAnalyserMatchingDocument() {
    var document =
        Document.builder().contentType(MediaType.APPLICATION_PDF_VALUE).fileSize(1000L).build();

    assertThat(factory.getAnalyser(document)).isPresent().get().isEqualTo(pdfAnalyser);
  }
}
