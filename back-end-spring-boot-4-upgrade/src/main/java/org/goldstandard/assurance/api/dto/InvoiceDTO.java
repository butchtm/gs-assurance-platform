package org.goldstandard.assurance.api.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class InvoiceDTO {
  private final Integer id;
  private final Integer invoiceStatusId;
  private final ZonedDateTime statusChangedAt;
  private final BigDecimal amount;
}
