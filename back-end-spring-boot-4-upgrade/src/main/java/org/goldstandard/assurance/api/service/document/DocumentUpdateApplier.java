package org.goldstandard.assurance.api.service.document;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentUpdateApplier {

  private final DocumentUpdateChangeDetector changeDetector;
  private final Clock clock;

  public Document applyUpdates(Document document, DocumentUpdate update, Integer categoryType) {
    var builder = document.toBuilder();
    Optional.ofNullable(update.isLatest()).ifPresent(builder::isLatest);
    Optional.ofNullable(update.access()).ifPresent(builder::access);
    Optional.ofNullable(update.category()).ifPresent(builder::category);
    Optional.ofNullable(update.documentTypeId())
        .map(DocumentType::new)
        .ifPresent(builder::documentType);
    Optional.ofNullable(categoryType).ifPresent(builder::documentCategoryType);
    if (changeDetector.hasChanged(document, builder.build())) {
      builder.modifiedTimestamp(ZonedDateTime.now(clock));
    }
    return builder.build();
  }
}
