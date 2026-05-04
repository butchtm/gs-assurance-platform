package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.ACCEPTED_ID;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.DOCUMENT_REMOVED_ID;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.NEW_STATUS_ID;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.goldstandard.assurance.api.service.pii.PiiMetadataFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PiiMetadataFinderTest {

  @InjectMocks PiiMetadataFinder finder;
  @Mock PiiMetadataRepository repository;

  @Test
  void findNonNotifiedDocumentMetadata_ShouldReturnNonNotifiedMetadata() {
    var requestId = 1;
    PiiAction action = new PiiAction(DOCUMENT_REMOVED_ID);
    var metadataList = List.of(buildMetadata(false, action), buildMetadata(false, action));
    when(repository.findByRequestIdAndType(requestId, PiiType.DOCUMENT)).thenReturn(metadataList);
    var result = finder.findNonNotifiedDocumentMetadata(requestId);
    assertThat(result).hasSize(2);
  }

  @Test
  void findNonNotifiedDocumentMetadata_ShouldReturnNoResultsDueToNotifiedMetadata() {
    var requestId = 1;
    PiiAction action = new PiiAction(DOCUMENT_REMOVED_ID);
    var metadataList = List.of(buildMetadata(true, action), buildMetadata(true, action));
    when(repository.findByRequestIdAndType(requestId, PiiType.DOCUMENT)).thenReturn(metadataList);
    var result = finder.findNonNotifiedDocumentMetadata(requestId);
    assertThat(result).isEmpty();
  }

  @Test
  void findNonNotifiedDocumentMetadata_ShouldReturnNoResultsDueToActionedMetadata() {
    var requestId = 1;
    PiiAction action = new PiiAction(ACCEPTED_ID);
    var metadataList = List.of(buildMetadata(false, action), buildMetadata(false, action));
    when(repository.findByRequestIdAndType(requestId, PiiType.DOCUMENT)).thenReturn(metadataList);
    var result = finder.findNonNotifiedDocumentMetadata(requestId);
    assertThat(result).isEmpty();
  }

  @Test
  void findNonNotifiedDocumentMetadata_ShouldReturnEmptyListWhenNoMetadata() {
    var requestId = 1;
    when(repository.findByRequestIdAndType(requestId, PiiType.DOCUMENT))
        .thenReturn(Collections.emptyList());
    var result = finder.findNonNotifiedDocumentMetadata(requestId);
    assertThat(result).isEmpty();
  }

  @Test
  void findNonActionedDetailsForFieldsByProjectId() {
    var titleMetadata =
        PiiMetadata.builder()
            .action(new PiiAction(NEW_STATUS_ID))
            .type(PiiType.PROJECT)
            .typeDetail("title")
            .build();
    var descriptionMetadata =
        PiiMetadata.builder()
            .action(new PiiAction(NEW_STATUS_ID))
            .type(PiiType.PROJECT)
            .typeDetail("description")
            .build();
    var notesMetadata =
        PiiMetadata.builder()
            .action(new PiiAction(NEW_STATUS_ID))
            .type(PiiType.PROJECT)
            .typeDetail("notes")
            .build();
    when(repository.findByProjectIdAndTypeAndActionedFalse(1, PiiType.PROJECT))
        .thenReturn(Arrays.asList(titleMetadata, descriptionMetadata, notesMetadata));
    var result = finder.getNonActionedDetailsForFieldsByProjectId(1, List.of("title"));
    assertThat(result).hasSize(1);
  }

  private static PiiMetadata buildMetadata(boolean notified, PiiAction action) {
    return PiiMetadata.builder().notificationSent(notified).action(action).build();
  }
}
