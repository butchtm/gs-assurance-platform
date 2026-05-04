package org.goldstandard.assurance.api.service.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_REVIEW_FEE;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.DESIGN_CERTIFICATION_RENEWAL_FEE;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.DESIGN_CHANGE_REVIEW_FEE;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.ISSUANCE_FEES;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PERFORMANCE_REVIEW_FEES;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PRELIMINARY_REVIEW_FEES;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PROJECT_DESIGN_REVIEW_FEES;

import java.util.stream.Stream;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.fixtures.RequestTypeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FinanceInvoiceTypeMapperTest {

  private final FinanceInvoiceTypeMapper mapper = new FinanceInvoiceTypeMapper();

  @ParameterizedTest
  @MethodSource("assuranceRequestTypeAndFinanceInvoiceType")
  void shouldMapAssuranceRequestTypeCodeToFinanceInvoiceType(
      String assuranceTypeCode, FinanceInvoiceType expectedInvoiceType) {
    var requestType = RequestTypeMother.withCode(assuranceTypeCode);
    var request = RequestMother.builder().requestType(requestType).build();

    var invoiceType = mapper.toInvoiceType(request);

    assertThat(invoiceType).contains(expectedInvoiceType);
  }

  @Test
  void shouldReturnEmptyForNonMappedAssuranceCodes() {
    var requestType = RequestTypeMother.withCode("GENERIC");
    var request = RequestMother.builder().requestType(requestType).build();

    var invoiceType = mapper.toInvoiceType(request);

    assertThat(invoiceType).isEmpty();
  }

  private static Stream<Arguments> assuranceRequestTypeAndFinanceInvoiceType() {
    return Stream.of(
        Arguments.of("PRELIMINARY_REVIEW_REGULAR_CASE_VPA", PRELIMINARY_REVIEW_FEES),
        Arguments.of("PRELIMINARY_REVIEW_REAL_OR_REGULAR_CASE_POA", PRELIMINARY_REVIEW_FEES),
        Arguments.of("PRELIMINARY_REVIEW_TRANSITION", PRELIMINARY_REVIEW_FEES),
        Arguments.of("PRELIMINARY_REVIEW", PRELIMINARY_REVIEW_FEES),
        Arguments.of("PRELIMINARY_REVIEW_REGULAR_CASE_VPA", PRELIMINARY_REVIEW_FEES),
        Arguments.of("DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA", PROJECT_DESIGN_REVIEW_FEES),
        Arguments.of("DESIGN_CERTIFICATION_PROJECT", PROJECT_DESIGN_REVIEW_FEES),
        Arguments.of("DESIGN_CERTIFICATION_REAL_CASE_VPA", PROJECT_DESIGN_REVIEW_FEES),
        Arguments.of("DESIGN_REVIEW", PROJECT_DESIGN_REVIEW_FEES),
        Arguments.of("INCLUSION_REGULAR_VPA", PROJECT_DESIGN_REVIEW_FEES),
        Arguments.of("PERFORMANCE_CERTIFICATION_|_PROJECT", PERFORMANCE_REVIEW_FEES),
        Arguments.of("PERFORMANCE_CERTIFICATION_|_VPA", PERFORMANCE_REVIEW_FEES),
        Arguments.of("PERFORMANCE_REVIEW", PERFORMANCE_REVIEW_FEES),
        Arguments.of("ISSUANCE", ISSUANCE_FEES),
        Arguments.of("DESIGN_CERTIFICATION_RENEWAL", DESIGN_CERTIFICATION_RENEWAL_FEE),
        Arguments.of("DESIGN_CERTIFICATION_RENEWAL_|_PROJECT", DESIGN_CERTIFICATION_RENEWAL_FEE),
        Arguments.of(
            "DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA",
            DESIGN_CERTIFICATION_RENEWAL_FEE),
        Arguments.of("RENEWAL_|_REAL_CASE_VPA", DESIGN_CERTIFICATION_RENEWAL_FEE),
        Arguments.of("RENEWAL_|_REGULAR_VPA", DESIGN_CERTIFICATION_RENEWAL_FEE),
        Arguments.of("DESIGN_CHANGE_REVIEW", DESIGN_CHANGE_REVIEW_FEE),
        Arguments.of("DESIGN_CHANGE_|_WITH_VVB", DESIGN_CHANGE_REVIEW_FEE),
        Arguments.of(
            "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT",
            COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_REVIEW_FEE),
        Arguments.of(
            "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA",
            COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_REVIEW_FEE),
        Arguments.of(
            "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA",
            COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_REVIEW_FEE));
  }
}
