package org.goldstandard.assurance.api.repository;

import jakarta.transaction.Transactional;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.RequestDocument;
import org.goldstandard.assurance.api.entity.RequestDocumentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestDocumentRepository
    extends PagingAndSortingRepository<RequestDocument, RequestDocumentId>,
        CrudRepository<RequestDocument, RequestDocumentId> {

  @Modifying
  @Transactional
  @Query(
      nativeQuery = true,
      value =
          "INSERT INTO gs.request_document (request_id, document_id) "
              + "VALUES (:#{#rd.requestId}, :#{#rd.documentId})")
  void add(@Param("rd") RequestDocument rd);

  @Modifying
  @Transactional
  @Query(
      nativeQuery = true,
      value =
          "INSERT INTO gs.request_document (request_id, document_id) "
              + "VALUES (:#{#requestId}, :#{#documentId})")
  void add(@Param("requestId") Integer requestId, @Param("documentId") UUID documentId);

  @Query(
      nativeQuery = true,
      value =
          "SELECT * FROM gs.request_document AS rd WHERE request_id = ?1 AND "
              + "(SELECT COUNT(*) FROM gs.document WHERE id = rd.document_id AND access = ?2) = 1")
  Page<RequestDocument> findAllByRequestIdAndDocumentAccess(
      Integer requestId, boolean access, Pageable pageable);

  @Transactional
  @Modifying
  @Query(
      nativeQuery = true,
      value =
          "DELETE FROM gs.request_document rd WHERE rd.request_id = :#{#requestId} AND rd.document_id = :#{#documentId}")
  void deleteByRequestIdAndDocumentId(
      @Param("requestId") Integer requestId, @Param("documentId") UUID documentId);
}
