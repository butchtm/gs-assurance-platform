package org.goldstandard.assurance.api.service.finance;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class FinanceInvoiceTypeFactory {

  private final FinanceInvoiceTypeMapper mapper;
  private final FinanceInvoiceTypeDiscountApplier discountApplier;

  public Optional<String> toInvoiceType(Request request) {
    return mapper
        .toInvoiceType(request)
        .map(invoiceType -> discountApplier.applyDiscountsIfRequired(request, invoiceType));
  }
}
