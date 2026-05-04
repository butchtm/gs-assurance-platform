package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.RequestMethodologyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestMethodologyRepository
    extends JpaRepository<RequestMethodology, RequestMethodologyId> {

  List<RequestMethodology> getAllByRequestId(Integer requestId);
}
