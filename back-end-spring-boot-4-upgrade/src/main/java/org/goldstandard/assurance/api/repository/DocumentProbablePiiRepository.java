package org.goldstandard.assurance.api.repository;

import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.DocumentProbablePii;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentProbablePiiRepository
    extends CrudRepository<DocumentProbablePii, Integer> {

  List<DocumentProbablePii> getByDocumentId(UUID documentId);
}
