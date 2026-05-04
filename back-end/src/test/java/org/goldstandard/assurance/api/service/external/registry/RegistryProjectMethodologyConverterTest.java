package org.goldstandard.assurance.api.service.external.registry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.junit.jupiter.api.Test;

class RegistryProjectMethodologyConverterTest {

  private final RegistryProjectMethodologyConverter converter =
      new RegistryProjectMethodologyConverter();

  @Test
  void shouldReturnNullIfMethodologiesIsNull() {
    var project = Project.builder().build();

    var description = converter.toMethodologyDescription(project);

    assertThat(description).isNull();
  }

  @Test
  void shouldReturnNullIfMethodologiesIsEmpty() {
    var project = Project.builder().methodologies(Collections.emptyList()).build();

    var description = converter.toMethodologyDescription(project);

    assertThat(description).isNull();
  }

  @Test
  void shouldReturnFirstMethodologyDescriptionIfPresent() {
    var methodology1 = toMethodology("description-1");
    var methodology2 = toMethodology("description-2");
    var project = Project.builder().methodologies(List.of(methodology1, methodology2)).build();

    var description = converter.toMethodologyDescription(project);

    assertThat(description).isEqualTo(methodology1.getDescription());
  }

  private static Methodology toMethodology(String description) {
    var methodology = new Methodology();
    methodology.setDescription(description);
    return methodology;
  }
}
