package org.goldstandard.assurance.api.service.document;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class DocumentAuditInfoFactory {

  private static final String PROJECT_ID = "projectId";
  private static final String REQUEST_ID = "requestId";
  private static final String DOCUMENT_ID = "documentId";

  public Map<String, Object> build(int projectId, int requestId, UUID documentId) {
    return Map.of(
        PROJECT_ID, projectId,
        REQUEST_ID, requestId,
        DOCUMENT_ID, documentId.toString());
  }

  public Map<String, Object> build(
      int projectId, int requestId, UUID documentId, Collection<UUID> otherDocumentIds) {
    Map<String, Object> info = new HashMap<>(build(projectId, requestId, documentId));
    if (CollectionUtils.isEmpty(otherDocumentIds)) {
      return Collections.unmodifiableMap(info);
    }
    info.put("otherDocumentIds", toString(otherDocumentIds));
    return Collections.unmodifiableMap(info);
  }

  private String toString(Collection<UUID> ids) {
    return ids.stream().map(UUID::toString).collect(Collectors.joining(","));
  }
}
