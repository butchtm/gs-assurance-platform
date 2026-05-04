package org.goldstandard.assurance.api.repository;

import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestAccountAssignmentRepository
    extends PagingAndSortingRepository<RequestAccountAssignment, Integer>,
        CrudRepository<RequestAccountAssignment, Integer>,
        JpaSpecificationExecutor<RequestAccountAssignment> {

  Optional<RequestAccountAssignment> findFirstByRequestIdAndAssignmentType_IdOrderByUpdatedTimeDesc(
      Integer requestId, Integer assignmentTypeId);

  @Transactional
  @Modifying
  @Query(
      nativeQuery = true,
      value =
          """
            update gs.request_account_assignment
            set assignment_status_id = :assignment_status_id,
                updated_time = :updated_time
            where id = :assignment_id
            """)
  void updateAssignmentStatus(
      @Param(value = "assignment_id") Integer assignmentId,
      @Param(value = "assignment_status_id") Integer assignmentStatusId,
      @Param(value = "updated_time") ZonedDateTime updatedTime);
}
