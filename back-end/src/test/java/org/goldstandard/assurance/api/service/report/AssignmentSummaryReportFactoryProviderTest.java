package org.goldstandard.assurance.api.service.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignmentSummaryReportFactoryProviderTest {

  private AssignmentSummaryReportFactoryProvider provider;
  private ArmAssignmentSummaryReportFactory armReportFactory;

  @BeforeEach()
  void setup() {
    armReportFactory = new ArmAssignmentSummaryReportFactory();
    provider = new AssignmentSummaryReportFactoryProvider(List.of(armReportFactory));
  }

  @Test
  void shouldReturnFactoryForGivenType() {
    assertThat(provider.getReportFactory(AssignmentSummaryType.ARM)).isEqualTo(armReportFactory);
  }

  @Test
  void shouldReturnNullForUnrecognisedType() {
    assertThat(provider.getReportFactory(null)).isNull();
  }
}
