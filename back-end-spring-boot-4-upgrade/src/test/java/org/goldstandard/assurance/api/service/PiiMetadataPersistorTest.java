package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.OBSOLETE_ID;
import static org.goldstandard.assurance.api.entity.lookup.PiiAction.REMOVED_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.PiiActionRequestDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.goldstandard.assurance.api.service.pii.PiiMetadataFactory;
import org.goldstandard.assurance.api.service.pii.PiiMetadataFinder;
import org.goldstandard.assurance.api.service.pii.PiiMetadataPersistor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PiiMetadataPersistorTest {

  @InjectMocks private PiiMetadataPersistor persistor;
  @Mock private PiiMetadataRepository repository;
  @Mock private PiiMetadataFactory factory;
  @Mock private PiiMetadataFinder finder;
  @Captor private ArgumentCaptor<PiiAction> actionCaptor;

  @Test
  void update_ShouldUpdatePiiMetadataRecords() {
    var documentId = UUID.randomUUID();
    var piiAction = new PiiAction(5);
    var piiActionRequest =
        PiiActionRequestDTO.builder()
            .projectId(1)
            .requestId(2)
            .commentId(3)
            .documentId(documentId)
            .piiAction(piiAction)
            .build();

    var existingRecords = List.of(mock(PiiMetadata.class));
    var updatedRecords = List.of(mock(PiiMetadata.class));

    when(finder.getAssociatedPiiMetadataRecords(piiActionRequest)).thenReturn(existingRecords);
    when(factory.updateMetadataAction(existingRecords, piiAction)).thenReturn(updatedRecords);

    persistor.persistUpdate(piiActionRequest);

    verify(finder).getAssociatedPiiMetadataRecords(piiActionRequest);
    verify(factory).updateMetadataAction(existingRecords, piiAction);
    verify(repository).save(updatedRecords.getFirst());
  }

  @Test
  void shouldBuildAndSaveMetadataForComment() {
    var analysisDto =
        TextAnalysisResultsDTO.builder().probablePii(List.of(mock(ProbablePii.class))).build();
    var comment = mock(Comment.class);
    var expectedMetadata = mock(PiiMetadata.class);

    when(factory.buildForComment(comment)).thenReturn(expectedMetadata);

    persistor.persistCommentPii(analysisDto, comment);

    verify(factory).buildForComment(comment);
    verify(repository).save(expectedMetadata);
  }

  @Test
  void shouldNotBuildAndSaveMetadataForCommentWhenAnalysisDtoIsNull() {
    persistor.persistCommentPii(null, mock(Comment.class));

    verifyNoInteractions(factory, repository);
  }

  @Test
  void shouldNotBuildAndSaveMetadataForCommentWhenPiiIsEmpty() {
    var analysisDto = TextAnalysisResultsDTO.builder().probablePii(Collections.emptyList()).build();
    var comment = mock(Comment.class);

    persistor.persistCommentPii(analysisDto, comment);

    verifyNoInteractions(factory, repository);
  }

  @Test
  void shouldUpdateMetadata() {
    var metadata = mock(PiiMetadata.class);
    var action = mock(PiiAction.class);
    var updatedMetadata = mock(PiiMetadata.class);

    when(factory.updateMetadataAction(metadata, action)).thenReturn(updatedMetadata);

    persistor.persist(metadata, action);

    verify(factory).updateMetadataAction(metadata, action);
    verify(repository).save(updatedMetadata);
  }

  @Test
  void shouldNotUpdateMetadataIfMetadataIsNull() {
    persistor.persist(null, mock(PiiAction.class));

    verifyNoInteractions(factory, repository);
  }

  @Test
  void shouldNotUpdateMetadataIfActionIsNull() {
    persistor.persist(mock(PiiMetadata.class), null);

    verifyNoInteractions(factory, repository);
  }

  @Test
  void shouldMarkDocumentAsNotified() {
    var metadata = mock(PiiMetadata.class);
    var updatedMetadata = mock(PiiMetadata.class);

    when(metadata.withNotificationSent(true)).thenReturn(updatedMetadata);

    persistor.markDocumentAsNotified(metadata);

    verify(repository).save(updatedMetadata);
  }

  @Test
  void persistProjectPiiMetadata_WhenNoPiiExistsAndNoNewPii_ShouldDoNothing() {
    var project = mock(Project.class);
    var emptyAnalysis =
        TextAnalysisResultsDTO.builder().probablePii(Collections.emptyList()).build();

    when(project.getId()).thenReturn(1);
    when(finder.getAllNonActionedProjectMetadata(1)).thenReturn(Collections.emptyList());

    persistor.persistProjectPii(emptyAnalysis, project);

    verify(finder).getAllNonActionedProjectMetadata(1);
    verifyNoInteractions(factory);
  }

  @Test
  void persistProjectPiiMetadata_WhenExistingPiiAndNoNewPii_ShouldMarkAllAsRemoved() {
    var project = mock(Project.class);
    var emptyAnalysis =
        TextAnalysisResultsDTO.builder().probablePii(Collections.emptyList()).build();
    var existingMetadata = mock(PiiMetadata.class);
    var updatedMetadata = mock(PiiMetadata.class);

    when(project.getId()).thenReturn(1);
    when(finder.getAllNonActionedProjectMetadata(1)).thenReturn(List.of(existingMetadata));
    when(factory.updateMetadataAction(eq(existingMetadata), any(PiiAction.class)))
        .thenReturn(updatedMetadata);

    persistor.persistProjectPii(emptyAnalysis, project);

    verify(finder).getAllNonActionedProjectMetadata(1);
    verify(factory).updateMetadataAction(eq(existingMetadata), actionCaptor.capture());
    verify(repository).save(updatedMetadata);

    assertThat(actionCaptor.getValue().getId()).isEqualTo(REMOVED_ID);
  }

  @Test
  void persistProjectPiiMetadata_WhenNoPiiExistsAndNewPiiAdded_ShouldCreateNewRecords() {
    var project = mock(Project.class);
    var pii = mock(ProbablePii.class);
    var results = TextAnalysisResultsDTO.builder().probablePii(List.of(pii)).build();
    var newMetadata = mock(PiiMetadata.class);

    when(project.getId()).thenReturn(1);
    when(finder.getAllNonActionedProjectMetadata(1)).thenReturn(Collections.emptyList());
    when(factory.buildForProject(results, project)).thenReturn(List.of(newMetadata));

    persistor.persistProjectPii(results, project);

    verify(finder).getAllNonActionedProjectMetadata(1);
    verify(factory).buildForProject(results, project);
    verify(repository).save(newMetadata);
  }

  @Test
  void persistProjectPiiMetadata_WhenExistingPiiAndNewPii_ShouldUpdateExistingAndCreateNew() {
    var project = mock(Project.class);
    var pii = List.of(ProbablePii.builder().field("existing").build());
    var results = TextAnalysisResultsDTO.builder().probablePii(pii).build();
    var existingMetadata = mock(PiiMetadata.class);
    var updatedMetadata = mock(PiiMetadata.class);
    var newMetadata = mock(PiiMetadata.class);

    when(project.getId()).thenReturn(1);
    when(finder.getAllNonActionedProjectMetadata(1)).thenReturn(List.of(existingMetadata));
    when(factory.updateMetadataAction(eq(existingMetadata), any(PiiAction.class)))
        .thenReturn(updatedMetadata);
    when(factory.buildForProject(results, project)).thenReturn(List.of(newMetadata));

    persistor.persistProjectPii(results, project);

    verify(finder).getAllNonActionedProjectMetadata(1);
    verify(factory).updateMetadataAction(eq(existingMetadata), actionCaptor.capture());
    verify(repository).save(updatedMetadata);
    verify(factory).buildForProject(results, project);
    verify(repository).save(newMetadata);

    assertThat(actionCaptor.getValue().getId()).isEqualTo(REMOVED_ID);
  }

  @Test
  void persistProjectPiiMetadata_WhenFieldsMatch_ShouldMarkExistingAsObsoleteAndCreateNew() {
    var project = mock(Project.class);
    var pii = List.of(ProbablePii.builder().field("existing").build());
    var results = TextAnalysisResultsDTO.builder().probablePii(pii).build();
    var existingMetadata = mock(PiiMetadata.class);
    var updatedMetadata = mock(PiiMetadata.class);
    var newMetadata = mock(PiiMetadata.class);

    when(project.getId()).thenReturn(1);
    when(finder.getAllNonActionedProjectMetadata(1)).thenReturn(List.of(existingMetadata));
    when(existingMetadata.getTypeDetail()).thenReturn("existing");
    when(factory.updateMetadataAction(eq(existingMetadata), any(PiiAction.class)))
        .thenReturn(updatedMetadata);
    when(factory.buildForProject(results, project)).thenReturn(List.of(newMetadata));

    persistor.persistProjectPii(results, project);

    verify(factory).updateMetadataAction(eq(existingMetadata), actionCaptor.capture());
    verify(repository).save(updatedMetadata);
    verify(factory).buildForProject(results, project);
    verify(repository).save(newMetadata);

    assertThat(actionCaptor.getValue().getId()).isEqualTo(OBSOLETE_ID);
  }
}
