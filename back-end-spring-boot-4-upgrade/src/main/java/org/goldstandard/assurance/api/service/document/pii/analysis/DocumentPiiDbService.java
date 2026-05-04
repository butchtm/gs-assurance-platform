package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.goldstandard.assurance.api.entity.DocumentProbablePii;
import org.goldstandard.assurance.api.entity.lookup.DocumentPiiScanStatus;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.goldstandard.assurance.api.repository.DocumentPiiAnalysisRepository;
import org.goldstandard.assurance.api.repository.DocumentProbablePiiRepository;
import org.goldstandard.assurance.api.repository.GroupedDocumentPiiViewRepository;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.goldstandard.assurance.api.service.pii.PiiMetadataFactory;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.comprehend.model.JobStatus;

@Service
@RequiredArgsConstructor
public class DocumentPiiDbService {

  private final DocumentPiiAnalysisRepository analysisRepository;
  private final DocumentProbablePiiRepository probablePiiRepository;
  private final GroupedDocumentPiiViewRepository piiViewRepository;
  private final PiiMetadataFactory metadataFactory;
  private final PiiMetadataRepository metadataRepository;
  private final Clock clock;

  public DocumentPiiAnalysis saveInProgressAnalysis(UUID documentId) {
    var analysisRecord =
        DocumentPiiAnalysis.builder()
            .documentId(documentId)
            .statusId(DocumentPiiScanStatus.IN_PROGRESS)
            .startedAt(ZonedDateTime.now(clock))
            .build();
    saveAnalysis(analysisRecord);
    return analysisRecord;
  }

  public void updateWithJobStartResult(UUID documentId, AnalysisStartResult startJobResult) {
    var updated = analysisRepository.getByDocumentId(documentId);
    updated =
        updated.toBuilder()
            .comprehendJobId(startJobResult.getComprehendJobId())
            .comprehendJobStatus(startJobResult.getComprehendJobStatus())
            .build();
    saveAnalysis(updated);
  }

  public void updateToFailedStatus(DocumentPiiAnalysis analysis) {
    analysisRepository.save(analysis.withStatusId(DocumentPiiScanStatus.FAILED));
  }

  public void updateToCompletedNoPiiStatus(DocumentPiiAnalysis analysis) {
    analysisRepository.save(analysis.withStatusId(DocumentPiiScanStatus.COMPLETED_NO_PII));
  }

  public void updateToCompletedNoPiiStatus(UUID documentId) {
    updateAnalysisWithStatus(documentId, DocumentPiiScanStatus.COMPLETED_NO_PII);
  }

  public void updateAnalysisComprehendStatus(DocumentPiiAnalysis analysis, String newStatus) {
    if (!analysis.getComprehendJobStatus().equals(newStatus)) {
      analysisRepository.save(analysis.withComprehendJobStatus(newStatus));
    }
  }

  public void saveAnalysisResults(DocumentPiiAnalysisResults results) {
    updateToCompletedPiiFoundStatus(results.getDocument().getId());
    saveProbablePii(results.getProbablePii(), results.getDocument());
    saveMetadata(results);
  }

  public Page<GroupedDocumentPiiView> getProbablePiiPage(UUID documentId, Pageable pageable) {
    return piiViewRepository.findAllByDocumentId(documentId, pageable);
  }

  public List<GroupedDocumentPiiView> getAllProbablePiiSorted(UUID documentId) {
    Sort sort = Sort.by(Sort.Order.asc("pageNumber"), Sort.Order.asc("entity"));
    return piiViewRepository.getByDocumentId(documentId, sort);
  }

  public List<DocumentPiiAnalysis> getPendingComprehendJobs() {
    return analysisRepository.getByComprehendJobStatusInAndStatusId(
        List.of(JobStatus.SUBMITTED.toString(), JobStatus.IN_PROGRESS.toString()),
        DocumentPiiScanStatus.IN_PROGRESS);
  }

  public void markTextractAsFinished(UUID documentId) {
    var analysis = analysisRepository.getByDocumentId(documentId);
    ZonedDateTime now = ZonedDateTime.now(clock);
    var updatedAnalysis =
        analysis.toBuilder()
            .textractFinishedAt(now)
            .textractDurationMs(Duration.between(analysis.getStartedAt(), now).toMillis())
            .build();
    analysisRepository.save(updatedAnalysis);
  }

  private void saveAnalysis(DocumentPiiAnalysis analysis) {
    analysisRepository.save(analysis);
  }

  private void updateToCompletedPiiFoundStatus(UUID documentId) {
    updateAnalysisWithStatus(documentId, DocumentPiiScanStatus.COMPLETED_PII_FOUND);
  }

  private void updateAnalysisWithStatus(UUID documentId, int newStatus) {
    var update = analysisRepository.getByDocumentId(documentId);
    if (DocumentPiiScanStatus.isCompleted(newStatus)) {
      ZonedDateTime now = ZonedDateTime.now(clock);
      update =
          update.toBuilder()
              .comprehendFinishedAt(now)
              .comprehendDurationMs(calculateComprehendDuration(update, now))
              .statusId(newStatus)
              .build();
    }
    saveAnalysis(update);
  }

  private long calculateComprehendDuration(DocumentPiiAnalysis analysis, ZonedDateTime now) {
    if (Objects.nonNull(analysis.getTextractFinishedAt())) {
      return Duration.between(analysis.getTextractFinishedAt(), now).toMillis();
    } else {
      return Duration.between(analysis.getStartedAt(), now).toMillis();
    }
  }

  private void saveProbablePii(Collection<ProbablePii> probablePii, Document document) {
    var documentPii =
        probablePii.stream().map(pii -> toDocumentProbablePii(pii, document.getId())).toList();
    var documentPiiForContentType =
        removePageNumberForNonPagedDocument(documentPii, document.getContentType());
    probablePiiRepository.saveAll(documentPiiForContentType);
  }

  private DocumentProbablePii toDocumentProbablePii(ProbablePii probablePii, UUID documentId) {
    return DocumentProbablePii.builder()
        .documentId(documentId)
        .entity(probablePii.getEntity())
        .pageNumber(probablePii.getPageNumber())
        .probability(probablePii.getProbability())
        .location(probablePii.getLocation())
        .build();
  }

  private List<DocumentProbablePii> removePageNumberForNonPagedDocument(
      List<DocumentProbablePii> documentPii, String contentType) {
    if (!DocumentUtils.isPagedDocument(contentType)) {
      return documentPii.stream().map(pii -> pii.withPageNumber(null)).toList();
    }
    return documentPii;
  }

  private void saveMetadata(DocumentPiiAnalysisResults results) {
    metadataRepository.save(metadataFactory.buildForDocument(results));
  }
}
