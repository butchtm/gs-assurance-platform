package org.goldstandard.assurance.api.service.pii;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.PiiActionRequestDTO;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PiiMetadataFinder {

  private final PiiMetadataRepository repository;

  public List<PiiMetadata> findNonNotifiedDocumentMetadata(Integer requestId) {
    return repository.findByRequestIdAndType(requestId, PiiType.DOCUMENT).stream()
        .filter(PiiMetadata::shouldSendNotification)
        .toList();
  }

  public Collection<String> getNonActionedDetailsByProjectId(Integer projectId) {
    return getAllNonActionedProjectMetadata(projectId).stream()
        .map(PiiMetadata::getTypeDetail)
        .toList();
  }

  public Collection<PiiMetadata> getNonActionedDetailsForFieldsByProjectId(
      Integer projectId, Collection<String> projectFields) {
    return getAllNonActionedProjectMetadata(projectId).stream()
        .filter(metadata -> projectFields.contains(metadata.getTypeDetail()))
        .toList();
  }

  public List<PiiMetadata> getAllNonActionedProjectMetadata(Integer projectId) {
    return repository.findByProjectIdAndTypeAndActionedFalse(projectId, PiiType.PROJECT);
  }

  public List<PiiMetadata> getAssociatedPiiMetadataRecords(PiiActionRequestDTO piiActionRequest) {
    return repository.findByProjectIdAndRequestIdAndDocumentIdAndCommentId(
        piiActionRequest.getProjectId(),
        piiActionRequest.getRequestId(),
        piiActionRequest.getDocumentId(),
        piiActionRequest.getCommentId());
  }
}
