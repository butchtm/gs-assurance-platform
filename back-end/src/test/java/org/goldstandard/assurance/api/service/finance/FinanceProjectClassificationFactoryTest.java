package org.goldstandard.assurance.api.service.finance;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.fixtures.ProjectClassificationMother;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FinanceProjectClassificationFactoryTest {

  private final FinanceProjectClassificationFactory factory =
      new FinanceProjectClassificationFactory();

  @ParameterizedTest
  @CsvSource({
    "REGULAR_CASE_VPA,REGULAR_VPA",
    "REGULAR_VPA,REGULAR_VPA",
    "REGULAR_VPA_CROSS_LINKAGE,REGULAR_VPA",
    "REAL_CASE_VPA_LEGACY,REAL_CASE_VPA",
    "REAL_CASE_VPA,REAL_CASE_VPA",
    "REAL_CASE_VPA_CROSS_LINKAGE,REAL_CASE_VPA",
    "POA,PROGRAMME_OF_ACTIVITIES",
    "REAL_OR_REGULAR_CASE_POA,PROGRAMME_OF_ACTIVITIES",
    "PROGRAMME_OF_ACTIVITIES,PROGRAMME_OF_ACTIVITIES",
    "STANDALONE_PROJECT,STANDALONE_PROJECT",
    "NONE,NONE",
    "VPA_CPA,VPA_CPA",
    "ANY_OTHER,ANY_OTHER"
  })
  void shouldMapAssuranceClassificationCodeToFinanceClassificationCode(
      String assuranceCode, String expectedFinanceCode) {
    var classification = ProjectClassificationMother.withCode(assuranceCode);

    var financeCode = factory.toFinanceProjectClassificationCode(classification);

    assertThat(financeCode).contains(expectedFinanceCode);
  }
}
