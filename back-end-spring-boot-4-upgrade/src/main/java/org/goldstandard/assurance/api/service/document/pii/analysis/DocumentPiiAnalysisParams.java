package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class DocumentPiiAnalysisParams {
  private final String bucketName;
  private final String s3Key;
  private final String documentName;
  private final UUID documentId;
  private final String contentType;
}
