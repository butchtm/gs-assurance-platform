package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegacyBatchRequestRepository extends CrudRepository<Project, Integer> {

  @Query(
      nativeQuery = true,
      value =
          """
                    SELECT bpr.project_id
                    FROM gs.base_project_request bpr
                    WHERE bpr.request_id = ?1
                    """)
  List<Integer> findAllProjectIdsInLegacyBatch(Integer requestId);

  @Query(
      nativeQuery = true,
      value =
          """
                    SELECT *
                    FROM gs.project p
                    LEFT JOIN gs.base_project_request bpr
                    ON p.id = bpr.project_id
                    WHERE bpr.request_id = ?1
                    """)
  List<Project> findAllProjectsInLegacyBatch(Integer requestId);
}
