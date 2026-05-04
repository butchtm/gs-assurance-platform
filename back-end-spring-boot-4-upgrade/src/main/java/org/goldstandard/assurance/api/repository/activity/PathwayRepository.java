package org.goldstandard.assurance.api.repository.activity;

import org.goldstandard.assurance.api.entity.activity.Pathway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathwayRepository extends JpaRepository<Pathway, Integer> {

  Pathway findByIndex(int index);
}
