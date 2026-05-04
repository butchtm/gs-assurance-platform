package org.goldstandard.assurance.api.repository;

import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.pii.PiiMetadata;
import org.goldstandard.assurance.api.enumeration.PiiType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiiMetadataRepository extends JpaRepository<PiiMetadata, Integer> {

  List<PiiMetadata> findByProjectIdAndRequestIdAndDocumentIdAndCommentId(
      Integer projectId, Integer requestId, UUID documentId, Integer commentId);

  List<PiiMetadata> findByRequestIdAndType(Integer requestId, PiiType type);

  List<PiiMetadata> findByProjectIdAndTypeAndActionedFalse(Integer projectId, PiiType type);
}
