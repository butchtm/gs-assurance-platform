package org.goldstandard.assurance.api.repository;

import java.util.Collection;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.RequestTypeDocumentType;
import org.goldstandard.assurance.api.entity.RequestTypeDocumentTypeId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTypeDocumentTypeRepository
    extends CrudRepository<RequestTypeDocumentType, RequestTypeDocumentTypeId> {

  Collection<RequestTypeDocumentType> findAllByRequestTypeIdAndDocumentDisplayCategoryId(
      Integer requestTypeId, Integer documentDisplayCategoryId);

  Optional<RequestTypeDocumentType> findByRequestTypeIdAndDocumentTypeId(
      Integer requestTypeId, Integer documentTypeId);
}
