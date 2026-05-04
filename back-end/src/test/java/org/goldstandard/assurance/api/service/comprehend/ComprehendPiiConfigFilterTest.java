package org.goldstandard.assurance.api.service.comprehend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.configuration.ComprehendConfigProperties;
import org.goldstandard.assurance.api.fixtures.PiiEntityMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.comprehend.model.PiiEntity;

@ExtendWith(MockitoExtension.class)
class ComprehendPiiConfigFilterTest {
  @InjectMocks ComprehendPiiConfigFilter filter;
  @Mock ComprehendConfigProperties properties;

  @Test
  void shouldNotRemovePiiWhenDefaultThresholdMet() {
    var defaultThreshold = 0.80f;
    float entityScore = defaultThreshold + 0.10f;
    PiiEntity piiEntity = PiiEntityMother.build(entityScore);
    when(properties.getDefaultScoreThreshold()).thenReturn(defaultThreshold);

    var result = filter.filterByConfigProperties(List.of(piiEntity));

    assertThat(result).hasSize(1).contains((piiEntity));
  }

  @Test
  void shouldRemovePiiWhenDefaultThresholdNotMet() {
    var defaultThreshold = 0.80f;
    float entityScore = defaultThreshold - 0.10f;
    PiiEntity piiEntity = PiiEntityMother.build(entityScore);
    when(properties.getDefaultScoreThreshold()).thenReturn(defaultThreshold);

    var result = filter.filterByConfigProperties(List.of(piiEntity));

    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnProbablePiiWhenTypeShouldNotBeIgnored() {
    var defaultThreshold = 0.80f;
    PiiEntity piiEntity = PiiEntityMother.build(defaultThreshold);
    when(properties.getDefaultScoreThreshold()).thenReturn(defaultThreshold);
    when(properties.getTypesToIgnore()).thenReturn(Collections.emptyList());

    var result = filter.filterByConfigProperties(List.of(piiEntity));

    assertThat(result).hasSize(1).contains((piiEntity));
  }

  @Test
  void shouldNotReturnProbablePiiWhenTypeShouldBeIgnored() {
    var defaultThreshold = 0.80f;
    PiiEntity piiEntity = PiiEntityMother.build(defaultThreshold);
    when(properties.getDefaultScoreThreshold()).thenReturn(defaultThreshold);
    when(properties.getTypesToIgnore()).thenReturn(List.of(piiEntity.typeAsString()));

    var result = filter.filterByConfigProperties(List.of(piiEntity));

    assertThat(result).isEmpty();
  }
}
