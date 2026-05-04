package org.goldstandard.assurance.api.repository;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.DesignCertDateMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignCertDateMatrixRepository
    extends JpaRepository<DesignCertDateMatrix, Integer> {

  default Optional<Integer> findDaysToAdd(Integer requestTypeId, boolean hasAuditAssignment) {
    return findByRequestTypeIdAndVvbAssigned(requestTypeId, hasAuditAssignment)
        .map(DesignCertDateMatrix::getDays);
  }

  Optional<DesignCertDateMatrix> findByRequestTypeIdAndVvbAssigned(
      Integer requestTypeId, boolean vvbAssigned);
}
