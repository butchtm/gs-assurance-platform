package org.goldstandard.assurance.api.repository;

import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupedDocumentPiiViewRepository
    extends PagingAndSortingRepository<GroupedDocumentPiiView, Integer> {

  Page<GroupedDocumentPiiView> findAllByDocumentId(UUID documentId, Pageable pageable);

  List<GroupedDocumentPiiView> getByDocumentId(UUID documentId, Sort sort);
}
