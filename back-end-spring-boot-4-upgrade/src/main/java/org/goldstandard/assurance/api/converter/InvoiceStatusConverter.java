package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.InvoiceStatusDTO;
import org.goldstandard.assurance.api.entity.lookup.InvoiceStatus;
import org.springframework.stereotype.Component;

@Component
public class InvoiceStatusConverter {

  public InvoiceStatusDTO toDTO(InvoiceStatus invoiceStatus) {
    return InvoiceStatusDTO.builder()
        .id(invoiceStatus.getId())
        .code(invoiceStatus.getCode())
        .description(invoiceStatus.getDescription())
        .build();
  }
}
