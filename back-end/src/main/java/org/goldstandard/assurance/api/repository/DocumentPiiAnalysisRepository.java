package org.goldstandard.assurance.api.repository;

import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentPiiAnalysisRepository
    extends CrudRepository<DocumentPiiAnalysis, Integer> {

  List<DocumentPiiAnalysis> getByComprehendJobStatusInAndStatusId(
      List<String> comprehendJobStatus, Integer statusId);

  DocumentPiiAnalysis getByDocumentId(UUID documentId);
}
