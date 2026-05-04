package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.goldstandard.assurance.api.entity.DocumentProbablePii;
import org.goldstandard.assurance.api.entity.lookup.DocumentPiiScanStatus;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.goldstandard.assurance.api.fixtures.DocumentPiiAnalysisResultsMother;
import org.goldstandard.assurance.api.repository.DocumentPiiAnalysisRepository;
import org.goldstandard.assurance.api.repository.DocumentProbablePiiRepository;
import org.goldstandard.assurance.api.repository.GroupedDocumentPiiViewRepository;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.goldstandard.assurance.api.service.pii.PiiMetadataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import software.amazon.awssdk.services.comprehend.model.JobStatus;

@ExtendWith(MockitoExtension.class)
class DocumentPiiDbServiceTest {
  @InjectMocks private DocumentPiiDbService service;
  @Mock private DocumentPiiAnalysisRepository analysisRepository;
  @Mock private DocumentProbablePiiRepository probablePiiRepository;
  @Mock private GroupedDocumentPiiViewRepository piiViewRepository;
  @Mock private PiiMetadataFactory metadataFactory;
  @Mock private PiiMetadataRepository metadataRepository;
  @Mock private Clock clock;

  @Captor private ArgumentCaptor<DocumentPiiAnalysis> analysisCaptor;
  @Captor private ArgumentCaptor<List<DocumentProbablePii>> documentPiiCaptor;

  private static final UUID DOCUMENT_ID = UUID.randomUUID();

  @BeforeEach
  void setup() {
    lenient().when(clock.instant()).thenReturn(java.time.Instant.now());
    lenient().when(clock.getZone()).thenReturn(java.time.ZoneId.systemDefault());
  }

  @Test
  void shouldSaveAnalysisRecordWithInProgressStatus() {
    service.saveInProgressAnalysis(DOCUMENT_ID);

    verify(analysisRepository).save(analysisCaptor.capture());
    assertThat(analysisCaptor.getValue().getStatusId())
        .isEqualTo(DocumentPiiScanStatus.IN_PROGRESS);
  }

  @Test
  void shouldUpdateAnalysisWithComprehendJobDetails() {
    var analysis =
        DocumentPiiAnalysis.builder()
            .id(1)
            .documentId(DOCUMENT_ID)
            .statusId(DocumentPiiScanStatus.IN_PROGRESS)
            .build();
    var comprehendJobResult =
        AnalysisStartResult.builder()
            .comprehendJobId("job1")
            .comprehendJobStatus("jobStatus")
            .build();

    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(analysis);

    service.updateWithJobStartResult(DOCUMENT_ID, comprehendJobResult);

    verify(analysisRepository).save(analysisCaptor.capture());
    var updatedAnalysis = analysisCaptor.getValue();
    assertThat(updatedAnalysis.getComprehendJobId())
        .isEqualTo(comprehendJobResult.getComprehendJobId());
    assertThat(updatedAnalysis.getComprehendJobStatus())
        .isEqualTo(comprehendJobResult.getComprehendJobStatus());
  }

  @Test
  void shouldUpdateAnalysisRecordWithFailedStatus() {
    var analysis = new DocumentPiiAnalysis();
    service.updateToFailedStatus(analysis);

    verify(analysisRepository).save(analysisCaptor.capture());
    var updatedAnalysis = analysisCaptor.getValue();
    assertThat(updatedAnalysis.getStatusId()).isEqualTo(DocumentPiiScanStatus.FAILED);
  }

  @Test
  void shouldUpdateAnalysisRecordWithCompletedNoPiiStatus() {
    var analysis = new DocumentPiiAnalysis();

    service.updateToCompletedNoPiiStatus(analysis);

    verify(analysisRepository).save(analysisCaptor.capture());
    var updatedAnalysis = analysisCaptor.getValue();
    assertThat(updatedAnalysis.getStatusId()).isEqualTo(DocumentPiiScanStatus.COMPLETED_NO_PII);
  }

  @Test
  void shouldFetchAndUpdateAnalysisRecordWithCompletedNoPiiStatus() {
    var analysis =
        DocumentPiiAnalysis.builder().startedAt(ZonedDateTime.now(clock).minusMinutes(1)).build();
    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(analysis);
    service.updateToCompletedNoPiiStatus(DOCUMENT_ID);

    verify(analysisRepository).save(analysisCaptor.capture());
    var updatedAnalysis = analysisCaptor.getValue();
    assertThat(updatedAnalysis.getStatusId()).isEqualTo(DocumentPiiScanStatus.COMPLETED_NO_PII);
  }

  @Test
  void shouldUpdateAnalysisStatusToPiiFoundAndSaveProbablePii() {
    var results = DocumentPiiAnalysisResultsMother.build(DOCUMENT_ID);
    var expectedMetaData = mock(PiiMetadata.class);
    var analysis =
        DocumentPiiAnalysis.builder().startedAt(ZonedDateTime.now(clock).minusMinutes(1)).build();
    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(analysis);
    when(metadataFactory.buildForDocument(results)).thenReturn(expectedMetaData);

    service.saveAnalysisResults(results);

    verify(analysisRepository).save(analysisCaptor.capture());
    var updatedAnalysis = analysisCaptor.getValue();
    assertThat(updatedAnalysis.getStatusId()).isEqualTo(DocumentPiiScanStatus.COMPLETED_PII_FOUND);
    verify(probablePiiRepository).saveAll(any());
    verify(metadataFactory).buildForDocument(results);
    verify(metadataRepository).save(expectedMetaData);
  }

  @Test
  void shouldKeepPageNumberWhenSavingPiiForPagedDocument() {
    var results =
        DocumentPiiAnalysisResultsMother.withContentType(
            DOCUMENT_ID, MediaType.APPLICATION_PDF_VALUE);
    var expectedMetaData = mock(PiiMetadata.class);
    var analysis =
        DocumentPiiAnalysis.builder().startedAt(ZonedDateTime.now(clock).minusMinutes(1)).build();
    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(analysis);
    when(metadataFactory.buildForDocument(results)).thenReturn(expectedMetaData);

    service.saveAnalysisResults(results);

    verify(probablePiiRepository).saveAll(documentPiiCaptor.capture());
    assertThat(documentPiiCaptor.getValue().getFirst().getPageNumber()).isEqualTo(1);
  }

  @Test
  void shouldRemovePageNumberWhenSavingPiiForNonPagedDocument() {
    var results =
        DocumentPiiAnalysisResultsMother.withContentType(DOCUMENT_ID, MediaType.IMAGE_PNG_VALUE);
    var expectedMetaData = mock(PiiMetadata.class);
    var analysis =
        DocumentPiiAnalysis.builder().startedAt(ZonedDateTime.now(clock).minusMinutes(1)).build();
    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(analysis);
    when(metadataFactory.buildForDocument(results)).thenReturn(expectedMetaData);

    service.saveAnalysisResults(results);

    verify(probablePiiRepository).saveAll(documentPiiCaptor.capture());
    assertThat(documentPiiCaptor.getValue().getFirst().getPageNumber()).isNull();
  }

  @Test
  void shouldReturnPageOfGroupedProbablePii() {
    var pageable = mock(Pageable.class);
    var expectedPage =
        new PageImpl<>(List.of(mock(GroupedDocumentPiiView.class)), PageRequest.of(0, 1), 1);
    when(piiViewRepository.findAllByDocumentId(DOCUMENT_ID, pageable)).thenReturn(expectedPage);

    assertThat(service.getProbablePiiPage(DOCUMENT_ID, pageable)).isEqualTo(expectedPage);

    verify(piiViewRepository).findAllByDocumentId(DOCUMENT_ID, pageable);
  }

  @Test
  void shouldReturnAllGroupedProbablePii() {
    var expectedList = List.of(mock(GroupedDocumentPiiView.class));
    when(piiViewRepository.getByDocumentId(eq(DOCUMENT_ID), any(Sort.class)))
        .thenReturn(expectedList);

    assertThat(service.getAllProbablePiiSorted(DOCUMENT_ID)).isEqualTo(expectedList);

    verify(piiViewRepository).getByDocumentId(eq(DOCUMENT_ID), any(Sort.class));
  }

  @Test
  void shouldUseRepositoryToFetchPendingComprehendJobsThatHaveNotFailed() {
    var expectedResult = List.of(new DocumentPiiAnalysis());

    when(analysisRepository.getByComprehendJobStatusInAndStatusId(
            List.of(JobStatus.SUBMITTED.toString(), JobStatus.IN_PROGRESS.toString()),
            DocumentPiiScanStatus.IN_PROGRESS))
        .thenReturn(expectedResult);

    var result = service.getPendingComprehendJobs();

    assertThat(result).isEqualTo(expectedResult);

    verify(analysisRepository)
        .getByComprehendJobStatusInAndStatusId(
            List.of(JobStatus.SUBMITTED.toString(), JobStatus.IN_PROGRESS.toString()),
            DocumentPiiScanStatus.IN_PROGRESS);
  }

  @Test
  void shouldUpdateComprehendStatusWhenNewStatusIsDifferent() {
    var analysisRecord =
        DocumentPiiAnalysis.builder()
            .documentId(DOCUMENT_ID)
            .comprehendJobStatus("SUBMITTED")
            .build();

    service.updateAnalysisComprehendStatus(analysisRecord, "COMPLETED");

    verify(analysisRepository).save(analysisCaptor.capture());
    var updatedAnalysis = analysisCaptor.getValue();
    assertThat(updatedAnalysis.getComprehendJobStatus()).isEqualTo("COMPLETED");
  }

  @Test
  void shouldNotUpdateComprehendStatusWhenNewStatusIsTheSame() {
    var analysisRecord =
        DocumentPiiAnalysis.builder()
            .documentId(DOCUMENT_ID)
            .comprehendJobStatus("SUBMITTED")
            .build();

    service.updateAnalysisComprehendStatus(analysisRecord, "SUBMITTED");

    verifyNoInteractions(analysisRepository);
  }

  @Test
  void shouldMarkTextractAsFinishedWithTimingInformation() {
    var startTime = ZonedDateTime.now(clock).minusMinutes(2);
    var finishTime = ZonedDateTime.now(clock);
    long expectedDuration = 120000L;

    var existingAnalysis =
        DocumentPiiAnalysis.builder()
            .id(1)
            .documentId(DOCUMENT_ID)
            .statusId(DocumentPiiScanStatus.IN_PROGRESS)
            .startedAt(startTime)
            .build();

    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(existingAnalysis);
    when(clock.instant()).thenReturn(finishTime.toInstant());
    when(clock.getZone()).thenReturn(finishTime.getZone());

    service.markTextractAsFinished(DOCUMENT_ID);

    verify(analysisRepository).getByDocumentId(DOCUMENT_ID);
    verify(analysisRepository).save(analysisCaptor.capture());

    var savedAnalysis = analysisCaptor.getValue();
    assertThat(savedAnalysis.getId()).isEqualTo(1);
    assertThat(savedAnalysis.getDocumentId()).isEqualTo(DOCUMENT_ID);
    assertThat(savedAnalysis.getStatusId()).isEqualTo(DocumentPiiScanStatus.IN_PROGRESS);
    assertThat(savedAnalysis.getStartedAt()).isEqualTo(startTime);
    assertThat(savedAnalysis.getTextractFinishedAt()).isEqualTo(finishTime);
    assertThat(savedAnalysis.getTextractDurationMs()).isEqualTo(expectedDuration);
  }

  @Test
  void shouldSetComprehendFinishedTimeWhenUpdatingToCompletedStatus() {
    var startTime = ZonedDateTime.now(clock).minusMinutes(5);
    var finishTime = ZonedDateTime.now(clock);
    long expectedDuration = 300000L;

    var existingAnalysis =
        DocumentPiiAnalysis.builder()
            .id(1)
            .documentId(DOCUMENT_ID)
            .statusId(DocumentPiiScanStatus.IN_PROGRESS)
            .startedAt(startTime)
            .build();

    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(existingAnalysis);
    when(clock.instant()).thenReturn(finishTime.toInstant());
    when(clock.getZone()).thenReturn(finishTime.getZone());

    service.updateToCompletedNoPiiStatus(DOCUMENT_ID);

    verify(analysisRepository).save(analysisCaptor.capture());

    var savedAnalysis = analysisCaptor.getValue();
    assertThat(savedAnalysis.getStatusId()).isEqualTo(DocumentPiiScanStatus.COMPLETED_NO_PII);
    assertThat(savedAnalysis.getComprehendFinishedAt()).isEqualTo(finishTime);
    assertThat(savedAnalysis.getComprehendDurationMs()).isEqualTo(expectedDuration);
  }

  @Test
  void shouldCalculateComprehendDurationFromTextractFinishTimeWhenAvailable() {
    var startTime = ZonedDateTime.now(clock).minusMinutes(10);
    var textractFinishTime = ZonedDateTime.now(clock).minusMinutes(3);
    var comprehendFinishTime = ZonedDateTime.now(clock);
    long expectedDuration = 180000L;

    var existingAnalysis =
        DocumentPiiAnalysis.builder()
            .id(1)
            .documentId(DOCUMENT_ID)
            .statusId(DocumentPiiScanStatus.IN_PROGRESS)
            .startedAt(startTime)
            .textractFinishedAt(textractFinishTime)
            .build();

    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(existingAnalysis);
    when(clock.instant()).thenReturn(comprehendFinishTime.toInstant());
    when(clock.getZone()).thenReturn(comprehendFinishTime.getZone());

    service.updateToCompletedNoPiiStatus(DOCUMENT_ID);

    verify(analysisRepository).save(analysisCaptor.capture());

    var savedAnalysis = analysisCaptor.getValue();
    assertThat(savedAnalysis.getComprehendFinishedAt()).isEqualTo(comprehendFinishTime);
    assertThat(savedAnalysis.getComprehendDurationMs()).isEqualTo(expectedDuration);
  }

  @Test
  void shouldCalculateComprehendDurationFromStartTimeWhenTextractFinishTimeNotAvailable() {
    var startTime = ZonedDateTime.now(clock).minusMinutes(7);
    var comprehendFinishTime = ZonedDateTime.now(clock);
    long expectedDuration = 420000L;
    var existingAnalysis =
        DocumentPiiAnalysis.builder()
            .id(1)
            .documentId(DOCUMENT_ID)
            .statusId(DocumentPiiScanStatus.IN_PROGRESS)
            .startedAt(startTime)
            .textractFinishedAt(null)
            .build();

    when(analysisRepository.getByDocumentId(DOCUMENT_ID)).thenReturn(existingAnalysis);
    when(clock.instant()).thenReturn(comprehendFinishTime.toInstant());
    when(clock.getZone()).thenReturn(comprehendFinishTime.getZone());

    service.updateToCompletedNoPiiStatus(DOCUMENT_ID);

    verify(analysisRepository).save(analysisCaptor.capture());

    var savedAnalysis = analysisCaptor.getValue();
    assertThat(savedAnalysis.getComprehendFinishedAt()).isEqualTo(comprehendFinishTime);
    assertThat(savedAnalysis.getComprehendDurationMs()).isEqualTo(expectedDuration);
  }
}
