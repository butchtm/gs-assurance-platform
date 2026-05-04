package org.goldstandard.assurance.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentViewId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorisedRequestDocumentViewRepository
    extends PagingAndSortingRepository<
            CategorisedRequestDocumentView, CategorisedRequestDocumentViewId>,
        CrudRepository<CategorisedRequestDocumentView, CategorisedRequestDocumentViewId>,
        JpaSpecificationExecutor<CategorisedRequestDocumentView> {

  Page<CategorisedRequestDocumentView> findByRequestId(Integer requestId, Pageable pageable);

  List<CategorisedRequestDocumentView> findAllByRequestId(Integer requestId);

  Optional<CategorisedRequestDocumentView> findOneByDocumentId(UUID documentId);

  default List<Document> findAllPublicRequestDocumentsWithType(
      Integer requestId, Integer documentTypeId) {
    return findAllByRequestIdAndDocument_AccessTrueAndDocument_DocumentType_Id(
            requestId, documentTypeId)
        .stream()
        .map(CategorisedRequestDocumentView::getDocument)
        .toList();
  }

  List<CategorisedRequestDocumentView>
      findAllByRequestIdAndDocument_AccessTrueAndDocument_DocumentType_Id(
          Integer requestId, Integer documentTypeId);
}
