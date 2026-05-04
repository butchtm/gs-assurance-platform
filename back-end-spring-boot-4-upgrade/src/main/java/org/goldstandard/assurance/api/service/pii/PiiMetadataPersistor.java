package org.goldstandard.assurance.api.service.pii;

import static org.goldstandard.assurance.api.entity.lookup.PiiAction.ACCEPTED_ID;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.PiiActionRequestDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.Comment;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.PiiAction;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.repository.PiiMetadataRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PiiMetadataPersistor {

  private final PiiMetadataFinder finder;
  private final PiiMetadataRepository repository;
  private final PiiMetadataFactory factory;

  public void acceptProjectPii(Integer projectId) {
    var records = finder.getAllNonActionedProjectMetadata(projectId);
    var updatedRecords = factory.updateMetadataAction(records, new PiiAction(ACCEPTED_ID));
    updatedRecords.forEach(repository::save);
  }

  public void persistProjectPii(TextAnalysisResultsDTO probablePii, Project project) {
    var existingMetadata = finder.getAllNonActionedProjectMetadata(project.getId());
    var newPiiTypeDetails = probablePii.getDistinctFields();
    existingMetadata.forEach(
        existing -> persist(existing, toPiiAction(existing, newPiiTypeDetails)));
    if (!isEmpty(probablePii.getProbablePii())) {
      factory.buildForProject(probablePii, project).forEach(repository::save);
    }
  }

  public void persistUpdate(PiiActionRequestDTO piiActionRequest) {
    var records = finder.getAssociatedPiiMetadataRecords(piiActionRequest);
    var updatedRecords = factory.updateMetadataAction(records, piiActionRequest.getPiiAction());
    updatedRecords.forEach(repository::save);
  }

  public void persistCommentPii(TextAnalysisResultsDTO dto, Comment comment) {
    if (Objects.nonNull(dto) && !isEmpty(dto.getProbablePii())) {
      var piiMetadata = factory.buildForComment(comment);
      repository.save(piiMetadata);
    }
  }

  public void persist(PiiMetadata metadata, PiiAction action) {
    if (Objects.nonNull(metadata) && Objects.nonNull(action)) {
      repository.save(factory.updateMetadataAction(metadata, action));
    }
  }

  public void markDocumentAsNotified(PiiMetadata metadata) {
    repository.save(metadata.withNotificationSent(true));
  }

  private PiiAction toPiiAction(
      PiiMetadata existingMetadata, Collection<String> newPiiTypeDetails) {
    if (newPiiTypeDetails.contains(existingMetadata.getTypeDetail())) {
      return new PiiAction(PiiAction.OBSOLETE_ID);
    } else {
      return new PiiAction(PiiAction.REMOVED_ID);
    }
  }
}
