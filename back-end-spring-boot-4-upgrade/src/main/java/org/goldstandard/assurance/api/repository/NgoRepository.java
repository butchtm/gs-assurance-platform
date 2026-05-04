package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.Ngo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NgoRepository extends JpaRepository<Ngo, Integer> {
  // intentionally blank
}
