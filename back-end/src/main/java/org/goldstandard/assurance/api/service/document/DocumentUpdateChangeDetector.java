package org.goldstandard.assurance.api.service.document;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentUpdateChangeDetector {

  public boolean hasChanged(Document original, Document updated) {
    return Stream.of(
            hasChanged(original, updated, Document::getIsLatest),
            hasChanged(original, updated, Document::getAccess),
            hasChanged(original, updated, Document::getCategory),
            hasChanged(original, updated, Document::getDocumentType),
            hasChanged(original, updated, Document::getDocumentCategoryType))
        .anyMatch(e -> e);
  }

  private boolean hasChanged(
      Document original, Document updated, Function<Document, Object> toField) {
    return !Objects.equals(toField.apply(original), toField.apply(updated));
  }
}
