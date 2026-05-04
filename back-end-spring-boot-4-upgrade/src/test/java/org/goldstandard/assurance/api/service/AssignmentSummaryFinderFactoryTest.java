package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignmentSummaryFinderFactoryTest {

  private AssignmentSummaryFinderFactory factory;
  private ArmAssignmentSummaryFinder armFinder;

  @BeforeEach()
  void setup() {
    armFinder = new ArmAssignmentSummaryFinder(null, null);
    factory = new AssignmentSummaryFinderFactory(List.of(armFinder));
  }

  @Test
  void shouldReturnFactoryForGivenType() {
    assertThat(factory.getSummaryFinder(AssignmentSummaryType.ARM)).isEqualTo(armFinder);
  }

  @Test
  void shouldReturnNullForUnrecognisedType() {
    assertThat(factory.getSummaryFinder(null)).isNull();
  }
}
