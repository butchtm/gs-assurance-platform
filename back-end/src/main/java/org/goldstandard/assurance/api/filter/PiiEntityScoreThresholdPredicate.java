package org.goldstandard.assurance.api.filter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@RequiredArgsConstructor
public class PiiEntityScoreThresholdPredicate implements Predicate<PiiEntity> {

  private final Float defaultScoreThreshold;
  private final Map<String, Float> scoreThresholdOverrides;

  @Override
  public boolean test(PiiEntity entity) {
    var entityType = entity.typeAsString().toLowerCase();
    if (Objects.nonNull(scoreThresholdOverrides)
        && toLowerCaseOverrideKeys().containsKey(entityType)) {
      var overrideThreshold = toLowerCaseOverrideKeys().get(entityType);
      return entity.score() >= overrideThreshold;
    }
    return entity.score() >= defaultScoreThreshold;
  }

  private Map<String, Float> toLowerCaseOverrideKeys() {
    return scoreThresholdOverrides.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue));
  }
}
