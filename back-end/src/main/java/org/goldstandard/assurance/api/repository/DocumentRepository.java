package org.goldstandard.assurance.api.repository;

import jakarta.transaction.Transactional;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository
    extends PagingAndSortingRepository<Document, UUID>, CrudRepository<Document, UUID> {

  default Document forceFindById(UUID id) {
    return findById(id)
        .orElseThrow(() -> new NotFoundException("Document with id %s not found", id));
  }

  boolean existsByFileNameAndContainerName(String fileName, String containerName);

  @Modifying
  @Transactional
  @Query(
      nativeQuery = true,
      value = "INSERT INTO gs.base_project_document (project_id, document_id) VALUES (?1, ?2)")
  void saveBaseProjectDocument(Integer projectId, UUID documentId);

  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = "DELETE FROM gs.base_project_document")
  void deleteAllBaseProjectDocument();
}
