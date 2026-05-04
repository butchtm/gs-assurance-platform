package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.lookup.RequestStatusAccountType;
import org.goldstandard.assurance.api.entity.lookup.RequestStatusAccountTypeId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestStatusAccountTypeRepository
    extends CrudRepository<RequestStatusAccountType, RequestStatusAccountTypeId> {

  @Query
  List<RequestStatusAccountType> findAllByRequestStatusId(Integer requestStatusId);
}
