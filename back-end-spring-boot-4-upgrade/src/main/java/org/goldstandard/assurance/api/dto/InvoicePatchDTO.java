package org.goldstandard.assurance.api.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Getter
public class InvoicePatchDTO {
  private final @NotNull Integer invoiceStatusId;
  private final @NotNull BigDecimal amount;
}
