package org.goldstandard.assurance.api.repository.activity;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.activity.Pathway;
import org.goldstandard.assurance.api.entity.activity.PathwayGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathwayGroupRepository extends JpaRepository<PathwayGroup, Integer> {

  Optional<PathwayGroup> findFirstByPathwayOrderByIndexAsc(Pathway pathway);

  Optional<PathwayGroup> findByPathwayAndIndex(Pathway pathway, int pathwayGroupIndex);
}
