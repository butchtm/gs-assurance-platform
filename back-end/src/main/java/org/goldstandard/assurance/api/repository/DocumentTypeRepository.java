package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer> {

  default DocumentType forceFindById(Integer id) {
    return findById(id)
        .orElseThrow(() -> new NotFoundException("document type with id %d not found", id));
  }
}
