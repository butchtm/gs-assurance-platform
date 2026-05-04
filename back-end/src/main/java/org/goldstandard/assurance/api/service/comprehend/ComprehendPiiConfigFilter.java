package org.goldstandard.assurance.api.service.comprehend;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.configuration.ComprehendConfigProperties;
import org.goldstandard.assurance.api.filter.PiiEntityScoreThresholdPredicate;
import org.goldstandard.assurance.api.filter.PiiEntityTypesPredicate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@Component
@RequiredArgsConstructor
public class ComprehendPiiConfigFilter {

  private final ComprehendConfigProperties properties;

  public List<PiiEntity> filterByConfigProperties(List<PiiEntity> entities) {
    return entities.stream()
        .filter(new PiiEntityTypesPredicate(properties.getTypesToIgnore()))
        .filter(
            new PiiEntityScoreThresholdPredicate(
                properties.getDefaultScoreThreshold(), properties.getScoreThresholdOverrides()))
        .toList();
  }
}
