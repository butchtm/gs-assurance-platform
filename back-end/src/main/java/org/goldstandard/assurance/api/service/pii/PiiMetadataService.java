package org.goldstandard.assurance.api.service.pii;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.PiiActionRequestDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PiiMetadataService {

  private final PiiMetadataFinder finder;
  private final PiiMetadataPersistor persistor;

  public void acceptProjectPii(Integer projectId) {
    persistor.acceptProjectPii(projectId);
  }

  public void persistProjectPiiMetadata(TextAnalysisResultsDTO probablePii, Project project) {
    persistor.persistProjectPii(probablePii, project);
  }

  public void update(PiiActionRequestDTO piiActionRequest) {
    persistor.persistUpdate(piiActionRequest);
  }

  public void updateMetadata(PiiMetadata metadata, PiiAction action) {
    persistor.persist(metadata, action);
  }

  public void markDocumentAsNotified(PiiMetadata metadata) {
    persistor.markDocumentAsNotified(metadata);
  }

  public Collection<String> getNonActionedDetailsByProjectId(Integer projectId) {
    return finder.getNonActionedDetailsByProjectId(projectId);
  }

  public List<PiiMetadata> findNonNotifiedDocumentMetadata(Integer requestId) {
    return finder.findNonNotifiedDocumentMetadata(requestId);
  }

  public void updateProjectFieldsToNotFound(Integer projectId, Collection<String> projectFields) {
    var fieldsMetadata = finder.getNonActionedDetailsForFieldsByProjectId(projectId, projectFields);
    fieldsMetadata.forEach(
        piiMetadata -> updateMetadata(piiMetadata, new PiiAction(PiiAction.NO_PII_FOUND)));
  }
}
