package org.goldstandard.assurance.api.repository;

import static org.goldstandard.assurance.api.entity.FormDefinitionStatus.DRAFT_STATUS_ID;
import static org.goldstandard.assurance.api.entity.FormDefinitionStatus.PUBLISHED_STATUS_ID;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormDefinitionRepository extends JpaRepository<FormDefinition, Integer> {

  default double findLatestPublishedVersionNumber(Integer formTypeId) {
    return findLatestPublishedVersion(formTypeId).map(FormDefinition::getVersion).orElse(0.0);
  }

  default FormDefinition forceFindLatestPublishedVersion(Integer formTypeId) {
    return findLatestPublishedVersion(formTypeId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Published form definition not found for form type %d", formTypeId));
  }

  default Optional<FormDefinition> findLatestPublishedVersion(Integer formTypeId) {
    return findTopByTypeIdAndStatusIdOrderByVersionDesc(formTypeId, PUBLISHED_STATUS_ID).stream()
        .findFirst();
  }

  Optional<FormDefinition> findTopByTypeIdAndStatusIdOrderByVersionDesc(
      Integer formTypeId, Integer statusId);

  default FormDefinition forceFindById(Integer id) {
    return findById(id)
        .orElseThrow(() -> new NotFoundException("Form definition not found with id %d", id));
  }

  default boolean draftExistsByTypeId(Integer typeId) {
    return existsByTypeIdAndStatusId(typeId, DRAFT_STATUS_ID);
  }

  boolean existsByTypeIdAndStatusId(Integer formTypeId, int draftStatusId);
}
