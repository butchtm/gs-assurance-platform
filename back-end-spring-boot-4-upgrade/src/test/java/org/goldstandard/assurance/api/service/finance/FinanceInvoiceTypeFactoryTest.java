package org.goldstandard.assurance.api.service.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.finance.FinanceInvoiceType.PRELIMINARY_REVIEW_FEES;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinanceInvoiceTypeFactoryTest {

  @InjectMocks FinanceInvoiceTypeFactory factory;

  @Mock FinanceInvoiceTypeConverter mapper;

  @Mock FinanceInvoiceTypeDiscountApplier discountApplier;

  @Test
  void shouldMapRequestToInvoiceTypeAndApplyDiscount() {
    var request = RequestMother.build();
    var baseInvoiceType = PRELIMINARY_REVIEW_FEES;
    when(mapper.toInvoiceType(request)).thenReturn(Optional.of(baseInvoiceType));
    var discountedInvoiceType = "Preliminary Review Fees Microscale";
    when(discountApplier.applyDiscountsIfRequired(request, baseInvoiceType))
        .thenReturn(discountedInvoiceType);

    var invoiceType = factory.toInvoiceType(request);

    assertThat(invoiceType).contains(discountedInvoiceType);
  }

  @Test
  void shouldReturnEmptyIfInvoiceTypeIsNotMapped() {
    var request = RequestMother.build();
    when(mapper.toInvoiceType(request)).thenReturn(Optional.empty());

    var invoiceType = factory.toInvoiceType(request);

    assertThat(invoiceType).isEmpty();
    verifyNoInteractions(discountApplier);
  }
}
