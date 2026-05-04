package org.goldstandard.assurance.api.service.finance;

import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_REVIEW_FEE;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.DESIGN_CERTIFICATION_RENEWAL_FEE;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.DESIGN_CHANGE_REVIEW_FEE;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.ISSUANCE_FEES;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PERFORMANCE_REVIEW_FEES;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PRELIMINARY_REVIEW_FEES;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PROJECT_DESIGN_REVIEW_FEES;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class FinanceInvoiceTypeMapper {

  public Optional<FinanceInvoiceType> toInvoiceType(Request request) {
    var requestType = request.getRequestType();
    var invoiceType = toInvoiceType(requestType);
    log.debug("mapped request type {} to invoice type {}", requestType.getCode(), invoiceType);
    return invoiceType;
  }

  private Optional<FinanceInvoiceType> toInvoiceType(RequestType requestType) {
    var code = requestType.getCode();
    return switch (code) {
      case "PRELIMINARY_REVIEW_REGULAR_CASE_VPA",
          "PRELIMINARY_REVIEW_REAL_OR_REGULAR_CASE_POA",
          "PRELIMINARY_REVIEW_TRANSITION",
          "PRELIMINARY_REVIEW" ->
          Optional.of(PRELIMINARY_REVIEW_FEES);
      case "DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA",
          "DESIGN_CERTIFICATION_PROJECT",
          "DESIGN_CERTIFICATION_REAL_CASE_VPA",
          "DESIGN_REVIEW",
          "INCLUSION_REGULAR_VPA" ->
          Optional.of(PROJECT_DESIGN_REVIEW_FEES);
      case "PERFORMANCE_CERTIFICATION_|_PROJECT",
          "PERFORMANCE_CERTIFICATION_|_VPA",
          "PERFORMANCE_REVIEW" ->
          Optional.of(PERFORMANCE_REVIEW_FEES);
      case "ISSUANCE" -> Optional.of(ISSUANCE_FEES);
      case "DESIGN_CERTIFICATION_RENEWAL",
          "DESIGN_CERTIFICATION_RENEWAL_|_PROJECT",
          "DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA",
          "RENEWAL_|_REAL_CASE_VPA",
          "RENEWAL_|_REGULAR_VPA" ->
          Optional.of(DESIGN_CERTIFICATION_RENEWAL_FEE);
      case "DESIGN_CHANGE_REVIEW", "DESIGN_CHANGE_|_WITH_VVB" ->
          Optional.of(DESIGN_CHANGE_REVIEW_FEE);
      case "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT",
          "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA",
          "COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA" ->
          Optional.of(COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_REVIEW_FEE);
      default -> handleNotConfigured(code);
    };
  }

  private Optional<FinanceInvoiceType> handleNotConfigured(String code) {
    log.info("no finance item type mapping configured for request type code {}", code);
    return Optional.empty();
  }
}
