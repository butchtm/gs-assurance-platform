package org.goldstandard.assurance.api.repository.activity;

import java.util.Collection;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.activity.PathwayGroup;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestActivityRepository
    extends JpaRepository<RequestActivity, Integer>,
        PagingAndSortingRepository<RequestActivity, Integer> {

  Optional<RequestActivity>
      findFirstByRequestIdAndActivity_PathwayGroupAndActivity_IndexOrderByStartDateDesc(
          Integer requestId, PathwayGroup pathwayGroup, Integer index);

  Page<RequestActivity> findAllByRequestId(Integer requestId, Pageable pageable);

  Collection<RequestActivity> findAllByRequestId(Integer requestId);
}
