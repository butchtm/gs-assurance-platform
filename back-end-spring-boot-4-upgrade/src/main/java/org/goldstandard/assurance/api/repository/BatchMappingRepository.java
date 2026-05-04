package org.goldstandard.assurance.api.repository;

import java.util.List;
import org.goldstandard.assurance.api.entity.BatchMapping;
import org.goldstandard.assurance.api.entity.BatchMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchMappingRepository extends JpaRepository<BatchMapping, BatchMappingId> {
  List<BatchMapping> findAllByRequestTypeIdAndProjectClassificationId(
      Integer requestTypeId, Integer projectClassificationId);
}
