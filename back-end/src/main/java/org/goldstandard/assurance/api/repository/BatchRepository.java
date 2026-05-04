package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.Batch;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Integer> {

  default Batch forceFindById(Integer id) {
    return findById(id).orElseThrow(() -> new NotFoundException("Batch not found with id %d", id));
  }
}
