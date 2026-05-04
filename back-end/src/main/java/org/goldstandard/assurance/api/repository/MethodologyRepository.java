package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MethodologyRepository
    extends JpaRepository<Methodology, Integer>, JpaSpecificationExecutor<Methodology> {

  default Methodology forceFindById(Integer id) {
    return findById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("Methodology with ID %d not found", id)));
  }
}
