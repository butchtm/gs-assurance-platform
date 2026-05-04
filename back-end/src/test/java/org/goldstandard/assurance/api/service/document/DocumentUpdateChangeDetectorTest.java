package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.junit.jupiter.api.Test;

class DocumentUpdateChangeDetectorTest {

  private final DocumentUpdateChangeDetector detector = new DocumentUpdateChangeDetector();

  @Test
  void shouldReturnTrueIfIsLatestChanged() {
    var original = Document.builder().isLatest(false).build();
    var updated = Document.builder().isLatest(true).build();

    boolean changed = detector.hasChanged(original, updated);

    assertThat(changed).isTrue();
  }

  @Test
  void shouldReturnTrueIfAccessChanged() {
    var original = Document.builder().access(false).build();
    var updated = Document.builder().access(true).build();

    boolean changed = detector.hasChanged(original, updated);

    assertThat(changed).isTrue();
  }

  @Test
  void shouldReturnTrueIfCategoryChanged() {
    var original = Document.builder().category(false).build();
    var updated = Document.builder().category(true).build();

    boolean changed = detector.hasChanged(original, updated);

    assertThat(changed).isTrue();
  }

  @Test
  void shouldReturnTrueIfDocumentTypeChanged() {
    var original = Document.builder().documentType(new DocumentType(0)).build();
    var updated = Document.builder().documentType(new DocumentType(1)).build();

    boolean changed = detector.hasChanged(original, updated);

    assertThat(changed).isTrue();
  }

  @Test
  void shouldReturnTrueIfDocumentCategoryTypeChanged() {
    var original = Document.builder().documentCategoryType(0).build();
    var updated = Document.builder().documentCategoryType(1).build();

    boolean changed = detector.hasChanged(original, updated);

    assertThat(changed).isTrue();
  }

  @Test
  void shouldReturnFalseIfNoUpdatableFieldsChanged() {
    var original = Document.builder().build();
    var updated = Document.builder().build();

    boolean changed = detector.hasChanged(original, updated);

    assertThat(changed).isFalse();
  }
}
