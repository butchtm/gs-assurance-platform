package org.goldstandard.assurance.api.service.finance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FinanceInvoiceType {
  PRELIMINARY_REVIEW_FEES("Preliminary Review Fees"),
  PROJECT_DESIGN_REVIEW_FEES("Project Design Review Fees"),
  PERFORMANCE_REVIEW_FEES("Performance Review Fees"),
  ISSUANCE_FEES("Issuance Fees VERs"),
  DESIGN_CERTIFICATION_RENEWAL_FEE("Design Certification Renewal Fee"),
  DESIGN_CHANGE_REVIEW_FEE("Design Change Review Fee"),
  COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_REVIEW_FEE(
      "Combined Design and Performance Certification");

  private final String label;
}
