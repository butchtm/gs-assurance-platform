package org.goldstandard.assurance.api.repository;

import org.goldstandard.assurance.api.entity.RequestForm;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestFormRepository extends JpaRepository<RequestForm, Integer> {

  default RequestForm forceFindById(Integer formId) {
    return findById(formId)
        .orElseThrow(() -> new NotFoundException("Form with id %d not found", formId));
  }

  boolean existsByRequestIdAndDefinition_Type_Id(Integer id, Integer formTypeId);
}
