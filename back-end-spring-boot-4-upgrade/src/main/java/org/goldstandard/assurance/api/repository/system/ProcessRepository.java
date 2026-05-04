package org.goldstandard.assurance.api.repository.system;

import org.goldstandard.assurance.api.entity.system.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Integer> {

  @Query(
      nativeQuery = true,
      value =
          """
                    SELECT
                      id,
                      process_code,
                      process_status,
                      start_timestamp,
                      status_timestamp,
                      process_message
                    FROM (
                      SELECT
                        id,
                        process_code,
                        process_status,
                        start_timestamp,
                        status_timestamp,
                        process_message,
                        rank() OVER (PARTITION BY process_code order BY status_timestamp DESC) AS ranking
                      FROM
                        gs.process
                    ) AS latest_process
                    WHERE
                      ranking = 1 AND
                      process_code = :processCode
                    """)
  Process findLatestForProcessCode(@Param("processCode") String processCode);
}
