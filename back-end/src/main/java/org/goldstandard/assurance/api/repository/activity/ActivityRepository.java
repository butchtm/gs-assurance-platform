package org.goldstandard.assurance.api.repository.activity;

import java.util.Collection;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.PathwayGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

  Optional<Activity> findByPathwayGroupAndIndex(PathwayGroup pathwayGroup, Integer index);

  Optional<Activity> findFirstByPathwayGroupOrderByIndexAsc(PathwayGroup pathwayGroup);

  Collection<Activity> findByPathwayGroupOrderByIndexAsc(PathwayGroup pathwayGroup);
}
