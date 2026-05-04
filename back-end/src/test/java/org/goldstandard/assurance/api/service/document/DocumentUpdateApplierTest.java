package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentUpdateApplierTest {

  @InjectMocks DocumentUpdateApplier applier;

  @Mock DocumentUpdateChangeDetector changeDetector;

  @Mock Clock clock;

  @Test
  void shouldNotUpdateAccessIfNullOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = emptyUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getAccess()).isEqualTo(original.getAccess());
  }

  @Test
  void shouldNotUpdateCategoryIfNullOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = emptyUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getCategory()).isEqualTo(original.getCategory());
  }

  @Test
  void shouldNotUpdateDocumentTypeIdIfNullOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = emptyUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getDocumentTypeId()).isEqualTo(original.getDocumentTypeId());
  }

  @Test
  void shouldNotUpdateLatestIfNullOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = emptyUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getIsLatest()).isEqualTo(original.getIsLatest());
  }

  @Test
  void shouldNotUpdateCategoryTypeIfNull() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = emptyUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getDocumentCategoryType()).isEqualTo(original.getDocumentCategoryType());
  }

  @Test
  void shouldUpdateAccessIfChangedOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = changedUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getAccess()).isEqualTo(update.access());
  }

  @Test
  void shouldUpdateCategoryIfChangedOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = changedUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getCategory()).isEqualTo(update.category());
  }

  @Test
  void shouldUpdateDocumentTypeIdIfChangedOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = changedUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getDocumentTypeId()).isEqualTo(update.documentTypeId());
  }

  @Test
  void shouldUpdateLatestIfChangedOnUpdate() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = changedUpdate();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getIsLatest()).isEqualTo(update.isLatest());
  }

  @Test
  void shouldUpdateCategoryTypeIfChanged() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = emptyUpdate();
    int categoryType = 111;

    Document updated = applier.applyUpdates(original, update, categoryType);

    assertThat(updated.getDocumentCategoryType()).isEqualTo(categoryType);
  }

  @Test
  void shouldPassUpdatedDocumentToChangeDetector() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = changedUpdate();
    int categoryType = 222;

    applier.applyUpdates(original, update, categoryType);

    ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
    verify(changeDetector).hasChanged(eq(original), captor.capture());
    var updated = captor.getValue();
    assertThat(updated.getIsLatest()).isEqualTo(update.isLatest());
    assertThat(updated.getAccess()).isEqualTo(update.access());
    assertThat(updated.getCategory()).isEqualTo(update.category());
    assertThat(updated.getDocumentTypeId()).isEqualTo(update.documentTypeId());
    assertThat(updated.getDocumentCategoryType()).isEqualTo(categoryType);
  }

  @Test
  void shouldUpdateModifiedTimestampIfChangeDetected() {
    Document original = buildOriginalDocument();
    DocumentUpdate update = changedUpdate();
    when(changeDetector.hasChanged(eq(original), any(Document.class))).thenReturn(true);
    var now = mockTimeNow();

    Document updated = applier.applyUpdates(original, update, null);

    assertThat(updated.getModifiedTimestamp()).isEqualTo(now);
  }

  private ZonedDateTime mockTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private static Document buildOriginalDocument() {
    return Document.builder()
        .isLatest(false)
        .access(false)
        .category(false)
        .documentType(new DocumentType(0))
        .documentCategoryType(0)
        .build();
  }

  private static DocumentUpdate emptyUpdate() {
    return new DocumentUpdate(null, null, null, null);
  }

  private static DocumentUpdate changedUpdate() {
    return new DocumentUpdate(true, true, 999, true);
  }
}
