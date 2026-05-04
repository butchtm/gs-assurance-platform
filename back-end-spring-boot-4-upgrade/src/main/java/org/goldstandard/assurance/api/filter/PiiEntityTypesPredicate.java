package org.goldstandard.assurance.api.filter;

import java.util.Collection;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@RequiredArgsConstructor
public class PiiEntityTypesPredicate implements Predicate<PiiEntity> {

  private final Collection<String> typesToIgnore;

  @Override
  public boolean test(PiiEntity entity) {
    return !typesToIgnore.stream()
        .map(String::toLowerCase)
        .toList()
        .contains(entity.typeAsString().toLowerCase());
  }
}
